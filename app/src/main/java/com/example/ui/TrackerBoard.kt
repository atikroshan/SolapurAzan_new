package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.AzanTiming
import com.example.data.GoogleSheetMasjidSync
import kotlinx.coroutines.launch
import com.example.BuildConfig
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.PrayerLog
import com.example.ui.theme.TextMuted
import java.util.Calendar
import com.example.appBackground

typealias TrackerStrings = com.example.ui.theme.AppStrings

fun getMaxDaysOfMonth(month: Int): Int {
    return when (month) {
        1 -> 31
        2 -> 28
        3 -> 31
        4 -> 30
        5 -> 31
        6 -> 30
        7 -> 31
        8 -> 31
        9 -> 30
        10 -> 31
        11 -> 30
        12 -> 31
        else -> 30
    }
}

fun calculateStreaks(allLogs: List<PrayerLog>): Pair<Int, Int> {
    val perfectDays = allLogs.filter { it.isPerfectDay() }
        .map { "${it.month}-${it.day}" }
        .toSet()

    var longest = 0
    var currentCount = 0

    for (m in 1..12) {
        val maxDays = getMaxDaysOfMonth(m)
        for (d in 1..maxDays) {
            val key = "$m-$d"
            if (perfectDays.contains(key)) {
                currentCount++
                if (currentCount > longest) {
                    longest = currentCount
                }
            } else {
                currentCount = 0
            }
        }
    }

    var currentStreak = 0
    val tracker = Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata"))
    var isChecking = true
    var daysBack = 0

    while (isChecking && daysBack < 366) {
        val m = tracker.get(Calendar.MONTH) + 1
        val d = tracker.get(Calendar.DAY_OF_MONTH)
        val key = "$m-$d"

        if (perfectDays.contains(key)) {
            currentStreak++
            tracker.add(Calendar.DAY_OF_YEAR, -1)
            daysBack++
        } else {
            if (daysBack == 0) {
                tracker.add(Calendar.DAY_OF_YEAR, -1)
                daysBack++
            } else {
                isChecking = false
            }
        }
    }

    return Pair(currentStreak, longest)
}

data class RamazanSchedule(
    val id: Int,
    val cal: java.util.Calendar,
    val dateStr: String,
    val dayStr: String,
    val isFriday: Boolean,
    val seharTime: String,
    val iftarTime: String
)

fun getRamazan2027Schedule(): List<RamazanSchedule> {
    val list = mutableListOf<RamazanSchedule>()
    val startCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
        set(java.util.Calendar.YEAR, 2027)
        set(java.util.Calendar.MONTH, java.util.Calendar.FEBRUARY) // 1 = Feb
        set(java.util.Calendar.DAY_OF_MONTH, 9)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    
    val dateFormat = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.ENGLISH)
    val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.ENGLISH)

    // 30 days starting from 9 Feb 2027 (Tuesday) to 10 March 2027
    for (i in 0 until 30) {
        val currentCal = startCal.clone() as java.util.Calendar
        currentCal.add(java.util.Calendar.DAY_OF_YEAR, i)
        
        // Slightly adjusting times over the 30 days for realism
        val sMin = 30 - (i / 2) 
        val iMin = 25 + (i / 3) 
        
        val seharStr = String.format(java.util.Locale.US, "05:%02d AM", sMin)
        val iftarStr = String.format(java.util.Locale.US, "06:%02d PM", iMin)

        list.add(
            RamazanSchedule(
                id = i,
                cal = currentCal,
                dateStr = dateFormat.format(currentCal.time),
                dayStr = dayFormat.format(currentCal.time),
                isFriday = currentCal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY,
                seharTime = seharStr,
                iftarTime = iftarStr
            )
        )
    }
    return list
}

@Composable
fun TrackerBoardDialog(
    uiState: UIState,
    onDateSelected: (Int, Int) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onTogglePrayer: (Int, Int, String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit,
    onRestorePoints: (Int) -> Unit = {}
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        TrackerBoardContent(
            uiState = uiState,
            onDateSelected = onDateSelected,
            onLanguageSelect = onLanguageSelect,
            onTogglePrayer = onTogglePrayer,
            onBack = onDismiss,
            onRestorePoints = onRestorePoints
        )
    }
}

