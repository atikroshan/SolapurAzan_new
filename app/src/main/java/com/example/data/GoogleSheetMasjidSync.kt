package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object GoogleSheetMasjidSync {
    const val DEFAULT_SHEET_URL = "https://docs.google.com/spreadsheets/d/13l1dJh64fyOnpHFlw81iWKHZlA5ko0JVWJ_qoF43k0g/export?format=csv"
    const val SHEET_EDIT_URL = "https://docs.google.com/spreadsheets/d/13l1dJh64fyOnpHFlw81iWKHZlA5ko0JVWJ_qoF43k0g/edit?usp=sharing"
    const val DRIVE_FOLDER_URL = "https://drive.google.com/drive/folders/1zEbq9A1LvGk-etUfVcMHUhKd6bqVH01n?usp=sharing"

    val DEFAULT_CSV_CONTENT = """
Masjid Name,Mohammadiya Masjid,,,,,
Address,Swagat Nagar,,,,,
ID,100111111,,,,,
Masjid Photo,https://drive.google.com/file/d/1QP6elu7nlZwmrxjG4p-eyZWOrEaEdiwo/view?usp=drive_link,,,,,
,Fajr,Zohar,Asr,Maghrib,Isha,Jummah
Azan,05:40,01:15,05:17,06:10,07:50,12:30
Jammat,06:15,01:30,05:30,06:12,07:59,01:30
Admin ID,admin,,,,,
Password,9960171516,,,,,
,,,,,,
Masjid Name,Hajrat Imam Hussain Masjid,,,,,
Address,Tai Chowk,,,,,
ID,100111112,,,,,
Masjid Photo,https://drive.google.com/file/d/1Wtt9FYQSWz2o0PpxuCtGJZjSKBOLNhjH/view?usp=drive_link,,,,,
,Fajr,Zohar,Asr,Maghrib,Isha,Jummah
Azan,05:42,01:15,05:18,06:11,07:55,12:30
Jammat,06:17,01:30,05:32,06:13,08:05,01:30
Admin ID,admin,,,,,
Password,9970595659,,,,,
,,,,,,
Masjid Name,Abu Bakar Siddique Masjid,,,,,
Address,Mumtaz Nagar,,,,,
ID,100111113,,,,,
Masjid Photo,https://drive.google.com/file/d/1wtzGogJQgbTLR1rHlTAocBeMdwMESBtD/view?usp=drive_link,,,,,
,Fajr,Zohar,Asr,Maghrib,Isha,Jummah
Azan,05:42,01:15,05:18,06:11,07:55,12:30
Jammat,06:17,01:30,05:32,06:13,08:05,01:30
Admin ID,admin,,,,,
Password,9371883412,,,,,
,,,,,,
Masjid Name,Hazrat Jang Bahadur Salabat Kha,,,,,
Address,"Bhayya Chowk, Railway Station Ground",,,,,
ID,100111114,,,,,
Masjid Photo,https://drive.google.com/file/d/1Ju4p3KkS6sc3vzSvf68Bm1panplC_g8r/view?usp=drive_link,,,,,
,Fajr,Zohar,Asr,Maghrib,Isha,Jummah
Azan,05:42,01:15,05:18,06:11,07:55,12:30
Jammat,06:17,01:30,05:32,06:13,08:05,01:30
Admin ID,admin,,,,,
Password,9595996629,,,,,
""".trimIndent()

    fun extractGoogleDriveDirectUrl(rawUrl: String): String {
        val clean = rawUrl.trim()
        if (clean.isBlank()) return ""
        val idRegex = "(?:/file/d/|/d/|id=)([-_a-zA-Z0-9]{20,})".toRegex()
        val match = idRegex.find(clean)?.groupValues?.getOrNull(1)
        if (match != null) {
            return "https://lh3.googleusercontent.com/d/$match=w1000"
        }
        val fallbackRegex = "[-_a-zA-Z0-9]{25,}".toRegex()
        val fallbackMatch = fallbackRegex.findAll(clean).lastOrNull()?.value
        return if (fallbackMatch != null) {
            "https://lh3.googleusercontent.com/d/$fallbackMatch=w1000"
        } else {
            clean
        }
    }

    fun to24Hr(timeStr: String, isPm: Boolean): String {
        val clean = timeStr.trim()
        if (!clean.contains(":")) return clean
        val parts = clean.split(":")
        var h = parts[0].toIntOrNull() ?: return clean
        val m = parts[1].toIntOrNull() ?: return clean
        if (h in 0..23 && (h >= 13 || (h == 12 && isPm))) {
            return String.format(Locale.US, "%02d:%02d", h, m)
        }
        if (isPm) {
            if (h in 1..11) {
                h += 12
            }
        } else {
            if (h == 12) {
                h = 0
            }
        }
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    fun formatForCsv(time24: String?): String {
        if (time24.isNullOrBlank() || !time24.contains(":")) return "00:00"
        val parts = time24.trim().split(":")
        val rawH = parts[0].toIntOrNull() ?: 0
        val m = parts[1].toIntOrNull() ?: 0
        val h12 = when {
            rawH == 0 -> 12
            rawH > 12 -> rawH - 12
            else -> rawH
        }
        return String.format(Locale.US, "%02d:%02d", h12, m)
    }

    var APPS_SCRIPT_WEBAPP_URL = "https://script.google.com/macros/s/AKfycbwNt9E_46DnmH_GJHZd16qBq0VpaN8GI9IKAQuwOaelfBKsWtXpiFlNTchF0rMgG4Lf/exec"

    val APPS_SCRIPT_SAMPLE_CODE = """
function doGet(e) {
  return handleRequest(e);
}

function doPost(e) {
  return handleRequest(e);
}

function handleRequest(e) {
  try {
    var p = e.parameter || {};
    var action = p.action || "ping";
    
    if (action === "ping") {
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        message: "Google Apps Script Azan Sync is active!"
      })).setMimeType(ContentService.MimeType.JSON);
    }
    
    if (action === "updateAll") {
      var id = (p.id || "").toString().trim();
      if (!id) {
        return ContentService.createTextOutput(JSON.stringify({
          status: "error",
          message: "Missing id parameter"
        })).setMimeType(ContentService.MimeType.JSON);
      }
      var ss = SpreadsheetApp.getActiveSpreadsheet();
      var sheet = ss.getActiveSheet();
      var data = sheet.getDataRange().getValues();
      var foundRow = -1;
      for (var r = 0; r < data.length; r++) {
        var firstCell = (data[r][0] || "").toString().trim();
        var secondCell = (data[r][1] || "").toString().trim();
        if (firstCell.toLowerCase() === "id" && secondCell === id) {
          foundRow = r;
          break;
        }
      }
      if (foundRow === -1) {
        return ContentService.createTextOutput(JSON.stringify({
          status: "error",
          message: "Masjid ID #" + id + " not found in sheet"
        })).setMimeType(ContentService.MimeType.JSON);
      }
      var azanRow = -1;
      var jammatRow = -1;
      for (var r = foundRow; r < Math.min(data.length, foundRow + 8); r++) {
        var tag = (data[r][0] || "").toString().trim().toLowerCase();
        if (tag === "azan") azanRow = r;
        if (tag === "jammat") jammatRow = r;
      }
      var prayers = [
        { key: "fajr", col: 1 },
        { key: "zohar", col: 2 },
        { key: "asr", col: 3 },
        { key: "maghrib", col: 4 },
        { key: "isha", col: 5 },
        { key: "jumah", col: 6 }
      ];
      for (var i = 0; i < prayers.length; i++) {
        var item = prayers[i];
        var a = p[item.key + "Azan"];
        var j = p[item.key + "Jammat"];
        if (azanRow !== -1 && a) sheet.getRange(azanRow + 1, item.col + 1).setValue(a);
        if (jammatRow !== -1 && j) sheet.getRange(jammatRow + 1, item.col + 1).setValue(j);
      }
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        message: "Updated all timings for Masjid #" + id
      })).setMimeType(ContentService.MimeType.JSON);
    }

    if (action === "update") {
      var id = (p.id || "").toString().trim();
      var prayer = (p.prayer || "").toString().trim().toLowerCase();
      var azan = (p.azan || "").toString().trim();
      var jammat = (p.jammat || "").toString().trim();
      
      if (!id || !prayer) {
        return ContentService.createTextOutput(JSON.stringify({
          status: "error",
          message: "Missing id or prayer parameter"
        })).setMimeType(ContentService.MimeType.JSON);
      }
      
      var ss = SpreadsheetApp.getActiveSpreadsheet();
      var sheet = ss.getActiveSheet();
      var data = sheet.getDataRange().getValues();
      
      var colMap = {
        "fajr": 1,
        "zohar": 2,
        "dhuhr": 2,
        "asr": 3,
        "maghrib": 4,
        "isha": 5,
        "jumah": 6,
        "jummah": 6,
        "jum'ah": 6
      };
      
      var targetCol = colMap[prayer];
      if (targetCol === undefined) {
        return ContentService.createTextOutput(JSON.stringify({
          status: "error",
          message: "Unknown prayer: " + prayer
        })).setMimeType(ContentService.MimeType.JSON);
      }
      
      // Find row where Column A is "ID" and Column B matches the masjid ID
      var foundRow = -1;
      for (var r = 0; r < data.length; r++) {
        var firstCell = (data[r][0] || "").toString().trim();
        var secondCell = (data[r][1] || "").toString().trim();
        if (firstCell.toLowerCase() === "id" && secondCell === id) {
          foundRow = r;
          break;
        }
      }
      
      if (foundRow === -1) {
        return ContentService.createTextOutput(JSON.stringify({
          status: "error",
          message: "Masjid ID #" + id + " not found in sheet"
        })).setMimeType(ContentService.MimeType.JSON);
      }
      
      // Find Azan and Jammat rows for this masjid block
      var azanRow = -1;
      var jammatRow = -1;
      for (var r = foundRow; r < Math.min(data.length, foundRow + 8); r++) {
        var tag = (data[r][0] || "").toString().trim().toLowerCase();
        if (tag === "azan") azanRow = r;
        if (tag === "jammat") jammatRow = r;
      }
      
      if (azanRow !== -1 && azan) {
        sheet.getRange(azanRow + 1, targetCol + 1).setValue(azan);
      }
      if (jammatRow !== -1 && jammat) {
        sheet.getRange(jammatRow + 1, targetCol + 1).setValue(jammat);
      }
      
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        message: "Updated Masjid #" + id + " " + prayer + ": Azan " + azan + ", Jammat " + jammat
      })).setMimeType(ContentService.MimeType.JSON);
    }
    
    if (action === "addRating") {
      var ss = SpreadsheetApp.getActiveSpreadsheet();
      var rSheet = ss.getSheetByName("rating") || ss.insertSheet("rating");
      var stars = parseInt(p.stars || "5", 10);
      var lastRow = rSheet.getLastRow();
      var nextId = lastRow <= 1 ? 1 : lastRow;
      rSheet.appendRow([nextId, stars]);
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        message: "Rating of " + stars + " stars recorded!"
      })).setMimeType(ContentService.MimeType.JSON);
    }

    if (action === "requestMasjid") {
      var name = (p.name || "").toString().trim();
      var loc = (p.location || "").toString().trim();
      var admin = (p.admin || "").toString().trim();
      var contact = (p.contact || "").toString().trim();
      
      try {
        MailApp.sendEmail({
          to: "atikroshan@gmail.com",
          subject: "New Masjid Request: " + name + " (" + loc + ")",
          body: "New Masjid Submission:\n\nMasjid Name: " + name + "\nLocation: " + loc + "\nAdmin: " + admin + "\nContact: " + contact
        });
      } catch (mailErr) {}
      
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        message: "Masjid request received"
      })).setMimeType(ContentService.MimeType.JSON);
    }
    
    return ContentService.createTextOutput(JSON.stringify({
      status: "error",
      message: "Unknown action: " + action
    })).setMimeType(ContentService.MimeType.JSON);
  } catch (err) {
    return ContentService.createTextOutput(JSON.stringify({
      status: "error",
      message: err.toString()
    })).setMimeType(ContentService.MimeType.JSON);
  }
}
    """.trimIndent()

    suspend fun updateAllMasjidTimings(
        masjid: MasjidItem,
        webAppUrl: String? = null
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val targetUrl = webAppUrl?.trim()?.ifBlank { null } ?: APPS_SCRIPT_WEBAPP_URL.trim().ifBlank { null }
        if (targetUrl == null) {
            return@withContext Pair(false, "Apps Script WebApp URL not configured.")
        }
        try {
            val params = mapOf(
                "action" to "updateAll",
                "id" to masjid.id,
                "fajrAzan" to formatForCsv(masjid.fajrAzanFixed ?: "05:40"),
                "fajrJammat" to formatForCsv(masjid.fajrJammatFixed ?: "06:15"),
                "zoharAzan" to formatForCsv(masjid.zoharAzanFixed ?: "13:15"),
                "zoharJammat" to formatForCsv(masjid.zoharJammatFixed ?: "13:30"),
                "asrAzan" to formatForCsv(masjid.asrAzanFixed ?: "17:17"),
                "asrJammat" to formatForCsv(masjid.asrJammatFixed ?: "17:30"),
                "maghribAzan" to formatForCsv(masjid.maghribAzanFixed ?: "18:10"),
                "maghribJammat" to formatForCsv(masjid.maghribJammatFixed ?: "18:12"),
                "ishaAzan" to formatForCsv(masjid.ishaAzanFixed ?: "19:50"),
                "ishaJammat" to formatForCsv(masjid.ishaJammatFixed ?: "19:59"),
                "jumahAzan" to formatForCsv(masjid.jumahAzanTime.ifBlank { "12:30" }),
                "jumahJammat" to formatForCsv(masjid.jumahJammatTime.ifBlank { "13:30" })
            )
            val query = params.entries.joinToString("&") { (k, v) ->
                "${java.net.URLEncoder.encode(k, "UTF-8")}=${java.net.URLEncoder.encode(v, "UTF-8")}"
            }
            var currentUrl = if (targetUrl.contains("?")) "$targetUrl&$query" else "$targetUrl?$query"

            var redirectCount = 0
            var finalCode = 0
            var responseBody = ""

            while (redirectCount < 6) {
                val url = URL(currentUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 15000
                    readTimeout = 15000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "AzanTimeApp/2.6")
                }
                finalCode = conn.responseCode
                if (finalCode in listOf(301, 302, 303, 307, 308)) {
                    val location = conn.getHeaderField("Location")
                    if (!location.isNullOrBlank()) {
                        currentUrl = location
                        redirectCount++
                        continue
                    }
                }

                responseBody = try {
                    val stream = if (finalCode in 200..299) conn.inputStream else conn.errorStream
                    stream?.bufferedReader()?.use { it.readText() } ?: ""
                } catch (e: Exception) {
                    ""
                }
                break
            }

            if (finalCode in 200..299) {
                if (responseBody.contains("\"error\"") && responseBody.contains("Unknown action")) {
                    Pair(false, "OLD_SCRIPT_FORMAT")
                } else {
                    Pair(true, if (responseBody.isNotBlank()) responseBody.take(120) else "Google Sheet updated successfully")
                }
            } else {
                Pair(false, "Google Sheet sync failed (HTTP $finalCode): ${responseBody.take(100)}")
            }
        } catch (e: Exception) {
            Pair(false, "Sync network error: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun updateRemoteGoogleSheet(
        masjidId: String,
        prayerName: String,
        azanTime: String,
        jammatTime: String,
        webAppUrl: String? = null
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val targetUrl = webAppUrl?.trim()?.ifBlank { null } ?: APPS_SCRIPT_WEBAPP_URL.trim().ifBlank { null }
        if (targetUrl == null) {
            return@withContext Pair(false, "Apps Script WebApp URL not configured. Configure in Admin Panel.")
        }
        try {
            val encodedPrayer = java.net.URLEncoder.encode(prayerName, "UTF-8")
            val encodedAzan = java.net.URLEncoder.encode(formatForCsv(azanTime), "UTF-8")
            val encodedJammat = java.net.URLEncoder.encode(formatForCsv(jammatTime), "UTF-8")
            var currentUrl = if (targetUrl.contains("?")) {
                "$targetUrl&action=update&id=$masjidId&prayer=$encodedPrayer&azan=$encodedAzan&jammat=$encodedJammat"
            } else {
                "$targetUrl?action=update&id=$masjidId&prayer=$encodedPrayer&azan=$encodedAzan&jammat=$encodedJammat"
            }

            var redirectCount = 0
            var finalCode = 0
            var responseBody = ""

            while (redirectCount < 6) {
                val url = URL(currentUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 12000
                    readTimeout = 12000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "AzanTimeApp/2.6")
                }
                finalCode = conn.responseCode
                if (finalCode in listOf(301, 302, 303, 307, 308)) {
                    val location = conn.getHeaderField("Location")
                    if (!location.isNullOrBlank()) {
                        currentUrl = location
                        redirectCount++
                        continue
                    }
                }

                responseBody = try {
                    val stream = if (finalCode in 200..299) conn.inputStream else conn.errorStream
                    stream?.bufferedReader()?.use { it.readText() } ?: ""
                } catch (e: Exception) {
                    ""
                }
                break
            }

            if (finalCode in 200..299) {
                Pair(true, if (responseBody.isNotBlank()) responseBody.take(80) else "Google Sheet updated successfully")
            } else {
                Pair(false, "Google Sheet sync failed (HTTP $finalCode): ${responseBody.take(100)}")
            }
        } catch (e: Exception) {
            Pair(false, "Sync network error: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun testAppsScriptConnection(rawUrl: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val clean = rawUrl.trim()
        if (clean.isBlank()) return@withContext Pair(false, "URL cannot be empty")
        try {
            var currentUrl = if (clean.contains("?")) "$clean&action=ping" else "$clean?action=ping"
            var redirectCount = 0
            var finalCode = 0
            var responseBody = ""

            while (redirectCount < 6) {
                val url = URL(currentUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 12000
                    readTimeout = 12000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "AzanTimeApp/2.6")
                }
                finalCode = conn.responseCode
                if (finalCode in listOf(301, 302, 303, 307, 308)) {
                    val location = conn.getHeaderField("Location")
                    if (!location.isNullOrBlank()) {
                        currentUrl = location
                        redirectCount++
                        continue
                    }
                }

                responseBody = try {
                    val stream = if (finalCode in 200..299) conn.inputStream else conn.errorStream
                    stream?.bufferedReader()?.use { it.readText() } ?: ""
                } catch (e: Exception) {
                    ""
                }
                break
            }

            if (finalCode in 200..299) {
                Pair(true, "Connected! Server responded: ${responseBody.take(100)}")
            } else {
                Pair(false, "Server returned HTTP $finalCode: ${responseBody.take(120)}")
            }
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun fetchCsv(urlStr: String = DEFAULT_SHEET_URL): String = withContext(Dispatchers.IO) {
        val url = URL(urlStr)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            instanceFollowRedirects = true
        }
        BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
            reader.readText()
        }
    }

    fun buildCsv(masajid: List<MasjidItem>): String {
        val sb = StringBuilder()
        for (m in masajid) {
            sb.append("Masjid Name,${m.name},,,,,\n")
            sb.append("Address,${m.area},,,,,\n")
            sb.append("ID,${m.id},,,,,\n")
            val photoLink = if (m.photoUrl.isNotBlank()) m.photoUrl else ""
            sb.append("Masjid Photo,$photoLink,,,,,\n")
            sb.append(",Fajr,Zohar,Asr,Maghrib,Isha,Jummah\n")

            val fAzan = formatForCsv(m.fajrAzanFixed ?: "05:40")
            val zAzan = formatForCsv(m.zoharAzanFixed ?: "13:15")
            val aAzan = formatForCsv(m.asrAzanFixed ?: "17:17")
            val mAzan = formatForCsv(m.maghribAzanFixed ?: "18:10")
            val iAzan = formatForCsv(m.ishaAzanFixed ?: "19:50")
            val jAzan = formatForCsv(m.jumahAzanTime)
            sb.append("Azan,$fAzan,$zAzan,$aAzan,$mAzan,$iAzan,$jAzan\n")

            val fJammat = formatForCsv(m.fajrJammatFixed ?: "06:15")
            val zJammat = formatForCsv(m.zoharJammatFixed ?: "13:30")
            val aJammat = formatForCsv(m.asrJammatFixed ?: "17:30")
            val mJammat = formatForCsv(m.maghribJammatFixed ?: "18:12")
            val iJammat = formatForCsv(m.ishaJammatFixed ?: "19:59")
            val jJammat = formatForCsv(m.jumahJammatTime)
            sb.append("Jammat,$fJammat,$zJammat,$aJammat,$mJammat,$iJammat,$jJammat\n")
            val admId = if (m.adminId.isNotBlank()) m.adminId else "admin"
            sb.append("Admin ID,$admId,,,,,\n")
            sb.append("Password,${m.adminPassword},,,,,\n")
            sb.append(",,,,,,\n")
        }
        return sb.toString()
    }

    fun parseCsv(csvText: String): List<MasjidItem> {
        val lines = csvText.lines()
        val list = mutableListOf<MasjidItem>()

        var currentName = ""
        var currentAddress = ""
        var currentId = ""
        var currentPhoto = ""
        var currentAdminId = "admin"
        var currentPassword = ""
        var azanTimes = listOf<String>()
        var jammatTimes = listOf<String>()

        fun flushCurrent() {
            if (currentName.isNotBlank() && currentId.isNotBlank()) {
                // Azan: Fajr (AM), Zohar (PM), Asr (PM), Maghrib (PM), Isha (PM), Jummah (12:30 PM)
                val fAzan = azanTimes.getOrNull(0)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, false) } ?: "05:40"
                val zAzan = azanTimes.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "13:15"
                val aAzan = azanTimes.getOrNull(2)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "17:17"
                val mAzan = azanTimes.getOrNull(3)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "18:10"
                val iAzan = azanTimes.getOrNull(4)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "19:50"
                val jAzan = azanTimes.getOrNull(5)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "12:30"

                // Jammat: Fajr (AM), Zohar (PM), Asr (PM), Maghrib (PM), Isha (PM), Jummah (PM)
                val fJammat = jammatTimes.getOrNull(0)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, false) } ?: "06:15"
                val zJammat = jammatTimes.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "13:30"
                val aJammat = jammatTimes.getOrNull(2)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "17:30"
                val mJammat = jammatTimes.getOrNull(3)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "18:12"
                val iJammat = jammatTimes.getOrNull(4)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "19:59"
                val jJammat = jammatTimes.getOrNull(5)?.trim()?.takeIf { it.isNotEmpty() }?.let { to24Hr(it, true) } ?: "13:30"

                val directPhoto = extractGoogleDriveDirectUrl(currentPhoto)

                val masjidItem = MasjidItem(
                    id = currentId.trim(),
                    name = currentName.trim(),
                    area = currentAddress.trim(),
                    city = "Solapur",
                    state = "Maharashtra",
                    photoUrl = directPhoto,
                    jumahAzanTime = jAzan,
                    jumahJammatTime = jJammat,
                    fajrAzanFixed = fAzan,
                    fajrJammatFixed = fJammat,
                    zoharAzanFixed = zAzan,
                    zoharJammatFixed = zJammat,
                    asrAzanFixed = aAzan,
                    asrJammatFixed = aJammat,
                    maghribAzanFixed = mAzan,
                    maghribJammatFixed = mJammat,
                    ishaAzanFixed = iAzan,
                    ishaJammatFixed = iJammat,
                    adminId = currentAdminId.ifBlank { "admin" },
                    adminPassword = currentPassword
                )
                list.add(masjidItem)
            }
            currentName = ""
            currentAddress = ""
            currentId = ""
            currentPhoto = ""
            currentAdminId = "admin"
            currentPassword = ""
            azanTimes = emptyList()
            jammatTimes = emptyList()
        }

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            val tokens = line.split(",").map { it.trim() }
            if (tokens.isEmpty()) continue

            val first = tokens[0]
            if (first.equals("Masjid Name", ignoreCase = true)) {
                if (currentName.isNotBlank() && currentId.isNotBlank()) {
                    flushCurrent()
                }
                currentName = tokens.getOrElse(1) { "" }
            } else if (first.equals("Address", ignoreCase = true)) {
                currentAddress = tokens.getOrElse(1) { "" }
            } else if (first.equals("ID", ignoreCase = true)) {
                currentId = tokens.getOrElse(1) { "" }
            } else if (first.equals("Masjid Photo", ignoreCase = true)) {
                currentPhoto = tokens.getOrElse(1) { "" }
            } else if (first.equals("Azan", ignoreCase = true)) {
                azanTimes = tokens.drop(1)
            } else if (first.equals("Jammat", ignoreCase = true)) {
                jammatTimes = tokens.drop(1)
            } else if (first.equals("Admin ID", ignoreCase = true) || first.equals("AdminID", ignoreCase = true)) {
                currentAdminId = tokens.getOrElse(1) { "admin" }
            } else if (first.equals("Password", ignoreCase = true)) {
                currentPassword = tokens.getOrElse(1) { "" }
            }
        }
        flushCurrent()
        return list
    }

    const val RATING_SHEET_URL = "https://docs.google.com/spreadsheets/d/13l1dJh64fyOnpHFlw81iWKHZlA5ko0JVWJ_qoF43k0g/gviz/tq?tqx=out:csv&sheet=rating"

    suspend fun fetchRatingStats(): Pair<Double, Int> = withContext(Dispatchers.IO) {
        try {
            val url = URL(RATING_SHEET_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", "AzanTimeApp/2.6")
            }
            if (conn.responseCode == 200) {
                val csv = conn.inputStream.bufferedReader().use { it.readText() }
                val lines = csv.lines().map { it.trim().trim('"') }.filter { it.isNotBlank() }
                val starsList = mutableListOf<Int>()
                for (line in lines.drop(1)) {
                    val parts = line.split(",").map { it.trim().trim('"') }
                    if (parts.size >= 2) {
                        val s = parts[1].toIntOrNull() ?: parts[0].toIntOrNull()
                        if (s != null && s in 1..5) starsList.add(s)
                    } else if (parts.size == 1) {
                        val s = parts[0].toIntOrNull()
                        if (s != null && s in 1..5) starsList.add(s)
                    }
                }
                if (starsList.isNotEmpty()) {
                    val avg = starsList.average()
                    return@withContext Pair(avg, starsList.size)
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        Pair(4.8, 0)
    }

    suspend fun submitRating(stars: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val query = "action=addRating&stars=$stars"
            val targetUrl = if (APPS_SCRIPT_WEBAPP_URL.contains("?")) "$APPS_SCRIPT_WEBAPP_URL&$query" else "$APPS_SCRIPT_WEBAPP_URL?$query"
            val url = URL(targetUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = true
            }
            conn.responseCode in 200..399
        } catch (e: Exception) {
            false
        }
    }

    suspend fun submitMasjidRequestAuto(
        name: String,
        location: String,
        adminName: String,
        contact: String
    ): Boolean = withContext(Dispatchers.IO) {
        var sent = false
        // 1. Send via formsubmit.co direct to atikroshan@gmail.com
        try {
            val json = """
                {
                    "name": "${name.replace("\"", "\\\"")}",
                    "location": "${location.replace("\"", "\\\"")}",
                    "admin": "${adminName.replace("\"", "\\\"")}",
                    "contact": "${contact.replace("\"", "\\\"")}",
                    "message": "New Masjid submission for Azan Time Solapur"
                }
            """.trimIndent()
            val url = URL("https://formsubmit.co/ajax/atikroshan@gmail.com")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Origin", "https://azan-app.web.app")
                setRequestProperty("Referer", "https://azan-app.web.app")
                connectTimeout = 10000
                readTimeout = 10000
            }
            conn.outputStream.use { os ->
                os.write(json.toByteArray(Charsets.UTF_8))
            }
            if (conn.responseCode in 200..299) sent = true
        } catch (e: Exception) {
            // Fallback
        }

        // 2. Also send to Google Apps Script
        try {
            val params = mapOf(
                "action" to "requestMasjid",
                "name" to name,
                "location" to location,
                "admin" to adminName,
                "contact" to contact
            )
            val q = params.entries.joinToString("&") { (k, v) ->
                "${java.net.URLEncoder.encode(k, "UTF-8")}=${java.net.URLEncoder.encode(v, "UTF-8")}"
            }
            val targetUrl = if (APPS_SCRIPT_WEBAPP_URL.contains("?")) "$APPS_SCRIPT_WEBAPP_URL&$q" else "$APPS_SCRIPT_WEBAPP_URL?$q"
            val conn = (URL(targetUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = true
            }
            if (conn.responseCode in 200..399) sent = true
        } catch (e: Exception) {
            // Fallback
        }

        true
    }
}
