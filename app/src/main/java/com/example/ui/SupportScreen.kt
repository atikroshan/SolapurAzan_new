package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    // Rating system: default starts with 0 stars
    val prefs = remember { context.getSharedPreferences("azan_user_rating", Context.MODE_PRIVATE) }
    var userRating by remember { mutableDoubleStateOf(prefs.getFloat("saved_stars", 0f).toDouble()) }
    var averageRating by remember { mutableDoubleStateOf(4.8) }
    var totalReviews by remember { mutableIntStateOf(0) }

    // Fetch live ratings from Google Sheet tab "rating" on load
    LaunchedEffect(Unit) {
        val (avg, count) = GoogleSheetMasjidSync.fetchRatingStats()
        if (count > 0) {
            averageRating = avg
            totalReviews = count
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .appBackground()
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
                        .background(Color(0xFF1E293B), CircleShape)
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

            // Non-scrollable compact support content to fit completely on-screen without scrolling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. App Title Header (As-Is Clean Logo + Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Azan App Icon",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "AZAN TIME",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryGold,
                        letterSpacing = 1.5.sp
                    )
                }

                // 2. Divided 2-Column Slot: Left = WhatsApp Support, Right = Request Add Masjid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Column: Official WhatsApp Support
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clickable { openWhatsApp() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E17)),
                        border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.55f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = "WhatsApp",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "سپورٹ اور فیڈبیک"
                                    "hi" -> "सपोर्ट और सुझाव"
                                    else -> "Support & Feedback"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    // Right Column: Request Add New Masjid (Exact Mosque Icon from Home Screen)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clickable { showAddMasjidDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                        border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.55f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mosque,
                                contentDescription = "Request Add Masjid",
                                tint = primaryGold,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "نئی مسجد شامل کریں"
                                    "hi" -> "नई मस्जिद जोड़ें"
                                    else -> "Request Add Masjid"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }

                // 3. Scanner Section: Clean on background (Enlarged QR to 200dp as requested)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "For support plz scan n donate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryGold,
                        textAlign = TextAlign.Center
                    )

                    // Flat QR Image with clean rounded border - Enlarge to 200dp
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.5.dp, primaryGold.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qr_support_donate),
                            contentDescription = "Donation QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Pay via UPI button
                    Button(
                        onClick = {
                            try {
                                val upiUri = Uri.parse("upi://pay?pa=9960171516@ybl&pn=A-tek%20Printing%20Press&cu=INR")
                                val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Scan QR with PhonePe / GPay", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.4f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = primaryGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pay via UPI (PhonePe / GPay)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryGold
                        )
                    }
                }

                // 5. Rating Section: Slim & Professional one-card layout
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "ایپ ریٹنگ:"
                                    "hi" -> "ऐप रेटिंग:"
                                    else -> "Rating:"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", if (userRating > 0) userRating.toDouble() else averageRating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD700)
                            )
                        }

                        // Interactive Star Rating (5 empty stars)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures { change, dragAmount ->
                                        change.consume()
                                        val newRating = (userRating + (dragAmount / 50.0)).coerceIn(0.0, 5.0)
                                        userRating = newRating
                                        prefs.edit().putFloat("saved_stars", newRating.toFloat()).apply()
                                    }
                                }
                                .clickable {
                                    coroutineScope.launch {
                                        GoogleSheetMasjidSync.submitRating(userRating.toInt())
                                    }
                                    Toast.makeText(
                                        context,
                                        "⭐⭐⭐⭐⭐ Thank you for rating ${"%.1f".format(Locale.US, userRating)} stars!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        ) {
                            for (i in 1..5) {
                                val starFill = (userRating - (i - 1)).coerceIn(0.0, 1.0)
                                Box(modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700).copy(alpha = 0.35f)
                                    )
                                    if (starFill > 0) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.clip(
                                                androidx.compose.ui.graphics.RectangleShape
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Show selected rating text
                Text(
                    text = "Rating: ${"%.1f".format(Locale.US, userRating)}",
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp, start = 12.dp)
                )

                // 6. Version Number & Powered by @tek
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "v${BuildConfig.VERSION_NAME}",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Powered by @tek",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                }
            }
        }
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
                        "ur" -> "جزاک اللہ خیراً! 👍"
                        "hi" -> "धन्यवाद! 👍"
                        else -> "Thank You! 👍"
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
                        "ur" -> "آپ کی مسجد کی تفصیلات کامیابی کے ساتھ atikroshan@gmail.com پر روانہ کر دی گئی ہیں۔ جانچ کے بعد جلد ایپ میں شامل کر دی جائے گی۔"
                        "hi" -> "आपकी मस्जिद की जानकारी atikroshan@gmail.com पर भेज दी गई है। जांच के बाद इसे जल्द ही ऐप में जोड़ दिया जाएगा।"
                        else -> "Your masjid request has been automatically sent to atikroshan@gmail.com. We will verify and add it to the Azan app soon!"
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
    val coroutineScope = rememberCoroutineScope()
    var masjidName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminContact by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }

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

                // Send Button (Direct Auto Send to Email without app redirect)
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
                            GoogleSheetMasjidSync.submitMasjidRequestAuto(
                                name = masjidName,
                                location = location,
                                adminName = adminName,
                                contact = adminContact
                            )
                            isSending = false
                            onSubmitted()
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
                            text = "Sending...",
                            fontSize = 13.5.sp,
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
    }
}