@Composable
fun TrackerBoardContent(
    uiState: UIState,
    onDateSelected: (Int, Int) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onTogglePrayer: (Int, Int, String) -> Unit = { _, _, _ -> },
    onBack: (() -> Unit)? = null,
    onRestorePoints: (Int) -> Unit = {},
    onBackupTaqwa: ((String, (Boolean, String) -> Unit) -> Unit)? = null,
    onRestoreTaqwa: ((String, (Boolean, Int, String) -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedDayInfo by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showRestorePointsDialog by remember { mutableStateOf(false) }

    val curStrings = when (uiState.language) {
        "hi" -> com.example.ui.theme.HindiStrings
        "ur" -> com.example.ui.theme.UrduStrings
        else -> com.example.ui.theme.EnglishStrings
    }

    val totalPoints = remember(uiState.allLogs, uiState.restoredTaqwaPoints) { 
        uiState.restoredTaqwaPoints + uiState.allLogs.sumOf { it.getFivePrayersCount() } 
    }

    val currentHijriYear = remember(uiState.selectedDate) {
        try {
            val y = uiState.selectedDate.get(java.util.Calendar.YEAR)
            val m = uiState.selectedDate.get(java.util.Calendar.MONTH) + 1
            val d = uiState.selectedDate.get(java.util.Calendar.DAY_OF_MONTH)
            val localDate = java.time.LocalDate.of(y, m, d)
            val hijrahDate = java.time.chrono.HijrahDate.from(localDate)
            hijrahDate.get(java.time.temporal.ChronoField.YEAR)
        } catch (e: Exception) {
            1448
        }
    }

    CompositionLocalProvider(com.example.ui.theme.LocalAppStrings provides curStrings) {
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Sticky Header & Language Toggle (Always visible at the top while scrolling)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Top Left Language Circles (E, ह, ا)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.LanguageCirclesRow(
                            currentLang = uiState.language,
                            onLangSelect = onLanguageSelect
                        )
                    }

                    // Unified Consistent App Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = curStrings.solapur,
                            fontSize = if (curStrings.solapur == "SOLAPUR") 10.sp else 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = if (curStrings.solapur == "SOLAPUR") 5.sp else 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = if (uiState.language == "ur") curStrings.azanTimeHeader else "AZAN TIME",
                            fontSize = if (uiState.language == "ur") 22.sp else 30.sp,
                            fontFamily = if (uiState.language == "ur") null else GraenMetalFontFamily,
                            fontWeight = FontWeight.Normal,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = GraenMetalGoldGradient,
                                shadow = GraenMetalGoldShadow
                            ),
                            letterSpacing = if (uiState.language == "ur") 0.sp else 1.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (curStrings.punchLine == "Come towards the path of Allah") curStrings.punchLine.uppercase() else curStrings.punchLine,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = if (uiState.language == "ur") 0.sp else 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = curStrings.title.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                        textAlign = TextAlign.Center
                    )

                // Header card for stats: Total Points + Restore Previous Points
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1524)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color(0xFFE5B842).copy(alpha = 0.5f), Color(0xFF10B981).copy(alpha = 0.3f))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.verticalGradient(listOf(Color(0xFF131D32), Color(0xFF0B111D))))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                listOf(Color(0xFFFBBF24).copy(alpha = 0.25f), Color(0xFF131D32))
                                            ),
                                            shape = CircleShape
                                        )
                                        .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = curStrings.points.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "$totalPoints ${curStrings.ptsUnit}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        style = androidx.compose.ui.text.TextStyle(
                                            brush = Brush.verticalGradient(
                                                listOf(Color(0xFFF3DE8E), Color(0xFFE5B842))
                                            )
                                        )
                                    )
                                    Text(
                                        text = curStrings.ptsPerPrayerSubtitle,
                                        fontSize = 9.5.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = when (uiState.language) {
                                            "ur" -> "تقویٰ پوائنٹس ہر 1 محرم سے شروع ہو کر مکمل سال جاری رہتے ہیں • "
                                            "hi" -> "सालाना तक़वा अंक हर 1 मुहर्रम से शुरू होकर पूरे साल चलते हैं • "
                                            else -> "Annual Taqwa points start from every 1st Muharram for the complete year • "
                                        },
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF86EFAC),
                                        maxLines = 1,
                                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                                    )
                                }
                            }

                            // Restore Previous Points Button
                            OutlinedButton(
                                onClick = { showRestorePointsDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFF3DE8E).copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF1E293B)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color(0xFFF3DE8E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = when (uiState.language) {
                                            "ur" -> "پوائنٹس بحال کریں"
                                            "hi" -> "अंक रीस्टोर करें"
                                            else -> "Restore Points"
                                        },
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF3DE8E)
                                    )
                                }
                            }
                        }

                        if (uiState.restoredTaqwaPoints > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "سابقہ بحال شدہ: +${uiState.restoredTaqwaPoints} پوائنٹس"
                                        "hi" -> "पुराने रीस्टोर किए गए: +${uiState.restoredTaqwaPoints} अंक"
                                        else -> "Previous Restored: +${uiState.restoredTaqwaPoints} pts"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFF86EFAC),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "تبدیل کریں"
                                        "hi" -> "बदलें"
                                        else -> "Edit"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFFF3DE8E),
                                    modifier = Modifier.clickable { showRestorePointsDialog = true },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (showRestorePointsDialog) {
                    var inputPoints by remember { mutableStateOf(if (uiState.restoredTaqwaPoints > 0) uiState.restoredTaqwaPoints.toString() else "") }
                    var syncUserId by remember { mutableStateOf(uiState.taqwaSyncUser.ifBlank { "" }) }
                    var isSyncing by remember { mutableStateOf(false) }
                    var syncStatusMsg by remember { mutableStateOf<String?>(null) }
                    var syncIsSuccess by remember { mutableStateOf(true) }
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    
                    AlertDialog(
                        onDismissRequest = { showRestorePointsDialog = false },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFFF3DE8E),
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "تقویٰ پوائنٹس کلاؤڈ / ڈرائیو بیک اپ و بحالی"
                                        "hi" -> "तक़वा अंक ड्राइव / क्लाउड बैकअप व रीस्टोर"
                                        else -> "Taqwa Points Cloud / Drive Sync"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF3DE8E)
                                )
                            }
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                // 1. Cloud & Drive Sync Info Banner (Like Contact Sync)
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C)),
                                    border = BorderStroke(1.dp, Color(0xFF1E4C38))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "☁️ Google Drive / Cloud Sync (Contact Sync Style)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399)
                                        )
                                        Text(
                                            text = when (uiState.language) {
                                                "ur" -> "جب ایپ ڈیلیٹ ہو، نیا فون لیں، یا فون ری سیٹ/فلیش کریں، اپنے گوگل اکاؤنٹ یا موبائل نمبر سے تمام تقویٰ پوائنٹس واپس بحال کریں۔"
                                                "hi" -> "जब ऐप डिलीट हो, नया फोन लें, या फोन रीसेट/फ्लैश करें, अपने गूगल अकाउंट या मोबाइल नंबर से सभी तक़वा अंक तुरंत रीस्टोर करें।"
                                                else -> "Restore your Taqwa points instantly from Google Drive / Cloud anytime when you switch phones or delete/flash your mobile."
                                            },
                                            fontSize = 10.5.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                // Sync User / Phone input field
                                OutlinedTextField(
                                    value = syncUserId,
                                    onValueChange = { syncUserId = it },
                                    label = {
                                        Text(
                                            text = when (uiState.language) {
                                                "ur" -> "گوگل اکاؤنٹ / موبائل نمبر"
                                                "hi" -> "गूगल अकाउंट / मोबाइल नंबर"
                                                else -> "Google Account / Mobile No."
                                            }
                                        )
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF34D399),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedLabelColor = Color(0xFF34D399),
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Action Buttons for Cloud Sync & Restore
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Backup Button
                                    Button(
                                        onClick = {
                                            val user = syncUserId.trim()
                                            if (user.isBlank()) {
                                                syncStatusMsg = "Please enter Google Account or Mobile Number"
                                                syncIsSuccess = false
                                                return@Button
                                            }
                                            isSyncing = true
                                            syncStatusMsg = null
                                            if (onBackupTaqwa != null) {
                                                onBackupTaqwa(user) { success, msg ->
                                                    isSyncing = false
                                                    syncIsSuccess = success
                                                    syncStatusMsg = msg
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    val res = GoogleSheetMasjidSync.backupTaqwaToCloud(
                                                        user,
                                                        totalPoints,
                                                        uiState.restoredTaqwaPoints,
                                                        "[]"
                                                    )
                                                    isSyncing = false
                                                    syncIsSuccess = res.first
                                                    syncStatusMsg = res.second
                                                    Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        enabled = !isSyncing,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = when (uiState.language) {
                                                "ur" -> "بیک اپ لیں ☁️"
                                                "hi" -> "बैकअप लें ☁️"
                                                else -> "Backup ☁️"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Restore Button
                                    Button(
                                        onClick = {
                                            val user = syncUserId.trim()
                                            if (user.isBlank()) {
                                                syncStatusMsg = "Please enter Google Account or Mobile Number"
                                                syncIsSuccess = false
                                                return@Button
                                            }
                                            isSyncing = true
                                            syncStatusMsg = null
                                            if (onRestoreTaqwa != null) {
                                                onRestoreTaqwa(user) { success, pts, msg ->
                                                    isSyncing = false
                                                    syncIsSuccess = success
                                                    syncStatusMsg = msg
                                                    if (success) {
                                                        inputPoints = pts.toString()
                                                    }
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    val res = GoogleSheetMasjidSync.restoreTaqwaFromCloud(user)
                                                    isSyncing = false
                                                    if (res != null) {
                                                        val ptsToRestore = if (res.second > 0) res.second else res.first
                                                        onRestorePoints(ptsToRestore)
                                                        inputPoints = ptsToRestore.toString()
                                                        syncIsSuccess = true
                                                        syncStatusMsg = "Restored $ptsToRestore Points successfully!"
                                                        Toast.makeText(context, syncStatusMsg, Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        syncIsSuccess = false
                                                        syncStatusMsg = "No backup found for '$user'"
                                                        Toast.makeText(context, syncStatusMsg, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isSyncing,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A93C)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = when (uiState.language) {
                                                "ur" -> "بحال کریں 🔄"
                                                "hi" -> "रीस्टोर करें 🔄"
                                                else -> "Restore 🔄"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }

                                if (isSyncing) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFF34D399)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Connecting to Cloud / Drive...",
                                            fontSize = 11.sp,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                }

                                if (syncStatusMsg != null) {
                                    Text(
                                        text = syncStatusMsg ?: "",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (syncIsSuccess) Color(0xFF34D399) else Color(0xFFEF4444),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                // 2. Manual Offline Points Entry
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "یا دستی طور پر پوائنٹس درج کریں:"
                                        "hi" -> "या मैन्युअल रूप से अंक दर्ज करें:"
                                        else -> "Or enter points manually:"
                                    },
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                OutlinedTextField(
                                    value = inputPoints,
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                                            inputPoints = newValue
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = when (uiState.language) {
                                                "ur" -> "پوائنٹس کی تعداد"
                                                "hi" -> "अंकों की संख्या"
                                                else -> "Number of Points"
                                            }
                                        )
                                    },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    ),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFFF3DE8E),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedLabelColor = Color(0xFFF3DE8E),
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val pts = inputPoints.toIntOrNull() ?: 0
                                    onRestorePoints(pts)
                                    showRestorePointsDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A93C))
                            ) {
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "محفوظ کریں"
                                        "hi" -> "सेव करें"
                                        else -> "Save Points"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRestorePointsDialog = false }) {
                                Text(
                                    text = when (uiState.language) {
                                        "ur" -> "بند کریں"
                                        "hi" -> "बंद करें"
                                        else -> "Close"
                                    },
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        containerColor = Color(0xFF141C2E)
                    )
                }

                var isCalendarExpanded by remember { mutableStateOf(false) }

                // 1 Month Point History Calendar & Yesterday/Today Editable Prayer Points
                MonthPointHistoryCalendar(
                    uiState = uiState,
                    curStrings = curStrings,
                    isCalendarExpanded = isCalendarExpanded,
                    onToggleCalendar = {
                        isCalendarExpanded = !isCalendarExpanded
                    },
                    onTogglePrayer = onTogglePrayer,
                    onSelectDay = { month, day ->
                        selectedDayInfo = Pair(month, day)
                    }
                )
            }

            Text(
                text = "v${BuildConfig.VERSION_NAME} • Powered by @tek",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.Center
            )
        }
        }

    selectedDayInfo?.let { (month, day) ->
        val log = uiState.allLogs.find { it.month == month && it.day == day }
        val appStrings = com.example.ui.theme.LocalAppStrings.current
        val todayCal = remember { Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")) }
        val todayMonth = todayCal.get(Calendar.MONTH) + 1
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)
        val yesterdayCal = remember {
            Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply { add(Calendar.DAY_OF_YEAR, -1) }
        }
        val yesterdayMonth = yesterdayCal.get(Calendar.MONTH) + 1
        val yesterdayDay = yesterdayCal.get(Calendar.DAY_OF_MONTH)
        val targetCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val isTargetFriday = targetCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val dhuhrDisplayName = if (isTargetFriday) appStrings.jumah else appStrings.dhuhr
        val isToday = (month == todayMonth && day == todayDay)
        val isYesterday = (month == yesterdayMonth && day == yesterdayDay)
        val isEditable = isToday || isYesterday
        val totalCompleted = log?.getCompletedPrayersCount() ?: 0

        androidx.compose.ui.window.Dialog(onDismissRequest = { selectedDayInfo = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
                border = BorderStroke(1.dp, if (isEditable) Color(0xFFEAB308) else Color(0xFF1F293D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "$day ${appStrings.getMonthShortName(month)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (isToday) {
                                    Text(
                                        text = appStrings.todayLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.background(Color(0xFFEAB308), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                } else if (isYesterday) {
                                    Text(
                                        text = appStrings.yesterdayLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.background(Color(0xFF25D366), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isEditable) appStrings.activeDateHint else appStrings.historicalRecordHint,
                                fontSize = 11.sp,
                                color = if (isEditable) Color(0xFF25D366) else TextMuted
                            )
                        }
                        IconButton(onClick = { selectedDayInfo = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = appStrings.close, tint = TextMuted)
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1F293D))
                    
                    val context = LocalContext.current

                    @Composable
                    fun InteractivePrayerRow(name: String, systemName: String, prayed: Boolean, color: Color) {
                        val isArrived = isPrayerTimeArrived(isToday, systemName, uiState.todayTimings)
                        val prayerTimeStr = when (systemName.lowercase()) {
                            "fajr" -> uiState.todayTimings?.fajr
                            "dhuhr" -> uiState.todayTimings?.dhuhr
                            "asr" -> uiState.todayTimings?.asr
                            "maghrib" -> uiState.todayTimings?.maghrib
                            "isha" -> uiState.todayTimings?.isha
                            else -> null
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        !isArrived && isToday -> Color(0xFF0E1422).copy(alpha = 0.4f)
                                        prayed -> Color(0xFF1B5E20).copy(alpha = 0.3f)
                                        else -> Color(0xFF151C2C).copy(alpha = 0.4f)
                                    }
                                )
                                .clickable {
                                    if (isToday && !isArrived) {
                                        val msg = if (prayerTimeStr != null) {
                                            String.format(appStrings.prayerTimeNotArrivedToast, name, prayerTimeStr)
                                        } else {
                                            String.format(appStrings.prayerTimeNotStartedToast, name)
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    } else if (isEditable) {
                                        onTogglePrayer(month, day, systemName)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = when {
                                        !isArrived && isToday -> Icons.Outlined.AccessTime
                                        prayed -> Icons.Filled.CheckCircle
                                        else -> Icons.Outlined.CheckCircle
                                    },
                                    contentDescription = if (prayed) "Prayed" else "Missed",
                                    tint = when {
                                        !isArrived && isToday -> TextMuted.copy(alpha = 0.45f)
                                        prayed -> Color(0xFF25D366)
                                        else -> Color(0xFFFF7043)
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isArrived && isToday) TextMuted.copy(alpha = 0.5f) else color
                                    )
                                    if (!isArrived && isToday && prayerTimeStr != null) {
                                        Text(
                                            text = String.format(appStrings.scheduledAt, prayerTimeStr),
                                            fontSize = 10.sp,
                                            color = TextMuted.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = when {
                                    !isArrived && isToday -> appStrings.yetToPerform
                                    prayed -> appStrings.plusOnePoint
                                    isEditable -> appStrings.tapToMark
                                    else -> appStrings.zeroPt
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    !isArrived && isToday -> TextMuted.copy(alpha = 0.5f)
                                    prayed -> Color(0xFF25D366)
                                    else -> TextMuted
                                }
                            )
                        }
                    }

                    InteractivePrayerRow(appStrings.fajr, "Fajr", log?.fajrPrayed ?: false, Color(0xFF90CAF9))
                    InteractivePrayerRow(dhuhrDisplayName, "Dhuhr", log?.dhuhrPrayed ?: false, Color(0xFFFFEB3B))
                    InteractivePrayerRow(appStrings.asr, "Asr", log?.asrPrayed ?: false, Color(0xFFFFB74D))
                    InteractivePrayerRow(appStrings.maghrib, "Maghrib", log?.maghribPrayed ?: false, Color(0xFFAB47BC))
                    InteractivePrayerRow(appStrings.isha, "Isha", log?.ishaPrayed ?: false, Color(0xFF5C6BC0))

                    HorizontalDivider(color = Color(0xFF1F293D))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(appStrings.totalPointsForDay, fontSize = 12.sp, color = TextMuted)
                        Text(String.format(appStrings.pointsOutOfFive, totalCompleted), fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (totalCompleted == 6) Color(0xFFFFD700) else if (totalCompleted > 0) Color(0xFF25D366) else Color.White)
                    }
                }
            }
        }
    }
}
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1524)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color(0xFFE5B842).copy(alpha = 0.5f), Color(0xFF10B981).copy(alpha = 0.3f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF131D32), Color(0xFF0B111D))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFBBF24).copy(alpha = 0.25f), Color(0xFF131D32))
                            ),
                            shape = CircleShape
                        )
                        .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFF3DE8E), Color(0xFFE5B842))
                            )
                        )
                    )
                    Text(
                        text = subtitle,
                        fontSize = 9.5.sp,
                        color = TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF34D399)
                )
            }
        }
    }
}

