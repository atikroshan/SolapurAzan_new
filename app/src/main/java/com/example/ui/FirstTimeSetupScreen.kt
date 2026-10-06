package com.example.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.MasjidItem
import com.example.data.getLocalizedArea
import com.example.data.getLocalizedName
import java.util.Locale

private const val KAABA_BG_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuD6TYjWkZp6_LZHKswlMMD5vxC39ZbF05d_QgMWEV4PRTOct-gagp5BbUxOkxr_ZBGaQHh6euej1VX0n4cVVyyOqqTlqomtxXllJFFdTZ7znUB8T5crJmB6-grdLEQv-HGnBD469f1KaBnqXuvBSjJQtKSXejQWH_XdTYRkO-mQIC9eFYH0d4do--51pjlVSp7Yaynw4seWDkd8Q4ry2IdE8n07gt-Gfvfqs7rnjR2ne0hyPomXh6yAuA"

@Composable
fun FirstTimeSetupScreen(
    uiState: UIState,
    onSelectMasjid: (MasjidItem) -> Unit,
    onFinishSetup: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isChangeMode = onBack != null

    // Auto-refresh masajid list from Google Sheet whenever Search/Setup screen opens
    LaunchedEffect(Unit) {
        onRefresh()
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    // In Change mode: start with currently set masjid.
    // In First-Time setup: do NOT auto-select any masjid until user actually searches and clicks one!
    var userSelectedMasjid by remember(uiState.selectedMasjid, isChangeMode) {
        mutableStateOf(if (isChangeMode) uiState.selectedMasjid else null)
    }

    val hasQuery = searchQuery.trim().isNotBlank()
    // Show photo card only when a masjid is actually chosen by the user (or in change mode)
    val showMasjidCard = userSelectedMasjid != null

    // System Back handling
    BackHandler {
        if (hasQuery) {
            searchQuery = ""
        } else if (onBack != null) {
            onBack()
        }
    }

    // Speech to text activity launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTextList?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                searchQuery = spokenText
                isSearchFocused = true
            }
        }
    }

    // Audio permission launcher for speech recognition
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchVoiceSearch(context, speechLauncher)
        } else {
            Toast.makeText(
                context,
                "Microphone permission is needed for voice search",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun startVoiceInput() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            launchVoiceSearch(context, speechLauncher)
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Matching masajid list (only shown when user types search text)
    val matchedList = remember(searchQuery, uiState.allMasajid, uiState.language, hasQuery) {
        if (!hasQuery) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            uiState.allMasajid.filter { m ->
                m.name.lowercase().contains(q) ||
                m.area.lowercase().contains(q) ||
                m.city.lowercase().contains(q) ||
                m.state.lowercase().contains(q) ||
                m.id.lowercase().contains(q) ||
                m.getLocalizedName(uiState.language).lowercase().contains(q) ||
                m.getLocalizedArea(uiState.language).lowercase().contains(q)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F14))
    ) {
        // 1. FULL-BLEED KAABA ATMOSPHERIC BACKGROUND (SHOWN ON START PAGE WHEN NO MASJID CARD IS ACTIVE)
        if (!showMasjidCard) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(KAABA_BG_URL)
                    .error(R.drawable.img_makkah_light)
                    .placeholder(R.drawable.img_makkah_light)
                    .crossfade(true)
                    .build(),
                contentDescription = "Majestic Masjid al-Haram & Holy Kaaba",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.02f)
            )

            // High-grade atmospheric gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0A0F14).copy(alpha = 0.88f),
                                Color(0xFF0A0F14).copy(alpha = 0.72f),
                                Color(0xFF0A0F14).copy(alpha = 0.94f)
                            )
                        )
                    )
            )
        }

        // SCROLLABLE CONTAINER
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeight = maxHeight
            // Masjid select karne par photo screen ka 50%
            val photoHeight = (screenHeight * 0.50f).coerceAtLeast(240.dp)

            if (showMasjidCard && userSelectedMasjid != null) {
                // PHOTO CARD SCREEN (Change Mode or Selected Masjid View)
                val activeMasjid = userSelectedMasjid!!
                val hasSelectionChanged = activeMasjid.id != uiState.selectedMasjid.id

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top spacing without circular back button
                    Spacer(modifier = Modifier.height(10.dp))

                    // LARGE SELECTED MASJID PHOTO CARD (Fills down to search & finish)
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color(0xFFF2CA50).copy(alpha = 0.35f)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F151C)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(photoHeight)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (activeMasjid.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(activeMasjid.photoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = activeMasjid.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF0F151C))
                                )
                            }

                            // Dark Gradient Overlay at bottom of photo with Name, Location & ID badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0xFF0A0F14).copy(alpha = 0.95f)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        Text(
                                            text = activeMasjid.getLocalizedName(uiState.language),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF2CA50),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        val displayLoc = listOf(
                                            activeMasjid.getLocalizedArea(uiState.language),
                                            activeMasjid.city,
                                            activeMasjid.state
                                        ).filter { it.isNotBlank() }.joinToString(", ")
                                        if (displayLoc.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Place,
                                                    contentDescription = null,
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = displayLoc,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.85f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF1E2836).copy(alpha = 0.85f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color(0xFFF2CA50).copy(alpha = 0.4f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "#${activeMasjid.id}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF2CA50)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SEARCH BAR (In Photo Card Screen)
                    SearchBarContent(
                        searchQuery = searchQuery,
                        onSearchQueryChange = {
                            searchQuery = it
                            isSearchFocused = true
                        },
                        onSearchFocused = { isSearchFocused = true },
                        onVoiceInput = {
                            isSearchFocused = true
                            startVoiceInput()
                        }
                    )

                    // SEARCH RESULTS (If query entered)
                    if (hasQuery) {
                        Spacer(modifier = Modifier.height(14.dp))
                        SearchResultsList(
                            matchedList = matchedList,
                            searchQuery = searchQuery,
                            selectedMasjidId = activeMasjid.id,
                            language = uiState.language,
                            onSelect = { item -> userSelectedMasjid = item }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTONS: CANCEL & FINISH
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isChangeMode) {
                            OutlinedButton(
                                onClick = { onBack?.invoke() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFF475569)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF1E293B).copy(alpha = 0.60f),
                                    contentColor = Color(0xFFCBD5E1)
                                )
                            ) {
                                Text(
                                    text = "CANCEL",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        val isFinishEnabled = !isChangeMode || hasSelectionChanged

                        Button(
                            onClick = {
                                onSelectMasjid(activeMasjid)
                                onFinishSetup(activeMasjid.id)
                            },
                            enabled = isFinishEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFinishEnabled) Color(0xFFF2CA50) else Color(0xFF1E2836).copy(alpha = 0.50f),
                                contentColor = if (isFinishEnabled) Color(0xFF241A00) else Color(0xFF64748B),
                                disabledContainerColor = Color(0xFF1E2836).copy(alpha = 0.50f),
                                disabledContentColor = Color(0xFF64748B)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = if (isFinishEnabled) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .weight(if (isChangeMode) 1.25f else 1f)
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Finish",
                                    tint = if (isFinishEnabled) Color(0xFF241A00) else Color(0xFF64748B),
                                    modifier = Modifier.size(19.dp)
                                )
                                Text(
                                    text = "FINISH",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // STARTING PAGE: Clean, Professional, Lifted 25% Up from Bottom
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!hasQuery) {
                        // Shift matter 10% lower down
                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height((screenHeight * 0.06f)))
                    } else {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Light Glow Crescent Moon with Star
                    Box(
                        modifier = Modifier
                            .padding(bottom = 14.dp)
                            .size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(46.dp)) {
                            val r = size.minDimension / 2f
                            val c = Offset(size.width / 2f, size.height / 2f)

                            // Soft light aura glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF2CA50).copy(alpha = 0.50f),
                                        Color(0xFFF2CA50).copy(alpha = 0.18f),
                                        Color.Transparent
                                    ),
                                    center = c,
                                    radius = r * 1.55f
                                )
                            )

                            // Crescent moon
                            val moonPath = Path().apply {
                                addOval(Rect(center = c, radius = r * 0.95f))
                                val cutCenter = Offset(c.x + r * 0.44f, c.y - r * 0.28f)
                                val cutPath = Path().apply {
                                    addOval(Rect(center = cutCenter, radius = r * 0.86f))
                                }
                                op(this, cutPath, PathOperation.Difference)
                            }

                            drawPath(
                                path = moonPath,
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFFBE8),
                                        Color(0xFFF2CA50),
                                        Color(0xFFD4AF37)
                                    )
                                )
                            )

                            // Sparkling Star near Crescent Moon
                            val starCenter = Offset(c.x + r * 0.36f, c.y - r * 0.28f)
                            drawCircle(
                                color = Color(0xFFFFFCE8),
                                radius = 2.8.dp.toPx(),
                                center = starCenter
                            )
                        }
                    }

                    // 1. Arabic Greeting
                    Text(
                        text = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ ٱللَّهِ وَبَرَكَاتُهُ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE5C158),
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. English Greeting
                    Text(
                        text = "Assalamu Alaikum",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Hindi Greeting
                    Text(
                        text = "अस्सलाम वालेकुम",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD5DDE5),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Welcome to",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE1C561).copy(alpha = 0.90f),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Heavy Bold AZAN TIME in Graen Metal Font
                    Text(
                        text = "AZAN TIME",
                        fontFamily = GraenMetalFontFamily,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 2.sp,
                        style = TextStyle(
                            brush = GraenMetalGoldGradient,
                            shadow = GraenMetalGoldShadow
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Punch line under AZAN TIME
                    Text(
                        text = "Connect with your Masjid • Never miss a Jama'at",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF2CA50).copy(alpha = 0.90f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // SEARCH BAR
                    SearchBarContent(
                        searchQuery = searchQuery,
                        onSearchQueryChange = {
                            searchQuery = it
                            isSearchFocused = true
                        },
                        onSearchFocused = { isSearchFocused = true },
                        onVoiceInput = {
                            isSearchFocused = true
                            startVoiceInput()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Find and select line directly below search bar
                    Text(
                        text = "Find and select your local Masjid to get started",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    // Results list when searching on start page
                    if (hasQuery) {
                        Spacer(modifier = Modifier.height(14.dp))
                        SearchResultsList(
                            matchedList = matchedList,
                            searchQuery = searchQuery,
                            selectedMasjidId = null,
                            language = uiState.language,
                            onSelect = { item -> userSelectedMasjid = item }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        // 15% bottom space (10% lower than previous 25%)
                        Spacer(modifier = Modifier.height((screenHeight * 0.15f).coerceAtLeast(40.dp)))
                    }
                }
            }
        }
    }
}

/**
 * Reusable Search Bar Component
 */
@Composable
private fun SearchBarContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocused: () -> Unit,
    onVoiceInput: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(
                Color(0xFF0F172A).copy(alpha = 0.90f),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Color(0xFFF2CA50).copy(alpha = 0.45f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onSearchFocused() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFF2CA50),
                modifier = Modifier
                    .padding(start = 8.dp, end = 10.dp)
                    .size(22.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search Masjid Name, Area or ID...",
                        color = Color(0xFF94A3B8).copy(alpha = 0.75f),
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(Color(0xFFF2CA50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            if (it.isFocused) {
                                onSearchFocused()
                            }
                        }
                )
            }

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color(0xFFD0C5AF).copy(alpha = 0.75f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Color(0xFFF2CA50).copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        Color(0xFFF2CA50).copy(alpha = 0.35f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onVoiceInput() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = Color(0xFFF2CA50),
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

/**
 * Reusable Search Results List
 */
@Composable
private fun SearchResultsList(
    matchedList: List<MasjidItem>,
    searchQuery: String,
    selectedMasjidId: String?,
    language: String,
    onSelect: (MasjidItem) -> Unit
) {
    if (matchedList.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A).copy(alpha = 0.90f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF2CA50).copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mosque,
                    contentDescription = null,
                    tint = Color(0xFFF2CA50).copy(alpha = 0.65f),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "No masjid found for \"${searchQuery.trim()}\"",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Try searching by Area name or 9-digit Masjid ID",
                    fontSize = 11.5.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MATCHING MASAJID (${matchedList.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF2CA50),
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Tap to select",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }

        matchedList.forEach { item ->
            val isSel = item.id == selectedMasjidId
            Card(
                onClick = { onSelect(item) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) {
                        Color(0xFF14291E).copy(alpha = 0.95f)
                    } else {
                        Color(0xFF0F151C).copy(alpha = 0.85f)
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSel) Color(0xFF22C55E) else Color(0xFFF2CA50).copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isSel) Color(0xFF166534) else Color(0xFF1E2836).copy(alpha = 0.6f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mosque,
                            contentDescription = null,
                            tint = if (isSel) Color(0xFF86EFAC) else Color(0xFFF2CA50),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.getLocalizedName(language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color(0xFF86EFAC) else Color.White
                        )
                        val itemLoc = listOf(
                            item.getLocalizedArea(language),
                            item.city,
                            item.state
                        ).filter { it.isNotBlank() }.joinToString(", ")
                        if (itemLoc.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = itemLoc,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFD0C5AF).copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF1E2836).copy(alpha = 0.8f),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                Color(0xFFF2CA50).copy(alpha = 0.35f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${item.id}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF2CA50)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper to launch speech recognition intent
 */
private fun launchVoiceSearch(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak Masjid Name, Area or ID..."
            )
        }
        launcher.launch(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Speech recognition is not available on this device",
            Toast.LENGTH_SHORT
        ).show()
    }
}
