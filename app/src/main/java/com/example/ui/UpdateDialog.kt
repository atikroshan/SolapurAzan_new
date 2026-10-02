package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AppUpdateManager
import com.example.util.UpdateInfo
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    language: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val primaryGold = Color(0xFFF3DE8E)

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableIntStateOf(0) }
    var downloadError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = {
        if (!isDownloading) onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, primaryGold.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(primaryGold.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, primaryGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "Update Available",
                        tint = primaryGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title
                Text(
                    text = when (language) {
                        "ur" -> "نیا اپ ڈیٹ دستیاب ہے (v${updateInfo.latestVersionName})"
                        "hi" -> "नया अपडेट उपलब्ध है (v${updateInfo.latestVersionName})"
                        else -> "Update Available (v${updateInfo.latestVersionName})"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGold,
                    textAlign = TextAlign.Center
                )

                // Subtitle / Description
                Text(
                    text = if (updateInfo.changelog.isNotBlank()) {
                        updateInfo.changelog
                    } else {
                        when (language) {
                            "ur" -> "ایپ کا نیا ورژن دستیاب ہے۔ بہتر کارکردگی اور اوقات کے لیے ابھی اپ ڈیٹ کریں۔"
                            "hi" -> "ऐप का नया वर्जन उपलब्ध है। बेहतर प्रदर्शन और नए फीचर्स के लिए अभी अपडेट करें।"
                            else -> "A new version of Azan Time Solapur is available with improvements and timetable updates."
                        }
                    },
                    fontSize = 12.5.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                if (isDownloading) {
                    // Progress Bar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF10B981),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "Downloading... $downloadProgress%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                if (downloadError != null) {
                    Text(
                        text = downloadError ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Actions: Update and Cancel
                if (!isDownloading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = when (language) {
                                    "ur" -> "منسوخ"
                                    "hi" -> "रद्द करें"
                                    else -> "Cancel"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Update Button
                        Button(
                            onClick = {
                                isDownloading = true
                                downloadError = null
                                coroutineScope.launch {
                                    val destination = File(context.cacheDir, "AzanTime_v${updateInfo.latestVersionName}.apk")
                                    val success = AppUpdateManager.downloadApk(
                                        downloadUrl = updateInfo.downloadUrl,
                                        destinationFile = destination,
                                        onProgress = { percent ->
                                            downloadProgress = percent
                                        }
                                    )
                                    isDownloading = false
                                    if (success) {
                                        onDismiss()
                                        AppUpdateManager.installApk(context, destination)
                                    } else {
                                        downloadError = "Download failed. Please check internet connection."
                                    }
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text(
                                text = when (language) {
                                    "ur" -> "اپ ڈیٹ کریں"
                                    "hi" -> "अपडेट करें"
                                    else -> "Update"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