@Composable
fun getPointColor(count: Int): Color {
    return when (count) {
        0 -> Color(0xFFE53935) // 0 Red
        1 -> Color(0xFFFF9800) // 1 Orange
        2 -> Color(0xFFE91E63) // 2 Pink
        3 -> Color(0xFF008080) // 3 Mor pankh
        4 -> Color(0xFF25D366) // 4 Green
        else -> Color(0xFFFFD700) // 5 prayer Golden
    }
}

@Composable
fun BigCalendarCard(
    selectedDate: Calendar,
    allLogs: List<PrayerLog>,
    onDateClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayCalendar = remember { Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")) }
    val currentMonth = remember(todayCalendar) { todayCalendar.get(Calendar.MONTH) + 1 }
    val currentYear = remember(todayCalendar) { todayCalendar.get(Calendar.YEAR) }
    val maxDays = remember(currentMonth) { getMaxDaysOfMonth(currentMonth) }

    val firstDayOfMonth = remember(currentMonth, currentYear) {
        Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val startOffset = remember(firstDayOfMonth) { firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 }

    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    val appStrings = com.example.ui.theme.LocalAppStrings.current
    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        val monthYearText = remember(currentMonth, currentYear, appStrings) { "${appStrings.getMonthFullName(currentMonth)} $currentYear" }
        // Month/Year Title
        Text(
            text = monthYearText,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Day Headers perfectly aligned using weight(1f)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            appStrings.getDayHeadersList().forEach {
                Text(
                    text = it.take(1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)

        for (row in 0..5) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0..6) {
                    val day = (row * 7 + col) - startOffset + 1
                    
                    if (day in 1..maxDays) {
                        val isToday = day == todayDay
                        val isPast = day < todayDay
                        val isFuture = day > todayDay
                        
                        val log = allLogs.find { it.month == currentMonth && it.day == day }
                        val completedCount = log?.getCompletedPrayersCount() ?: 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .then(if (isToday) Modifier.scale(scale) else Modifier)
                                .alpha(if (isToday || isPast) 1f else 0.5f)
                                .clickable(enabled = !isFuture) {
                                    onDateClick(currentMonth, day)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$day",
                                    color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (!isFuture && (isToday || isPast)) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(getPointColor(completedCount))
                                    )
                                } else {
                                    // Empty placeholder to keep layout perfectly stable
                                    Spacer(modifier = Modifier.size(6.dp))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerTableRow(
    name: String,
    completed: Int,
    missed: Int,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = completed.toString(),
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF25D366),
            textAlign = TextAlign.Center
        )
        Text(
            text = missed.toString(),
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF7043),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ZakatCalculatorDialog(strings: TrackerStrings, onDismiss: () -> Unit) {
    var netWealth by remember { mutableStateOf("") }
    val zakatAmount = netWealth.toDoubleOrNull()?.let { it * 0.025 } ?: 0.0

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1A)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1F293D))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.zakatCalcTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss, 
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF25D366), CircleShape)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Calculator Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF05080E), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1F293D), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = strings.netWealthTitle,
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (netWealth.isEmpty()) "0" else netWealth,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 1
                        )
                        
                        Divider(color = Color(0xFF1F293D), modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text(
                            text = strings.payableZakatTitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%.2f", zakatAmount),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF25D366),
                            maxLines = 1
                        )
                    }
                }

                // Custom Numpad
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in keys) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (key in row) {
                                Button(
                                    onClick = {
                                        when (key) {
                                            "C" -> netWealth = ""
                                            "⌫" -> if (netWealth.isNotEmpty()) netWealth = netWealth.dropLast(1)
                                            else -> {
                                                if (netWealth.length < 12) {
                                                    netWealth += key
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (key == "C" || key == "⌫") Color(0xFF162035) else Color(0xFF101622),
                                        contentColor = if (key == "C") Color(0xFFFF7043) else Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoldenPotliIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val goldPrimary = Color(0xFFFFD700)
        val goldDark = Color(0xFFC68B17)
        val cordColor = Color(0xFFFFF176)

        // Pouch main body
        val bodyPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.35f, h * 0.38f)
            cubicTo(w * 0.05f, h * 0.50f, w * 0.05f, h * 0.95f, w * 0.50f, h * 0.95f)
            cubicTo(w * 0.95f, h * 0.95f, w * 0.95f, h * 0.50f, w * 0.65f, h * 0.38f)
            close()
        }
        drawPath(bodyPath, Brush.verticalGradient(listOf(goldPrimary, goldDark)))

        // Top frills / neck ruffle of potli
        val frillPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.32f, h * 0.38f)
            lineTo(w * 0.18f, h * 0.16f)
            lineTo(w * 0.50f, h * 0.24f)
            lineTo(w * 0.82f, h * 0.16f)
            lineTo(w * 0.68f, h * 0.38f)
            close()
        }
        drawPath(frillPath, goldPrimary)

        // Tie string around neck
        drawLine(cordColor, Offset(w * 0.25f, h * 0.38f), Offset(w * 0.75f, h * 0.38f), strokeWidth = 3f)
        drawCircle(goldPrimary, radius = 2.5f, center = Offset(w * 0.5f, h * 0.45f))
    }
}

