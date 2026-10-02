package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.example.data.GoogleSheetMasjidSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersionName: String,
    val latestVersionCode: Int,
    val downloadUrl: String,
    val changelog: String = ""
)

object AppUpdateManager {

    var GITHUB_REPO_OWNER = "atikroshan"
    var GITHUB_REPO_NAME = "azan-app"

    /**
     * Checks if a new version is available from GitHub Releases or Google Apps Script.
     */
    suspend fun checkForUpdate(
        currentVersionCode: Int = BuildConfig.VERSION_CODE,
        currentVersionName: String = BuildConfig.VERSION_NAME
    ): UpdateInfo? = withContext(Dispatchers.IO) {
        // 1. Try Google Apps Script / Google Sheet first (fastest and directly controlled by owner)
        try {
            val scriptUrl = "${GoogleSheetMasjidSync.APPS_SCRIPT_WEBAPP_URL}?action=checkUpdate"
            val conn = (URL(scriptUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                instanceFollowRedirects = true
            }
            if (conn.responseCode in 200..399) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                if (body.contains("latestVersion")) {
                    val json = JSONObject(body)
                    val remoteVersionName = json.optString("latestVersionName", json.optString("latestVersion", ""))
                    val remoteVersionCode = json.optInt("latestVersionCode", 0)
                    val downloadUrl = json.optString("downloadUrl", "")
                    val changelog = json.optString("changelog", "Bug fixes and improvements")

                    val isNewer = isVersionNewer(remoteVersionName, currentVersionName) || (remoteVersionCode > currentVersionCode)
                    if (isNewer && downloadUrl.isNotBlank()) {
                        return@withContext UpdateInfo(
                            hasUpdate = true,
                            latestVersionName = remoteVersionName.ifBlank { "v$remoteVersionCode" },
                            latestVersionCode = remoteVersionCode,
                            downloadUrl = downloadUrl,
                            changelog = changelog
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore, proceed to GitHub
        }

        // 2. Try GitHub Releases API
        try {
            val githubApiUrl = "https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest"
            val conn = (URL(githubApiUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "AzanTimeApp")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                connectTimeout = 6000
                readTimeout = 6000
            }
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val tagName = json.optString("tag_name", "").removePrefix("v").trim()
                val body = json.optString("body", "New features and prayer timetable updates")

                // Find APK asset
                var apkUrl = ""
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkUrl = asset.optString("browser_download_url", "")
                            break
                        }
                    }
                }

                if (apkUrl.isBlank()) {
                    apkUrl = json.optString("html_url", "")
                }

                val isNewer = isVersionNewer(tagName, currentVersionName)
                if (isNewer && apkUrl.isNotBlank()) {
                    return@withContext UpdateInfo(
                        hasUpdate = true,
                        latestVersionName = tagName,
                        latestVersionCode = currentVersionCode + 1,
                        downloadUrl = apkUrl,
                        changelog = body
                    )
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        null
    }

    /**
     * Compares semver strings (e.g. "2.6.16" > "2.6.15")
     */
    fun isVersionNewer(remoteVersion: String, currentVersion: String): Boolean {
        if (remoteVersion.isBlank()) return false
        val rClean = remoteVersion.removePrefix("v").trim()
        val cClean = currentVersion.removePrefix("v").trim()
        if (rClean == cClean) return false

        val rParts = rClean.split(".").mapNotNull { it.toIntOrNull() }
        val cParts = cClean.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(rParts.size, cParts.size)
        for (i in 0 until maxLen) {
            val rVal = rParts.getOrElse(i) { 0 }
            val cVal = cParts.getOrElse(i) { 0 }
            if (rVal > cVal) return true
            if (rVal < cVal) return false
        }
        return false
    }

    /**
     * Downloads APK from URL directly into local cache with real-time percentage progress.
     */
    suspend fun downloadApk(
        downloadUrl: String,
        destinationFile: File,
        onProgress: (Int) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            var currentUrl = downloadUrl
            var redirectCount = 0

            // Follow redirects up to 5 times (GitHub releases redirect to AWS S3)
            while (redirectCount < 6) {
                val conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "Mozilla/5.0 AzanApp")
                    instanceFollowRedirects = false
                    connectTimeout = 15000
                    readTimeout = 30000
                }
                val code = conn.responseCode
                if (code in listOf(301, 302, 303, 307, 308)) {
                    val location = conn.getHeaderField("Location")
                    if (!location.isNullOrBlank()) {
                        currentUrl = location
                        redirectCount++
                        continue
                    }
                }

                if (code == 200) {
                    val contentLength = conn.contentLength
                    var totalBytesRead: Long = 0

                    if (destinationFile.exists()) destinationFile.delete()
                    destinationFile.parentFile?.mkdirs()

                    conn.inputStream.use { input ->
                        FileOutputStream(destinationFile).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                                if (contentLength > 0) {
                                    val percent = ((totalBytesRead * 100) / contentLength).toInt()
                                    onProgress(percent.coerceIn(0, 100))
                                }
                            }
                            output.flush()
                        }
                    }
                    onProgress(100)
                    return@withContext true
                } else {
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        false
    }

    /**
     * Prompts the native Android package installer to install the downloaded APK.
     */
    fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists() || apkFile.length() == 0L) {
            Toast.makeText(context, "Download failed or APK file empty", Toast.LENGTH_SHORT).show()
            return
        }

        // On Android 8.0+ (Oreo), verify Install Unknown Apps permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(
                    context,
                    "Please allow 'Install unknown apps' permission to update",
                    Toast.LENGTH_LONG
                ).show()
                val permIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(permIntent)
                return
            }
        }

        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open installer: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
