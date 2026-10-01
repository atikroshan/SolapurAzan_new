package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.LanguageToggleRow
import com.example.data.MasjidItem
import com.example.data.getLocalizedName
import com.example.data.getLocalizedArea
import com.example.data.getLocalizedCity

private class MasjidSelectorStrings(val lang: String) {
    val timetableHeader = when (lang) {
        "ur" -> "مسجد نظام الاوقات"
        "hi" -> "मस्जिद समय सारणी"
        else -> "MASJID TIMETABLE"
    }
    val countBadge = when (lang) {
        "ur" -> "100+ مساجد"
        "hi" -> "100+ मस्जिदें"
        else -> "100+ Masajid"
    }
    val changeBtn = when (lang) {
        "ur" -> "تبدیل کریں"
        "hi" -> "बदलें"
        else -> "Change"
    }
    val selectMasjidTitle = when (lang) {
        "ur" -> "مسجد منتخب کریں"
        "hi" -> "मस्जिद चुनें"
        else -> "SELECT MASJID"
    }
    val selectMasjidSubtitle = when (lang) {
        "ur" -> "100+ مساجد مقامی نماز کے اوقات کے ساتھ"
        "hi" -> "100+ मस्जिदें स्थानीय नमाज़ समय सारणी के साथ"
        else -> "100+ Masajid with Local Namaz Timetable"
    }
    val searchPlaceholder = when (lang) {
        "ur" -> "مسجد کا نام، علاقہ یا ID تلاش کریں..."
        "hi" -> "मस्जिद का नाम, इलाका या ID खोजें..."
        else -> "Search by masjid name, area or ID..."
    }
    fun showingCount(count: Int): String = when (lang) {
        "ur" -> "$count مساجد دکھائی جا رہی ہیں"
        "hi" -> "$count मस्जिदें दिखाई जा रही हैं"
        else -> "Showing $count Masajid"
    }
    val jumahLabel = when (lang) {
        "ur" -> "جمعہ"
        "hi" -> "जुमा"
        else -> "Jum'ah"
    }
    val azanLabel = when (lang) {
        "ur" -> "اذان"
        "hi" -> "अज़ान"
        else -> "Azan"
    }
    val jammatLabel = when (lang) {
        "ur" -> "جماعت"
        "hi" -> "जमात"
        else -> "Jammat"
    }
    val areaLabel = when (lang) {
        "ur" -> "علاقہ"
        "hi" -> "इलाका"
        else -> "Area"
    }
}

@Composable
fun MasjidSelectorDropdown(
    selectedMasjid: MasjidItem,
    allMasajid: List<MasjidItem>,
    language: String,
    onSelectMasjid: (MasjidItem) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onChangeClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val strings = remember(language) { MasjidSelectorStrings(language) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (onChangeClick != null) onChangeClick() else showDialog = true
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111726).copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Mosque Icon + Masjid Name and Location below it
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                        .border(1.dp, Color(0xFFF3DE8E).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mosque,
                        contentDescription = "Mosque",
                        tint = Color(0xFFF3DE8E),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = selectedMasjid.getLocalizedName(language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF3DE8E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val locArea = selectedMasjid.getLocalizedArea(language).ifBlank { selectedMasjid.area }
                    val locCity = selectedMasjid.getLocalizedCity(language).ifBlank { selectedMasjid.city }
                    val addressText = when {
                        locArea.isNotBlank() && locCity.isNotBlank() && !locArea.contains(locCity, ignoreCase = true) ->
                            "$locArea, $locCity"
                        locArea.isNotBlank() -> locArea
                        locCity.isNotBlank() -> locCity
                        else -> ""
                    }

                    if (addressText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFFE5A93C),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = addressText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Right: Change Button in the exact same line
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFF3DE8E).copy(alpha = 0.5f)),
                modifier = Modifier.clickable {
                    if (onChangeClick != null) onChangeClick() else showDialog = true
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = strings.changeBtn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF3DE8E)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select",
                        tint = Color(0xFFF3DE8E),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        MasjidSelectionDialog(
            selectedMasjid = selectedMasjid,
            allMasajid = allMasajid,
            language = language,
            onSelectMasjid = {
                onSelectMasjid(it)
                showDialog = false
            },
            onLanguageSelect = onLanguageSelect,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun MasjidSelectionDialog(
    selectedMasjid: MasjidItem,
    allMasajid: List<MasjidItem>,
    language: String,
    onSelectMasjid: (MasjidItem) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val strings = remember(language) { MasjidSelectorStrings(language) }

    val filteredMasajid = remember(searchQuery, allMasajid, language) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            allMasajid
        } else {
            allMasajid.filter { m ->
                m.name.lowercase().contains(q) ||
                m.area.lowercase().contains(q) ||
                m.city.lowercase().contains(q) ||
                m.state.lowercase().contains(q) ||
                m.id.lowercase().contains(q) ||
                m.getLocalizedName(language).lowercase().contains(q) ||
                m.getLocalizedArea(language).lowercase().contains(q) ||
                m.getLocalizedCity(language).lowercase().contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1524)),
            border = BorderStroke(1.5.dp, Color(0xFFE5A93C).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1E293B), CircleShape)
                                .border(1.dp, Color(0xFFF3DE8E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mosque,
                                contentDescription = null,
                                tint = Color(0xFFF3DE8E),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.selectMasjidTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF3DE8E),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = strings.selectMasjidSubtitle,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            strings.searchPlaceholder,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.45f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFF3DE8E),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFF3DE8E),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = Color(0xFF172033),
                        unfocusedContainerColor = Color(0xFF172033)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // User Requirement: "All delhi mumbai sab button nikalo, aur all delhi ke jagah home page ka language button copy paste karo waha language change karne par page ka language change karo"
                LanguageToggleRow(currentLang = language, onLangSelect = onLanguageSelect)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.showingCount(filteredMasajid.size),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
                )

                // List of Masajid
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMasajid, key = { it.id }) { masjid ->
                        val isCurrent = masjid.id == selectedMasjid.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectMasjid(masjid) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) Color(0xFF1E2B45) else Color(0xFF141C2E)
                            ),
                            border = BorderStroke(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) Color(0xFFF3DE8E) else Color.White.copy(alpha = 0.08f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                            .size(38.dp)
                                            .background(
                                                if (isCurrent) Color(0xFFF3DE8E).copy(alpha = 0.15f) else Color(0xFF1E293B),
                                                CircleShape
                                            )
                                            .border(
                                                1.dp,
                                                if (isCurrent) Color(0xFFF3DE8E) else Color.White.copy(alpha = 0.15f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Mosque,
                                            contentDescription = null,
                                            tint = if (isCurrent) Color(0xFFF3DE8E) else Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        // 1. Masjid Name
                                        Text(
                                            text = masjid.getLocalizedName(language),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color(0xFFF3DE8E) else Color.White
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        // 2. Area
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFFE5A93C),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "${strings.areaLabel}: ${masjid.getLocalizedArea(language)}, ${masjid.getLocalizedCity(language)}",
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.85f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // 3. ID Tag
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                                .border(1.dp, Color(0xFFF3DE8E).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ID: #${masjid.id}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF3DE8E)
                                            )
                                        }
                                    }
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFFE5A93C), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(16.dp)
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
}