@Composable
fun MonthPointHistoryCalendar(
    uiState: UIState,
    curStrings: TrackerStrings,
    isCalendarExpanded: Boolean = false,
    onToggleCalendar: () -> Unit = {},
    onTogglePrayer: (Int, Int, String) -> Unit,
    onSelectDay: (Int, Int) -> Unit
) {
    val todayCal = remember { Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")) }
    val todayMonth = todayCal.get(Calendar.MONTH) + 1
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)
    val todayYear = todayCal.get(Calendar.YEAR)

    val yesterdayCal = remember {
        Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
    }
    val yesterdayMonth = yesterdayCal.get(Calendar.MONTH) + 1
    val yesterdayDay = yesterdayCal.get(Calendar.DAY_OF_MONTH)
    val yesterdayYear = yesterdayCal.get(Calendar.YEAR)

    var displayMonth by remember { mutableIntStateOf(todayMonth) }
    var displayYear by remember { mutableIntStateOf(todayYear) }

    val monthCal = remember(displayMonth, displayYear) {
        Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
            set(Calendar.YEAR, displayYear)
            set(Calendar.MONTH, displayMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 7=Sat
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val appStrings = com.example.ui.theme.LocalAppStrings.current
    val monthName = remember(displayMonth, uiState.language) {
        appStrings.getMonthFullName(displayMonth)
    }

    val dayHeaders = remember(uiState.language) {
        appStrings.getDayHeadersList()
    }

    // Yesterday and Today logs
    val yesterdayLog = uiState.allLogs.find { it.month == yesterdayMonth && it.day == yesterdayDay }
    val todayLog = uiState.allLogs.find { it.month == todayMonth && it.day == todayDay }

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
        border = BorderStroke(1.dp, Color(0xFF1F293D))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Calendar Accordion Toggle Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleCalendar() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Column {
                        Text(
                            text = appStrings.oneMonthHistory,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$monthName $displayYear • ${if (isCalendarExpanded) appStrings.tapToCollapse else appStrings.tapToExpand}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onToggleCalendar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isCalendarExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isCalendarExpanded) appStrings.tapToCollapse else appStrings.tapToExpand,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Accordion Content (Calendar Navigation, Legend, Day Headers, and Date Grid)
            AnimatedVisibility(visible = isCalendarExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Month Navigation Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (displayMonth == 1) {
                                    displayMonth = 12
                                    displayYear--
                                } else {
                                    displayMonth--
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF1F293D), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$monthName $displayYear".uppercase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = appStrings.calendarRuleSubtitle,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                if (displayMonth == 12) {
                                    displayMonth = 1
                                    displayYear++
                                } else {
                                    displayMonth++
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF1F293D), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Legend indicators with color guidance: 0 Red, 1 Orange, 2-5 Light Green to WhatsApp Green, 6 Golden
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF151C2C), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFE53935), CircleShape))
                            Text("0", fontSize = 8.5.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFFF9800), CircleShape))
                            Text("1", fontSize = 8.5.sp, color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF81C784), CircleShape))
                            Text("2", fontSize = 8.5.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF4CAF50), CircleShape))
                            Text("3", fontSize = 8.5.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF388E3C), CircleShape))
                            Text("4", fontSize = 8.5.sp, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF25D366), CircleShape))
                            Text("5", fontSize = 8.5.sp, color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFFFD700), CircleShape))
                            Text("6", fontSize = 8.5.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                        }
                    }

                    // Day headers (Sun to Sat)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayHeaders.forEachIndexed { idx, dayName ->
                            val isFriday = idx == 5
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFriday) Color(0xFF25D366) else TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1F293D))

                    // Calendar Grid of Days
                    val emptySlots = firstDayOfWeek - 1
                    val totalCells = emptySlots + daysInMonth
                    val totalRows = (totalCells + 6) / 7

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        for (row in 0 until totalRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val cellIndex = row * 7 + col
                                    val dayNumber = cellIndex - emptySlots + 1

                                    if (cellIndex < emptySlots || dayNumber > daysInMonth) {
                                        Spacer(modifier = Modifier.weight(1f).height(46.dp))
                                    } else {
                                        val isToday = (displayMonth == todayMonth && dayNumber == todayDay && displayYear == todayYear)
                                        val isYesterday = (displayMonth == yesterdayMonth && dayNumber == yesterdayDay && displayYear == yesterdayYear)
                                        val isActive = isToday || isYesterday

                                        val isFutureDay = (displayYear > todayYear) ||
                                                (displayYear == todayYear && displayMonth > todayMonth) ||
                                                (displayYear == todayYear && displayMonth == todayMonth && dayNumber > todayDay)

                                        val log = uiState.allLogs.find { it.month == displayMonth && it.day == dayNumber }
                                        val points = log?.getCompletedPrayersCount() ?: 0

                                        // Point color as per prayers offered: 0 Red, 1 Orange, 2 to 5 Light Green to WhatsApp Green, 6 Golden
                                        val pointColor = when {
                                            isFutureDay -> TextMuted.copy(alpha = 0.3f)
                                            points == 0 -> Color(0xFFE53935) // 0 Red
                                            points == 1 -> Color(0xFFFF9800) // 1 Orange
                                            points == 2 -> Color(0xFF81C784) // 2 Light Green
                                            points == 3 -> Color(0xFF4CAF50) // 3 Medium Green
                                            points == 4 -> Color(0xFF388E3C) // 4 Dark Green
                                            points == 5 -> Color(0xFF25D366) // 5 WhatsApp Green
                                            else -> Color(0xFFFFD700) // 6 Golden
                                        }

                                        val cellBg = when {
                                            isToday -> Color(0xFFEAB308).copy(alpha = 0.20f)
                                            isYesterday -> Color(0xFF25D366).copy(alpha = 0.18f)
                                            points >= 6 -> Color(0xFFFFD700).copy(alpha = 0.16f)
                                            points in 2..5 -> Color(0xFF25D366).copy(alpha = 0.10f)
                                            points == 1 -> Color(0xFFFF9800).copy(alpha = 0.10f)
                                            !isFutureDay -> Color(0xFF1A0F14).copy(alpha = 0.5f)
                                            else -> Color(0xFF090D15).copy(alpha = 0.6f)
                                        }

                                        val cellBorder = when {
                                            isToday -> Color(0xFFEAB308)
                                            isYesterday -> Color(0xFF25D366)
                                            points >= 6 -> Color(0xFFFFD700).copy(alpha = 0.7f)
                                            points in 2..5 -> Color(0xFF25D366).copy(alpha = 0.4f)
                                            points == 1 -> Color(0xFFFF9800).copy(alpha = 0.4f)
                                            !isFutureDay && points == 0 -> Color(0xFFE53935).copy(alpha = 0.35f)
                                            else -> Color(0xFF161E2D).copy(alpha = 0.4f)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(46.dp)
                                                .background(cellBg, RoundedCornerShape(8.dp))
                                                .border(
                                                    width = if (isActive) 1.5.dp else 1.dp,
                                                    color = cellBorder,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    onSelectDay(displayMonth, dayNumber)
                                                }
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            // 100% Date number (centered, prominent)
                                            Text(
                                                text = "$dayNumber",
                                                fontSize = 13.5.sp,
                                                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                                                color = when {
                                                    isToday -> Color(0xFFEAB308)
                                                    isYesterday -> Color(0xFF25D366)
                                                    points > 0 -> Color.White
                                                    !isFutureDay && points == 0 -> Color.White.copy(alpha = 0.85f)
                                                    else -> Color.White.copy(alpha = 0.35f)
                                                },
                                                modifier = Modifier.align(Alignment.Center)
                                            )

                                            // Point indicator (number only, bottom-left aligned, colored by prayers offered, 0 RED)
                                            Text(
                                                text = when {
                                                    isFutureDay -> "•"
                                                    else -> "$points"
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = pointColor,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(start = 2.dp, bottom = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF1F293D), modifier = Modifier.padding(vertical = 2.dp))

            // Editable Prayer Time Points for Yesterday & Today (stays as it is below accordion)
            Text(
                text = appStrings.editPrayerPointsTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 1.sp
            )

            val isYesterdayFriday = yesterdayCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
            val isTodayFriday = todayCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

            // Today Card (Today on top)
            DayPrayerEditorCard(
                label = appStrings.todaySimple,
                dateString = "$todayDay ${appStrings.getMonthShortName(todayMonth)}",
                log = todayLog,
                badgeColor = Color(0xFFEAB308),
                isToday = true,
                todayTimings = uiState.todayTimings,
                isFriday = isTodayFriday,
                onTogglePrayer = { prayerName ->
                    onTogglePrayer(todayMonth, todayDay, prayerName)
                }
            )

            // Yesterday Card (Yesterday below it)
            DayPrayerEditorCard(
                label = appStrings.yesterdaySimple,
                dateString = "$yesterdayDay ${appStrings.getMonthShortName(yesterdayMonth)}",
                log = yesterdayLog,
                badgeColor = Color(0xFF25D366),
                isToday = false,
                todayTimings = uiState.todayTimings,
                isFriday = isYesterdayFriday,
                onTogglePrayer = { prayerName ->
                    onTogglePrayer(yesterdayMonth, yesterdayDay, prayerName)
                }
            )
        }
    }
}

@Composable
fun DayPrayerEditorCard(
    label: String,
    dateString: String,
    log: PrayerLog?,
    badgeColor: Color,
    isToday: Boolean,
    todayTimings: AzanTiming?,
    isFriday: Boolean = false,
    onTogglePrayer: (String) -> Unit
) {
    val context = LocalContext.current
    val appStrings = com.example.ui.theme.LocalAppStrings.current
    val completedCount = log?.getCompletedPrayersCount() ?: 0
    val dhuhrLabel = if (isFriday) appStrings.jumah else appStrings.dhuhr
    val prayers = listOf(
        Triple("Fajr", appStrings.fajr, log?.fajrPrayed ?: false),
        Triple("Dhuhr", dhuhrLabel, log?.dhuhrPrayed ?: false),
        Triple("Asr", appStrings.asr, log?.asrPrayed ?: false),
        Triple("Maghrib", appStrings.maghrib, log?.maghribPrayed ?: false),
        Triple("Isha", appStrings.isha, log?.ishaPrayed ?: false),
        Triple("Tahajjud", appStrings.tahajjud, log?.tahajjudPrayed ?: false)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF131926), RoundedCornerShape(12.dp))
            .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = String.format(appStrings.pointsOutOfFive, completedCount),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = if (completedCount == 6) Color(0xFFFFD700) else if (completedCount > 0) Color(0xFF25D366) else TextMuted
            )
        }

        // 5 Prayer toggle chips in a single row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            prayers.forEach { (systemName, displayName, prayed) ->
                val isArrived = isPrayerTimeArrived(isToday, systemName, todayTimings)
                val prayerTimeStr = when (systemName.lowercase()) {
                    "fajr" -> todayTimings?.fajr
                    "dhuhr" -> todayTimings?.dhuhr
                    "asr" -> todayTimings?.asr
                    "maghrib" -> todayTimings?.maghrib
                    "isha" -> todayTimings?.isha
                    else -> null
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .background(
                            color = when {
                                !isArrived && isToday -> Color(0xFF0C101B).copy(alpha = 0.6f)
                                prayed -> Color(0xFF1B5E20).copy(alpha = 0.6f)
                                else -> Color(0xFF0F1522)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                !isArrived && isToday -> Color(0xFF1F293D).copy(alpha = 0.5f)
                                prayed -> Color(0xFF25D366)
                                else -> Color(0xFF1F293D)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (isToday && !isArrived) {
                                val msg = if (prayerTimeStr != null) {
                                    String.format(appStrings.prayerTimeNotArrivedToast, displayName, prayerTimeStr)
                                } else {
                                    String.format(appStrings.prayerTimeNotStartedToast, displayName)
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                onTogglePrayer(systemName)
                            }
                        }
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = when {
                                    !isArrived && isToday -> Icons.Outlined.AccessTime
                                    prayed -> Icons.Filled.CheckCircle
                                    else -> Icons.Outlined.CheckCircle
                                },
                                contentDescription = displayName,
                                tint = when {
                                    !isArrived && isToday -> TextMuted.copy(alpha = 0.4f)
                                    prayed -> Color(0xFF25D366)
                                    else -> TextMuted.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = displayName.take(3),
                                fontSize = 9.5.sp,
                                fontWeight = if (prayed) FontWeight.ExtraBold else FontWeight.Medium,
                                color = when {
                                    !isArrived && isToday -> TextMuted.copy(alpha = 0.45f)
                                    prayed -> Color.White
                                    else -> TextMuted
                                }
                            )
                        }
                        if (!isArrived && isToday && prayerTimeStr != null) {
                            Text(
                                text = prayerTimeStr,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun isPrayerTimeArrived(
    isToday: Boolean,
    prayerName: String,
    todayTimings: AzanTiming?
): Boolean {
    if (!isToday) return true // Past days like yesterday can always be recorded
    if (todayTimings == null) return false
    val timeStr = when (prayerName.lowercase()) {
        "fajr" -> todayTimings.fajr
        "dhuhr" -> todayTimings.dhuhr
        "asr" -> todayTimings.asr
        "maghrib" -> todayTimings.maghrib
        "isha" -> todayTimings.isha
        "tahajjud" -> "01:30"
        else -> null
    } ?: return false

    val parts = timeStr.split(":")
    if (parts.size != 2) return false
    val hour = parts[0].toIntOrNull() ?: return false
    val minute = parts[1].toIntOrNull() ?: return false

    val now = Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata"))
    val prayerCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        // Selection / tick allowed 20 minutes after Azan time
        add(Calendar.MINUTE, 20)
    }

    return !now.before(prayerCal)
}

fun getMonthShort(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }
}

@Composable
fun RamazanDaySlotItem(
    day: RamazanSchedule,
    isDayToday: Boolean,
    isDayEnabled: Boolean,
    currentState: Int,
    isFirstSlot: Boolean,
    curStrings: com.example.ui.theme.AppStrings,
    uiState: UIState,
    onToggleState: () -> Unit,
    onDisabledClick: () -> Unit
) {
    val textColor = if (day.isFriday) Color(0xFFFFD700) else Color.White

    val dayOfMonth = day.cal.get(Calendar.DAY_OF_MONTH)
    val monthNum = day.cal.get(Calendar.MONTH) + 1
    val year = day.cal.get(Calendar.YEAR)
    val monthStr = if (curStrings.febShort == "Feb") curStrings.getMonthShortName(monthNum) else curStrings.getMonthFullName(monthNum)
    val dayOfWeekStr = curStrings.getDayOfWeekFull(day.cal.get(Calendar.DAY_OF_WEEK))
    val ordinalSuffix = if (dayOfMonth in 11..13) "th" else when (dayOfMonth % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    val formattedDate = if (uiState.language == "en") "$dayOfMonth$ordinalSuffix $monthStr $year" else "$dayOfMonth $monthStr $year"

    val isSlotEnabled = isDayEnabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isFirstSlot) 4.dp else 2.dp)
            .alpha(if (isSlotEnabled) 1f else 0.45f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141D2E)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isFirstSlot) Color(0xFF324263) else Color(0xFF1F293D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = if (isFirstSlot) 10.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Circle with 01, 02... and heading ROZA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(if (isFirstSlot) 44.dp else 34.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isFirstSlot) 38.dp else 28.dp)
                        .background(Color(0xFF1F293D), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day.id + 1).toString().padStart(2, '0'),
                        fontSize = if (isFirstSlot) 14.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (uiState.language == "en") "ROZA" else curStrings.roja,
                    fontSize = if (isFirstSlot) 8.sp else 6.5.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            // Center: Date + Day, and 2 columns for Sehar & Iftar
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Date Row (Caps)
                Text(
                    text = "$formattedDate ($dayOfWeekStr)".uppercase(),
                    fontSize = if (isFirstSlot) 13.sp else 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(if (isFirstSlot) 4.dp else 2.dp))

                // 2 Columns: Sehar & Iftar side by side with divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = curStrings.seharTime,
                            fontSize = if (isFirstSlot) 10.sp else 8.sp,
                            color = TextMuted
                        )
                        Text(
                            text = day.seharTime,
                            fontSize = if (isFirstSlot) 18.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(if (isFirstSlot) 26.dp else 18.dp)
                            .padding(horizontal = 4.dp),
                        color = Color(0xFF1F293D)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = curStrings.iftarTime,
                            fontSize = if (isFirstSlot) 10.sp else 8.sp,
                            color = TextMuted
                        )
                        Text(
                            text = day.iftarTime,
                            fontSize = if (isFirstSlot) 18.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    }
                }
            }

            // Right: Tick Mark Button
            IconButton(
                onClick = {
                    if (!isDayEnabled) {
                        onDisabledClick()
                    } else {
                        onToggleState()
                    }
                },
                modifier = Modifier.size(if (isFirstSlot) 36.dp else 30.dp),
                enabled = isSlotEnabled
            ) {
                when (currentState) {
                    1 -> Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = curStrings.rozaCompletedLabel,
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(if (isFirstSlot) 28.dp else 22.dp)
                    )
                    2 -> Box(
                        modifier = Modifier
                            .size(if (isFirstSlot) 24.dp else 20.dp)
                            .background(Color(0xFFE53935).copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, Color(0xFFE53935), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = curStrings.rozaMissedLabel,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(if (isFirstSlot) 15.dp else 13.dp)
                        )
                    }
                    else -> Box(
                        modifier = Modifier
                            .size(if (isFirstSlot) 22.dp else 18.dp)
                            .border(
                                1.5.dp,
                                if (isSlotEnabled) Color.White.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.18f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EidInfoRow(strings: com.example.ui.theme.AppStrings, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EidCard(strings.eidUlFitr, strings.eidFitrDate, strings.eidFitrDay, modifier = Modifier.weight(1f))
        EidCard(strings.eidUlAdha, strings.eidAdhaDate, strings.eidAdhaDay, modifier = Modifier.weight(1f))
    }
}

@Composable
fun EidCard(title: String, date: String, day: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .border(1.dp, Color(0xFF1F293D), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141D2E)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = date,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = day,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }
    }
}

