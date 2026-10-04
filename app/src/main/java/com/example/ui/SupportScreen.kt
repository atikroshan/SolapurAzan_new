package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import java.io.ByteArrayOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.LanguageCirclesRow
import com.example.R
import com.example.appBackground
import com.example.data.GoogleSheetMasjidSync
import com.example.islamicStarBackground
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SupportScreen(
    uiState: UIState,
    onBack: () -> Unit,
    onLangSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val primaryGold = Color(0xFFF3DE8E)
    val whatsappTarget = "919960171516"

    var showAddMasjidDialog by remember { mutableStateOf(false) }
    var showThanksDialog by remember { mutableStateOf(false) }
    var showRateUsDialog by remember { mutableStateOf(false) }
    var showOtherAmountDialog by remember { mutableStateOf(false) }
    var showUpiAppChooserDialog by remember { mutableStateOf(false) }
    var selectedContributionAmount by remember { mutableIntStateOf(250) }

    // Rating system: safely read saved stars with fallback across all types (float, int, string), default 0.0
    val prefs = remember { context.getSharedPreferences("azan_user_rating", Context.MODE_PRIVATE) }
    val initialRating = remember(prefs) {
        try {
            prefs.getFloat("saved_stars", 0f).toDouble()
        } catch (e1: Throwable) {
            try {
                prefs.getInt("saved_stars", 0).toDouble()
            } catch (e2: Throwable) {
                try {
                    prefs.getString("saved_stars", "0")?.toDoubleOrNull() ?: 0.0
                } catch (e3: Throwable) {
                    0.0
                }
            }
        }
    }
    var userRating by remember { mutableDoubleStateOf(initialRating) }
    var averageRating by remember { mutableDoubleStateOf(0.0) }
    var totalReviews by remember { mutableIntStateOf(0) }

    // Fetch live ratings from Google Sheet tab "rating" on load
    LaunchedEffect(Unit) {
        try {
            val (avg, count) = GoogleSheetMasjidSync.fetchRatingStats()
            if (count > 0 && avg > 0.0) {
                averageRating = avg
                totalReviews = count
            }
        } catch (t: Throwable) {
            // Never crash if network or sheet fails
        }
    }

    fun openWhatsApp() {
        try {
            val message = when (uiState.language) {
                "ur" -> "السلام علیکم! اذان ایپ کے بارے میں فیڈ بیک / مدد درکار ہے۔"
                "hi" -> "अस्सलाम-ओ-अलैकुम! अज़ान ऐप के बारे में सहायता / सुझाव।"
                else -> "Assalam-o-Alaikum! I need support / have feedback regarding Azan App."
            }
            val uri = Uri.parse("https://wa.me/$whatsappTarget?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$whatsappTarget"))
                context.startActivity(dial)
            } catch (e2: Exception) {
                Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchUpiForApp(packageName: String?, amount: Int) {
        val amStr = if (amount > 0) "&am=$amount" else ""
        val uri = Uri.parse("upi://pay?pa=9960171516@ybl&pn=A-tek%20Printing%20Press${amStr}&cu=INR")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (!packageName.isNullOrBlank()) {
            intent.setPackage(packageName)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(Intent.createChooser(fallbackIntent, "Pay with UPI"))
            } catch (e2: Exception) {
                Toast.makeText(context, "No UPI app found. Please scan the QR code.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun shareApp() {
        try {
            val shareText = when (uiState.language) {
                "ur" -> "🕌 اذان ٹائم ایپ - سولاپور شہر کی مساجد کے اوقاتِ نماز و اذان!\nثوابِ جاریہ کی نیت سے اپنے اہل و عیال اور احباب کو شیئر کریں:\nhttps://drive.google.com/drive/folders/1zEbq9A1LvGk-etUfVcMHUhKd6bqVH01n?usp=sharing"
                "hi" -> "🕌 अज़ान टाइम ऐप - सोलापुर शहर की मस्जिदों के सही अज़ान और नमाज़ के औक़ात!\nसवाब-ए-जारिया के लिए अपने दोस्तों और परिवार के साथ शेयर करें:\nhttps://drive.google.com/drive/folders/1zEbq9A1LvGk-etUfVcMHUhKd6bqVH01n?usp=sharing"
                else -> "🕌 Azan Time App - Accurate prayer & Azan timings for Solapur City Masajid!\nShare with your family & friends to earn Sawab-e-Jariyah:\nhttps://drive.google.com/drive/folders/1zEbq9A1LvGk-etUfVcMHUhKd6bqVH01n?usp=sharing"
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Azan Time Solapur")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share Azan Time App"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open share menu", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF040C08))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Top Bar: Back Button (Left) + Same 3 Language Buttons from Home Page (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0D1E17), CircleShape)
                        .border(1.dp, primaryGold.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Same 3 language button copied from Home Page
                LanguageCirclesRow(
                    currentLang = uiState.language,
                    onLangSelect = onLangSelect
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Scrollable Content matching the user reference screenshot
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top Center App Logo & Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0E2F23), Color(0xFF061A13))
                                )
                            )
                            .border(1.dp, Color(0xFFE5B842).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.foreground),
                            contentDescription = "Azan App Icon",
                            modifier = Modifier.size(46.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AZAN TIME",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryGold,
                        letterSpacing = 2.5.sp
                    )
                }

                // 2. Divided 2-Column Slot: Left = Support & Feedback, Right = Request Add Masjid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: Support & Feedback (Instant WhatsApp)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 115.dp)
                            .clickable { openWhatsApp() },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF091712)),
                        border = BorderStroke(1.dp, Color(0xFF1B3B2E))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Clean WhatsApp icon without any surrounding green dot/circle
                            Box(
                                modifier = Modifier.size(34.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "WhatsApp",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "سپورٹ اور فیڈبیک"
                                    "hi" -> "सपोर्ट और सुझाव"
                                    else -> "Support & Feedback"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "فوری واٹس ایپ"
                                    "hi" -> "इंस्टेंट व्हाट्सएप"
                                    else -> "Instant WhatsApp"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4ADE80),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    // Right Column: Request Add Masjid (Submit Local Timings)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 115.dp)
                            .clickable { showAddMasjidDialog = true },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF091712)),
                        border = BorderStroke(1.dp, Color(0xFF1B3B2E))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.size(34.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Mosque,
                                    contentDescription = "Request Add Masjid",
                                    tint = primaryGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "نئی مسجد شامل کریں"
                                    "hi" -> "नई मस्जिद जोड़ें"
                                    else -> "Request Add Masjid"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "اوقاتِ نماز درج کریں"
                                    "hi" -> "लोकल टाइमिंग भेजें"
                                    else -> "Submit Local Timings"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE5B842),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }

                // 3. Main Donation Card (Sadaqah Jariyah)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071711)),
                    border = BorderStroke(1.dp, Color(0xFF1B3B2E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sadaqah Jariyah Pill Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF162D22))
                                .border(1.dp, Color(0xFF2E533F), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SADAQAH JARIYAH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Subtitle
                        Text(
                            text = "Your voluntary donation covers server costs, API maintenance, and accurate prayer broadcast across Solapur City.",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.72f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code in Rounded White Card
                        Box(
                            modifier = Modifier
                                .size(210.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .border(1.5.dp, primaryGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.qr_support_donate),
                                contentDescription = "Donation QR Code",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scan with UPI Apps
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(13.dp)) {
                                drawRect(color = primaryGold, style = Stroke(width = 1.4.dp.toPx()))
                                drawRect(
                                    color = primaryGold,
                                    size = androidx.compose.ui.geometry.Size(size.width * 0.45f, size.height * 0.45f),
                                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.275f, size.height * 0.275f)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scan with Google Pay, PhonePe, or Paytm",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryGold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Contribution Amount Section
                        Text(
                            text = "Choose a Contribution Amount",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 5 Buttons: ₹50, ₹100, ₹250, ₹500, Other
                        val amounts = listOf(50, 100, 250, 500)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            amounts.forEach { amt ->
                                val isSelected = selectedContributionAmount == amt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFFE5B842) else Color(0xFF0F261C))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color(0xFFFFE082) else Color(0xFF224836),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedContributionAmount = amt
                                            showUpiAppChooserDialog = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "₹$amt",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF0F1713) else primaryGold
                                    )
                                }
                            }

                            // 5th Button: Other
                            val isOtherSelected = !amounts.contains(selectedContributionAmount)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isOtherSelected) Color(0xFFE5B842) else Color(0xFF0F261C))
                                    .border(
                                        width = 1.dp,
                                        color = if (isOtherSelected) Color(0xFFFFE082) else Color(0xFF224836),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        showOtherAmountDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isOtherSelected) "₹$selectedContributionAmount" else "Other",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOtherSelected) Color(0xFF0F1713) else primaryGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pay via UPI App button - Opens 3 App Options (Google Pay, PhonePe, Paytm)
                        Button(
                            onClick = { showUpiAppChooserDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF123425)),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Text(
                                text = "Pay ₹$selectedContributionAmount via UPI (GooglePay, PhonePe, Paytm)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGold
                            )
                        }
                    }
                }

                // 4. Rating & Share App Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071711)),
                    border = BorderStroke(1.dp, Color(0xFF1B3B2E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Top Row: Rate Us Button (Left) & User Given Stars (Right / Beside it)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Rate Us Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF1A271B))
                                    .border(1.dp, Color(0xFFE5B842).copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .clickable { showRateUsDialog = true }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Rate Us →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryGold
                                )
                            }

                            // Beside Rate Us: Given star rating
                            if (userRating > 0.0) {
                                val starCount = userRating.toInt().coerceIn(1, 5)
                                val starEmojis = "⭐".repeat(starCount)
                                Text(
                                    text = "$starEmojis (${String.format(Locale.US, "%.1f", userRating)} ★)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Below Rate Us: App Rating Section
                        val appRatingDisplay = if (averageRating > 0.0) averageRating else 4.8
                        Column {
                            Text(
                                text = "App Rating",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1f", appRatingDisplay),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                // 5 Golden App Rating Stars
                                Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                    for (i in 1..5) {
                                        val fillRatio = when {
                                            appRatingDisplay >= i -> 1f
                                            appRatingDisplay >= i - 0.5 -> 0.5f
                                            else -> 0f
                                        }
                                        StarIconDisplay(fillRatio = fillRatio, size = 16.dp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            // Below App Rating: Trusted by 1000+ worshippers
                            Text(
                                text = "Trusted by 1000+ worshippers",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Share App & Earn Sawab Button
                        Button(
                            onClick = { shareApp() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3B29)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.55f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share App & Earn Sawab",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 5. Version Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF0A1B1F))
                        .border(1.dp, Color(0xFF163E44), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Version v${BuildConfig.VERSION_NAME}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6EE7B7)
                        )
                    }
                }

                // 6. Devotion Footer
                Text(
                    text = "Made with devotion • Powered by @tek",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = primaryGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }

    // Custom Amount Dialog (when "Other" contribution is clicked)
    if (showOtherAmountDialog) {
        var customAmountText by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showOtherAmountDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E17)),
                border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Enter Contribution Amount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { customAmountText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Amount (₹)", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("e.g. 1000", color = Color.White.copy(alpha = 0.3f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showOtherAmountDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Color.White)
                        }
                        Button(
                            onClick = {
                                val entered = customAmountText.toIntOrNull()
                                if (entered != null && entered > 0) {
                                    selectedContributionAmount = entered
                                    showOtherAmountDialog = false
                                    showUpiAppChooserDialog = true
                                } else {
                                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5B842)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Proceed", color = Color(0xFF0F1713), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Rate Us Interactive Dialog (Supports Half-Stars e.g. 2.5 and Saves to Google Sheet)
    if (showRateUsDialog) {
        var tempRating by remember { mutableDoubleStateOf(if (userRating > 0.0) userRating else 5.0) }
        var isSubmitting by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showRateUsDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E17)),
                border = BorderStroke(1.5.dp, primaryGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Rate Azan Time",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                    Text(
                        text = "Tap left half of a star for half point (e.g. 2.5, 3.5, 4.5) or right half for full star.",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center
                    )

                    // Large Interactive Half-Star Rating Bar
                    InteractiveHalfStarRatingBar(
                        rating = tempRating,
                        onRatingSelected = { selected ->
                            tempRating = selected
                        },
                        starSize = 36.dp,
                        spacing = 8.dp
                    )

                    Text(
                        text = String.format(Locale.US, "%.1f / 5.0 Stars", tempRating),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )

                    Button(
                        onClick = {
                            isSubmitting = true
                            userRating = tempRating
                            try {
                                prefs.edit().putFloat("saved_stars", tempRating.toFloat()).apply()
                            } catch (e: Throwable) {
                                // Ignore
                            }
                            coroutineScope.launch {
                                try {
                                    GoogleSheetMasjidSync.submitRating(tempRating)
                                } catch (t: Throwable) {
                                    // Ignore
                                }
                                isSubmitting = false
                                showRateUsDialog = false
                            }
                            Toast.makeText(
                                context,
                                "⭐⭐⭐⭐⭐ JazakAllah Khair for rating ${String.format(Locale.US, "%.1f", tempRating)} stars!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5B842)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color(0xFF0F1713),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving...", color = Color(0xFF0F1713), fontWeight = FontWeight.Bold)
                        } else {
                            Text("Submit Rating", color = Color(0xFF0F1713), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    // UPI App Chooser Dialog (Google Pay, PhonePe, Paytm, Other)
    if (showUpiAppChooserDialog) {
        UpiAppChooserDialog(
            amount = selectedContributionAmount,
            onDismiss = { showUpiAppChooserDialog = false },
            onAppSelected = { pkg ->
                showUpiAppChooserDialog = false
                launchUpiForApp(pkg, selectedContributionAmount)
            }
        )
    }

    // Add Masjid Form Dialog (Auto sends directly to email without redirecting)
    if (showAddMasjidDialog) {
        AddMasjidAutoDialog(
            language = uiState.language,
            onDismiss = { showAddMasjidDialog = false },
            onSubmitted = {
                showAddMasjidDialog = false
                showThanksDialog = true
            }
        )
    }

    // Thanks Popup in Green with Thumb 👍 Icon
    if (showThanksDialog) {
        AlertDialog(
            onDismissRequest = { showThanksDialog = false },
            containerColor = Color(0xFF0F241A),
            shape = RoundedCornerShape(22.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape)
                        .border(2.dp, Color(0xFF10B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Thanks",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    text = when (uiState.language) {
                        "ur" -> "درخواست موصول ہوگئی! 👍"
                        "hi" -> "अनुरोध प्राप्त हुआ! 👍"
                        else -> "Request Received! 👍"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color(0xFF34D399),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = when (uiState.language) {
                        "ur" -> "مسجد کی تفصیلات گوگل شیٹ (Sheet1) اور فوٹو گوگل ڈرائیو میں کامیابی کے ساتھ درج کر دی گئی ہیں۔ ایڈمن جانچ کے بعد جلد ایکٹیو کر دیں گے۔"
                        "hi" -> "मस्जिद की जानकारी गूगल शीट (Sheet1) और फोटो गूगल ड्राइव में सफलतापूर्वक दर्ज कर दी गई है। जांच के बाद इसे जल्द ही ऐप में लाइव कर दिया जाएगा।"
                        else -> "Masjid details successfully submitted to Google Sheet (Sheet1) and photo to Google Drive! ID & Password can now be added in the sheet."
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showThanksDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (uiState.language) {
                            "ur" -> "ٹھیک ہے"
                            "hi" -> "ठीक है"
                            else -> "OK"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun AddMasjidAutoDialog(
    language: String,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var masjidName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminContact by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var showScriptUpdateHelpDialog by remember { mutableStateOf(false) }
    var serverErrorDetail by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedPhotoUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFFF3DE8E).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (language) {
                            "ur" -> "نئی مسجد شامل کریں"
                            "hi" -> "नई मस्जिद जोड़ें"
                            else -> "Add New Masjid"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF3DE8E)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Field 1: Name of Masjid
                OutlinedTextField(
                    value = masjidName,
                    onValueChange = { masjidName = it; errorMessage = null },
                    label = { Text("Name of Masjid *") },
                    placeholder = { Text("e.g. Markaz Masjid, Jama Masjid") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF3DE8E),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFFF3DE8E),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field 2: Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; errorMessage = null },
                    label = { Text("Location / City / Area *") },
                    placeholder = { Text("e.g. Solapur, Muslim Peth") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF3DE8E),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFFF3DE8E),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field 3: Admin Name
                OutlinedTextField(
                    value = adminName,
                    onValueChange = { adminName = it; errorMessage = null },
                    label = { Text("Admin Name *") },
                    placeholder = { Text("e.g. Haji Abdul Rahman") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF3DE8E),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFFF3DE8E),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field 4: Admin Contact Number
                OutlinedTextField(
                    value = adminContact,
                    onValueChange = { adminContact = it; errorMessage = null },
                    label = { Text("Admin Contact Number *") },
                    placeholder = { Text("e.g. 9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF3DE8E),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFFF3DE8E),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field 5: Photo Browser Button
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Masjid Photo (Optional):",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Photo",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPhotoUri != null) "Photo Selected ✓ Change" else "Browse / Upload Masjid Photo",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    if (selectedPhotoUri != null) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Masjid Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Submit Request Button (Uploads photo to Google Drive & inserts 9 rows into Sheet1)
                Button(
                    onClick = {
                        if (masjidName.isBlank()) {
                            errorMessage = "Please enter Masjid Name"
                            return@Button
                        }
                        if (location.isBlank()) {
                            errorMessage = "Please enter Location"
                            return@Button
                        }
                        if (adminName.isBlank()) {
                            errorMessage = "Please enter Admin Name"
                            return@Button
                        }
                        if (adminContact.isBlank()) {
                            errorMessage = "Please enter Admin Contact Number"
                            return@Button
                        }

                        isSending = true
                        coroutineScope.launch {
                            var photoBase64: String? = null
                            val cleanMasjidName = masjidName.trim()
                            if (selectedPhotoUri != null) {
                                try {
                                    val inputStream = context.contentResolver.openInputStream(selectedPhotoUri!!)
                                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                    inputStream?.close()
                                    if (originalBitmap != null) {
                                        val maxDim = 1024
                                        val scale = (maxDim.toFloat() / Math.max(originalBitmap.width, originalBitmap.height)).coerceAtMost(1f)
                                        val scaledBitmap = if (scale < 1f) {
                                            Bitmap.createScaledBitmap(
                                                originalBitmap,
                                                (originalBitmap.width * scale).toInt(),
                                                (originalBitmap.height * scale).toInt(),
                                                true
                                            )
                                        } else {
                                            originalBitmap
                                        }
                                        val baos = ByteArrayOutputStream()
                                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                                        photoBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
                                    }
                                } catch (e: Throwable) {
                                    // Proceed without photo if encoding fails
                                }
                            }

                            val result = com.example.data.GoogleSheetMasjidSync.submitMasjidRequestAuto(
                                name = cleanMasjidName,
                                location = location.trim(),
                                adminName = adminName.trim(),
                                contact = adminContact.trim(),
                                photoBase64 = photoBase64,
                                photoName = "$cleanMasjidName.jpg"
                            )
                            isSending = false
                            if (result.first) {
                                val successMsg = when (language) {
                                    "ur" -> "درخواست اور تصویر گوگل شیٹ (Sheet1) میں محفوظ ہو گئی۔ جزاک اللہ خیر۔"
                                    "hi" -> "अनुरोध और फोटो गूगल शीट (Sheet1) में सेव हो गया। जज़ाकल्लाह ख़ैर।"
                                    else -> "Masjid request & photo saved to Sheet1 successfully! JazakAllah khair."
                                }
                                Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
                                onSubmitted()
                            } else {
                                errorMessage = result.second
                                serverErrorDetail = result.second
                                showScriptUpdateHelpDialog = true
                            }
                        }
                    },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Uploading to Drive & Sheet...",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (language) {
                                "ur" -> "درخواست ارسال کریں"
                                "hi" -> "अनुरोध भेजें"
                                else -> "Send Request"
                            },
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (showScriptUpdateHelpDialog) {
            AlertDialog(
                onDismissRequest = { showScriptUpdateHelpDialog = false },
                title = {
                    Text(
                        text = "Google Apps Script Update Required",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF3DE8E),
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Server Error: $serverErrorDetail",
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aapke Google Sheet ke Apps Script me naya code update nahi hai. Sheet me naya code daal kar 'Deploy > New Version' karein tab data & photo Sheet1 me jayega.",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                        
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("AppsScriptCode", com.example.data.GoogleSheetMasjidSync.APPS_SCRIPT_SAMPLE_CODE)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Apps Script Code Copied to Clipboard! ✓", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "📋 Copy Latest Apps Script Code",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Button(
                            onClick = {
                                val cleanName = masjidName.trim()
                                val cleanLoc = location.trim()
                                val cleanAdmin = adminName.trim()
                                val cleanContact = adminContact.trim()
                                val msg = "🕌 *Request Add Masjid*\n*Name:* $cleanName\n*Address:* $cleanLoc\n*Admin:* $cleanAdmin\n*Contact:* $cleanContact"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=919960171516&text=" + Uri.encode(msg))
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💬 Send Request via WhatsApp",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showScriptUpdateHelpDialog = false }) {
                        Text("OK", color = Color(0xFFF3DE8E), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun StarIconDisplay(
    fillRatio: Float, // 0f = outline, 0.5f = half filled, 1f = fully filled
    size: androidx.compose.ui.unit.Dp = 20.dp,
    tint: Color = Color(0xFFFFD700),
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(size)) {
        val path = Path().apply {
            val cx = this@Canvas.size.width / 2f
            val cy = this@Canvas.size.height / 2f
            val outerR = this@Canvas.size.minDimension / 2f
            val innerR = outerR * 0.42f
            for (p in 0 until 10) {
                val r = if (p % 2 == 0) outerR else innerR
                val angle = Math.toRadians((p * 36 - 90).toDouble())
                val x = cx + (r * Math.cos(angle)).toFloat()
                val y = cy + (r * Math.sin(angle)).toFloat()
                if (p == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        if (fillRatio >= 1f) {
            drawPath(path, color = tint, style = Fill)
        } else if (fillRatio >= 0.5f) {
            // Half-star: clip left half
            clipRect(right = this.size.width / 2f) {
                drawPath(path, color = tint, style = Fill)
            }
            // Full star outline
            drawPath(path, color = tint.copy(alpha = 0.5f), style = Stroke(width = 1.5.dp.toPx()))
        } else {
            // Empty star outline
            drawPath(path, color = tint.copy(alpha = 0.35f), style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}

@Composable
fun InteractiveHalfStarRatingBar(
    rating: Double,
    onRatingSelected: (Double) -> Unit,
    starSize: androidx.compose.ui.unit.Dp = 24.dp,
    tint: Color = Color(0xFFFFD700),
    spacing: androidx.compose.ui.unit.Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val fillRatio = when {
                rating >= i -> 1f
                rating >= i - 0.5 -> 0.5f
                else -> 0f
            }
            Box(
                modifier = Modifier.size(starSize),
                contentAlignment = Alignment.Center
            ) {
                // Drawn Star vector
                StarIconDisplay(
                    fillRatio = fillRatio,
                    size = starSize,
                    tint = tint
                )
                // Left half clickable for (i - 0.5), right half clickable for (i.toDouble())
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                onRatingSelected(i - 0.5)
                            }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                onRatingSelected(i.toDouble())
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun UpiAppChooserDialog(
    amount: Int,
    onDismiss: () -> Unit,
    onAppSelected: (String?) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B15)),
            border = BorderStroke(1.5.dp, Color(0xFFE5B842).copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pay via UPI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF3DE8E)
                        )
                        Text(
                            text = "Amount: ₹$amount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF34D399)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                Text(
                    text = "Select your payment app:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.align(Alignment.Start)
                )

                // 1. Google Pay
                UpiAppOptionItem(
                    name = "Google Pay",
                    subtitle = "GPay UPI",
                    badgeColor = Color(0xFF1A73E8),
                    badgeText = "GPay",
                    onClick = { onAppSelected("com.google.android.apps.nbu.paisa.user") }
                )

                // 2. PhonePe
                UpiAppOptionItem(
                    name = "PhonePe",
                    subtitle = "PhonePe UPI",
                    badgeColor = Color(0xFF5F259F),
                    badgeText = "Pe",
                    onClick = { onAppSelected("com.phonepe.app") }
                )

                // 3. Paytm
                UpiAppOptionItem(
                    name = "Paytm",
                    subtitle = "Paytm Payments Bank & UPI",
                    badgeColor = Color(0xFF00B9F5),
                    badgeText = "Paytm",
                    onClick = { onAppSelected("net.one97.paytm") }
                )

                // 4. Other UPI App (System Chooser)
                UpiAppOptionItem(
                    name = "Other UPI App",
                    subtitle = "BHIM, Cred, Amazon Pay, etc.",
                    badgeColor = Color(0xFF10B981),
                    badgeText = "UPI",
                    onClick = { onAppSelected(null) }
                )
            }
        }
    }
}

@Composable
fun UpiAppOptionItem(
    name: String,
    subtitle: String,
    badgeColor: Color,
    badgeText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF122820)),
        border = BorderStroke(1.dp, Color(0xFF1E4636))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = Color(0xFFF3DE8E),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