@Composable
fun RamazanScheduleAndDuaBlock(
    curStrings: com.example.ui.theme.AppStrings,
    uiState: UIState,
    onLanguageSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("RamazanTracker", android.content.Context.MODE_PRIVATE) }
    val todayForRamazan = remember {
        java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata")).apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
    }
    val ramazanSchedule = remember { getRamazan2027Schedule() }
    val visibleRamazanSchedule = ramazanSchedule.filter { it.cal.timeInMillis >= todayForRamazan.timeInMillis }

    // Reactive Roza states: 0 = No selected, 1 = Green (Done), 2 = Red (Missed)
    val rojaStates = remember {
        mutableStateMapOf<Int, Int>().apply {
            ramazanSchedule.forEach { day ->
                val state = if (prefs.contains("roja_state_${day.id}")) {
                    prefs.getInt("roja_state_${day.id}", 0)
                } else if (prefs.getBoolean("roja_done_${day.id}", false)) {
                    1
                } else {
                    0
                }
                put(day.id, state)
            }
        }
    }
    val totalRozaPoints = rojaStates.values.count { it == 1 }
    val missedRozaCount = rojaStates.values.count { it == 2 }

    val isBeforeRamazan = todayForRamazan.timeInMillis < ramazanSchedule.first().cal.timeInMillis
    var showZakatPopup by remember { mutableStateOf(false) }

    var activeDuaAudio by remember { mutableStateOf<String?>(null) }
    val mediaPlayerHolder = remember { mutableStateOf<android.media.MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayerHolder.value?.stop()
                mediaPlayerHolder.value?.release()
            } catch (_: Exception) {}
            mediaPlayerHolder.value = null
        }
    }

    val toggleDuaAudio: (String, Int) -> Unit = { duaType, resId ->
        if (activeDuaAudio == duaType) {
            try {
                mediaPlayerHolder.value?.stop()
                mediaPlayerHolder.value?.release()
            } catch (_: Exception) {}
            mediaPlayerHolder.value = null
            activeDuaAudio = null
        } else {
            try {
                mediaPlayerHolder.value?.stop()
                mediaPlayerHolder.value?.release()
            } catch (_: Exception) {}
            mediaPlayerHolder.value = null
            try {
                val mp = android.media.MediaPlayer.create(context, resId)
                if (mp != null) {
                    mp.setOnCompletionListener {
                        activeDuaAudio = null
                        try { it.release() } catch (_: Exception) {}
                        mediaPlayerHolder.value = null
                    }
                    mp.start()
                    mediaPlayerHolder.value = mp
                    activeDuaAudio = duaType
                }
            } catch (_: Exception) {
                activeDuaAudio = null
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Sehar and Iftar Dua Section (Placed at the top right below the Language button)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141D2E)),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row with Centered Title (Language buttons removed, follows top language selection)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = curStrings.ramazanDuasHeading,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                }

                // Sehar Dua
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF0D1424),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbTwilight,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = curStrings.seharDuaTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (activeDuaAudio == "sehar") Color(0xFFFFD700) else Color(0xFF141D2E)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFFFD700),
                                    shape = CircleShape
                                )
                                .clickable {
                                    toggleDuaAudio("sehar", com.example.R.raw.dua_sehar)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (activeDuaAudio == "sehar") Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause Sehar Dua",
                                tint = if (activeDuaAudio == "sehar") Color(0xFF0D1424) else Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = curStrings.seharDuaArabic,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    if (curStrings.seharDuaTranslit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = curStrings.seharDuaTranslit,
                            fontSize = 11.sp,
                            color = Color(0xFFFFE082),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = curStrings.seharDuaTranslation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB0BEC5),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }

                // Iftar Dua
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF0D1424),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            Color(0xFF25D366).copy(alpha = 0.35f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = curStrings.iftarDuaTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (activeDuaAudio == "iftar") Color(0xFF25D366) else Color(0xFF141D2E)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF25D366),
                                    shape = CircleShape
                                )
                                .clickable {
                                    toggleDuaAudio("iftar", com.example.R.raw.dua_iftar)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (activeDuaAudio == "iftar") Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause Iftar Dua",
                                tint = if (activeDuaAudio == "iftar") Color(0xFF0D1424) else Color(0xFF25D366),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = curStrings.iftarDuaArabic,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    if (curStrings.iftarDuaTranslit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = curStrings.iftarDuaTranslit,
                            fontSize = 11.sp,
                            color = Color(0xFFA5D6A7),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = curStrings.iftarDuaTranslation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB0BEC5),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Ramazan Timing Title
        Text(
            text = curStrings.ramazanTime2027.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            textAlign = TextAlign.Center
        )

        // Total Roza Points Banner (Placed below Ramazan Time 2027 heading)
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141D2E)),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = curStrings.totalRozaPoints,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${curStrings.rozaCompletedLabel}: $totalRozaPoints  •  ${curStrings.rozaMissedLabel}: $missedRozaCount",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "$totalRozaPoints / 30",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    val progressFraction = (totalRozaPoints / 30f).coerceIn(0f, 1f)
                    if (progressFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressFraction)
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF25D366))))
                        )
                    }
                }
            }
        }

        // 1-Month Ramazan Point History Calendar & Daily Schedule
        var isCalendarExpanded by remember { mutableStateOf(false) }

        val dayHeaders = remember(uiState.language) {
            curStrings.getDayHeadersList()
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1F293D))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Calendar Accordion Toggle Header (Same as Record)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isCalendarExpanded = !isCalendarExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Column {
                            Text(
                                text = curStrings.oneMonthHistory,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "9 Feb – 10 Mar 2027 • ${if (isCalendarExpanded) curStrings.tapToCollapse else curStrings.tapToExpand}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = { isCalendarExpanded = !isCalendarExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isCalendarExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (isCalendarExpanded) curStrings.tapToCollapse else curStrings.tapToExpand,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Accordion Content (1 Month Ramazan Calendar: 9 Feb 2027 to 10 March 2027)
                AnimatedVisibility(visible = isCalendarExpanded) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Month Header Title (1 Month Ramazan only)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RAMAZAN 2027 (1448 AH)".uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "9 FEB 2027 – 10 MAR 2027",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "1 pt per Roza • 30 Roza Total",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }

                        // Legend indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF151C2C), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFF25D366), CircleShape))
                                Text(curStrings.rozaCompletedLabel + " (1)", fontSize = 8.5.sp, color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFFE53935), CircleShape))
                                Text(curStrings.rozaMissedLabel + " (0)", fontSize = 8.5.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFFEAB308), CircleShape))
                                Text(curStrings.legendToday, fontSize = 8.5.sp, color = Color(0xFFEAB308), fontWeight = FontWeight.Bold)
                            }
                        }

                        // Day headers (Sun to Sat)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            dayHeaders.forEachIndexed { idx, dayName ->
                                val isFriday = idx == 5
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFriday) Color(0xFF25D366) else TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF1F293D))

                        // Calendar Grid (30 days from 9 Feb 2027 to 10 March 2027)
                        val startDayOfWeek = ramazanSchedule.first().cal.get(Calendar.DAY_OF_WEEK) // Tuesday = 3
                        val emptySlots = startDayOfWeek - 1 // 2 empty slots
                        val totalCells = emptySlots + ramazanSchedule.size // 32
                        val totalRows = (totalCells + 6) / 7 // 5 rows

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            for (row in 0 until totalRows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    for (col in 0 until 7) {
                                        val cellIndex = row * 7 + col
                                        val scheduleIndex = cellIndex - emptySlots

                                        if (cellIndex < emptySlots || scheduleIndex >= ramazanSchedule.size) {
                                            Spacer(modifier = Modifier.weight(1f).height(46.dp))
                                        } else {
                                            val day = ramazanSchedule[scheduleIndex]
                                            val isDayToday = if (isBeforeRamazan) {
                                                day.id == 0
                                            } else {
                                                day.cal.get(Calendar.YEAR) == todayForRamazan.get(Calendar.YEAR) &&
                                                day.cal.get(Calendar.DAY_OF_YEAR) == todayForRamazan.get(Calendar.DAY_OF_YEAR)
                                            }
                                            val isFutureDay = if (isBeforeRamazan) {
                                                day.id > 0
                                            } else {
                                                day.cal.timeInMillis > todayForRamazan.timeInMillis
                                            }
                                            val isDayEnabled = !isFutureDay
                                            val currentState = rojaStates[day.id] ?: 0

                                            val pointColor = when {
                                                isFutureDay -> TextMuted.copy(alpha = 0.3f)
                                                currentState == 1 -> Color(0xFF25D366)
                                                currentState == 2 -> Color(0xFFE53935)
                                                else -> Color.White.copy(alpha = 0.4f)
                                            }

                                            val cellBg = when {
                                                isDayToday -> Color(0xFFEAB308).copy(alpha = 0.20f)
                                                currentState == 1 -> Color(0xFF25D366).copy(alpha = 0.18f)
                                                currentState == 2 -> Color(0xFFE53935).copy(alpha = 0.18f)
                                                !isFutureDay -> Color(0xFF131A29)
                                                else -> Color(0xFF090D15).copy(alpha = 0.6f)
                                            }

                                            val cellBorder = when {
                                                isDayToday -> Color(0xFFEAB308)
                                                currentState == 1 -> Color(0xFF25D366).copy(alpha = 0.7f)
                                                currentState == 2 -> Color(0xFFE53935).copy(alpha = 0.7f)
                                                !isFutureDay -> Color(0xFF2B3954)
                                                else -> Color(0xFF161E2D).copy(alpha = 0.4f)
                                            }

                                            val dayNum = day.cal.get(Calendar.DAY_OF_MONTH)
                                            val monthNum = day.cal.get(Calendar.MONTH) + 1

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(46.dp)
                                                    .background(cellBg, RoundedCornerShape(8.dp))
                                                    .border(
                                                        width = if (isDayToday) 1.5.dp else 1.dp,
                                                        color = cellBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable {
                                                        if (!isDayEnabled) {
                                                            Toast.makeText(context, curStrings.cannotTickBeforeDateToast, Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            val nextState = when (currentState) {
                                                                0 -> 1
                                                                1 -> 2
                                                                else -> 0
                                                            }
                                                            rojaStates[day.id] = nextState
                                                            prefs.edit().putInt("roja_state_${day.id}", nextState).apply()
                                                        }
                                                    }
                                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "R${day.id + 1}",
                                                    fontSize = 7.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (day.isFriday) Color(0xFFFFD700) else Color.White.copy(alpha = 0.55f),
                                                    modifier = Modifier.align(Alignment.TopStart).padding(start = 1.dp)
                                                )

                                                Column(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = "$dayNum",
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isDayToday) FontWeight.Black else FontWeight.Bold,
                                                        color = when {
                                                            isDayToday -> Color(0xFFEAB308)
                                                            currentState == 1 -> Color(0xFF25D366)
                                                            currentState == 2 -> Color(0xFFE53935)
                                                            !isFutureDay -> Color.White
                                                            else -> Color.White.copy(alpha = 0.35f)
                                                        }
                                                    )
                                                    if (dayNum == 9 && monthNum == 2) {
                                                        Text("Feb", fontSize = 6.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                                                    } else if (dayNum == 1 && monthNum == 3) {
                                                        Text("Mar", fontSize = 6.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Text(
                                                    text = when {
                                                        isFutureDay -> "•"
                                                        currentState == 1 -> "1"
                                                        currentState == 2 -> "0"
                                                        else -> "—"
                                                    },
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = pointColor,
                                                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 1.dp, bottom = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF1F293D), modifier = Modifier.padding(vertical = 2.dp))

                // Ramazan Daily Schedule Title below calendar
                Text(
                    text = curStrings.ramazanTime2027.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp
                )

                if (visibleRamazanSchedule.isEmpty()) {
                    Text(
                        text = curStrings.ramazanConcluded,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    val currentRoza = visibleRamazanSchedule.firstOrNull { day ->
                        if (isBeforeRamazan) {
                            day.id == 0
                        } else {
                            day.cal.get(Calendar.YEAR) == todayForRamazan.get(Calendar.YEAR) &&
                            day.cal.get(Calendar.DAY_OF_YEAR) == todayForRamazan.get(Calendar.DAY_OF_YEAR)
                        }
                    } ?: visibleRamazanSchedule.firstOrNull()

                    val remainingRozas = if (currentRoza != null) {
                        visibleRamazanSchedule.filter { it.id != currentRoza.id }
                    } else {
                        emptyList()
                    }

                    var isRemainingScheduleExpanded by remember { mutableStateOf(false) }

                    // 1. Highlighted / Current Roza visible as it is
                    if (currentRoza != null) {
                        val isDayToday = if (isBeforeRamazan) {
                            currentRoza.id == 0
                        } else {
                            currentRoza.cal.get(Calendar.YEAR) == todayForRamazan.get(Calendar.YEAR) &&
                            currentRoza.cal.get(Calendar.DAY_OF_YEAR) == todayForRamazan.get(Calendar.DAY_OF_YEAR)
                        }
                        val isFutureDay = if (isBeforeRamazan) {
                            currentRoza.id > 0
                        } else {
                            currentRoza.cal.timeInMillis > todayForRamazan.timeInMillis
                        }
                        val isDayEnabled = !isFutureDay
                        val currentState = rojaStates[currentRoza.id] ?: 0

                        RamazanDaySlotItem(
                            day = currentRoza,
                            isDayToday = isDayToday,
                            isDayEnabled = isDayEnabled,
                            currentState = currentState,
                            isFirstSlot = true,
                            curStrings = curStrings,
                            uiState = uiState,
                            onToggleState = {
                                val nextState = when (currentState) {
                                    0 -> 1
                                    1 -> 2
                                    else -> 0
                                }
                                rojaStates[currentRoza.id] = nextState
                                prefs.edit().putInt("roja_state_${currentRoza.id}", nextState).apply()
                            },
                            onDisabledClick = {
                                Toast.makeText(context, curStrings.cannotTickBeforeDateToast, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // 2. Below all in accordion closed by default
                    if (remainingRozas.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF1F293D))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                // Accordion Toggle Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isRemainingScheduleExpanded = !isRemainingScheduleExpanded }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(Color(0xFF1F293D), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = when (uiState.language) {
                                                    "ur" -> "باقی رمضان شیڈول (روزہ ${remainingRozas.first().id + 1} – 30)"
                                                    "hi" -> "बाकी रमज़ान शेड्यूल (रोज़ा ${remainingRozas.first().id + 1} – 30)"
                                                    else -> "REMAINING RAMAZAN SCHEDULE (ROZA ${remainingRozas.first().id + 1} – 30)"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD700),
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = if (isRemainingScheduleExpanded) curStrings.tapToCollapse else curStrings.tapToExpand,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { isRemainingScheduleExpanded = !isRemainingScheduleExpanded },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isRemainingScheduleExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                                contentDescription = if (isRemainingScheduleExpanded) curStrings.tapToCollapse else curStrings.tapToExpand,
                                                tint = Color(0xFFFFD700)
                                            )
                                        }
                                    }
                                }

                                // Accordion Content: remaining rozas
                                AnimatedVisibility(visible = isRemainingScheduleExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        remainingRozas.forEach { day ->
                                            val isDayToday = if (isBeforeRamazan) {
                                                day.id == 0
                                            } else {
                                                day.cal.get(Calendar.YEAR) == todayForRamazan.get(Calendar.YEAR) &&
                                                day.cal.get(Calendar.DAY_OF_YEAR) == todayForRamazan.get(Calendar.DAY_OF_YEAR)
                                            }
                                            val isFutureDay = if (isBeforeRamazan) {
                                                day.id > 0
                                            } else {
                                                day.cal.timeInMillis > todayForRamazan.timeInMillis
                                            }
                                            val isDayEnabled = !isFutureDay
                                            val currentState = rojaStates[day.id] ?: 0

                                            RamazanDaySlotItem(
                                                day = day,
                                                isDayToday = isDayToday,
                                                isDayEnabled = isDayEnabled,
                                                currentState = currentState,
                                                isFirstSlot = false,
                                                curStrings = curStrings,
                                                uiState = uiState,
                                                onToggleState = {
                                                    val nextState = when (currentState) {
                                                        0 -> 1
                                                        1 -> 2
                                                        else -> 0
                                                    }
                                                    rojaStates[day.id] = nextState
                                                    prefs.edit().putInt("roja_state_${day.id}", nextState).apply()
                                                },
                                                onDisabledClick = {
                                                    Toast.makeText(context, curStrings.cannotTickBeforeDateToast, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Eid Section (shifted from Home page to above Zakat Calculator)
                        EidInfoRow(
                            strings = curStrings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                        )

                        // Zakat Calculator outside accordion below remaining ramazan schedule
                        Button(
                            onClick = { showZakatPopup = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1F293D),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            GoldenPotliIcon()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = curStrings.zakatCalcBtn,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        if (showZakatPopup) {
            ZakatCalculatorDialog(strings = curStrings, onDismiss = { showZakatPopup = false })
        }
    }
}

@Composable
fun RamazanScreenContent(
    uiState: UIState,
    onLanguageSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val curStrings = when (uiState.language) {
        "hi" -> com.example.ui.theme.HindiStrings
        "ur" -> com.example.ui.theme.UrduStrings
        else -> com.example.ui.theme.EnglishStrings
    }

    CompositionLocalProvider(com.example.ui.theme.LocalAppStrings provides curStrings) {
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Sticky Header & Language Toggle (Always visible at the top while scrolling)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Top Left Language Circles (E, ह, ا)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.LanguageCirclesRow(
                            currentLang = uiState.language,
                            onLangSelect = onLanguageSelect
                        )
                    }

                    // Unified Consistent App Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = curStrings.solapur,
                            fontSize = if (curStrings.solapur == "SOLAPUR") 10.sp else 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = if (curStrings.solapur == "SOLAPUR") 5.sp else 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = if (uiState.language == "ur") curStrings.azanTimeHeader else "AZAN TIME",
                            fontSize = if (uiState.language == "ur") 22.sp else 30.sp,
                            fontFamily = if (uiState.language == "ur") null else GraenMetalFontFamily,
                            fontWeight = FontWeight.Normal,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = GraenMetalGoldGradient,
                                shadow = GraenMetalGoldShadow
                            ),
                            letterSpacing = if (uiState.language == "ur") 0.sp else 1.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (curStrings.punchLine == "Come towards the path of Allah") curStrings.punchLine.uppercase() else curStrings.punchLine,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = if (uiState.language == "ur") 0.sp else 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RamazanScheduleAndDuaBlock(
                        curStrings = curStrings,
                        uiState = uiState,
                        onLanguageSelect = onLanguageSelect
                    )
                }

                Text(
                    text = "v${BuildConfig.VERSION_NAME} • Powered by @tek",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


