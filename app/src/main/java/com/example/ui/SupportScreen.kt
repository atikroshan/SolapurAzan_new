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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.islamicStarBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupportScreen(
    uiState: UIState,
    onBack: () -> Unit,
    onLangSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryGold = Color(0xFFF3DE8E)
    val whatsappNumber = "+919960171516"
    val rawPhone = "919960171516"

    var showAddMasjidDialog by remember { mutableStateOf(false) }
    var showThanksDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableIntStateOf(5) }
    var showRatingToast by remember { mutableStateOf(false) }

    fun openWhatsApp() {
        try {
            val message = when (uiState.language) {
                "ur" -> "السلام علیکم! اذان ایپ کے بارے میں فیڈ بیک / مدد درکار ہے۔"
                "hi" -> "अस्सलाम-ओ-अलैकुम! अज़ान ऐप के बारे में सहायता / सुझाव।"
                else -> "Assalam-o-Alaikum! I need support / have feedback regarding Azan App."
            }
            val uri = Uri.parse("https://wa.me/$rawPhone?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$whatsappNumber"))
                context.startActivity(dial)
            } catch (e2: Exception) {
                Toast.makeText(context, "WhatsApp: $whatsappNumber", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .appBackground()
            .islamicStarBackground(primaryGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Support Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. From top ~30% of screen: App Icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFF3DE8E), Color(0xFFD49B37))
                                )
                            )
                            .border(2.dp, Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Azan App Icon",
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = "AZAN TIME",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when (uiState.language) {
                            "ur" -> "شولاپور و آس پاس کی مساجد کے اوقات"
                            "hi" -> "सोलापुर व आसपास की मस्जिदों के औक़ात"
                            else -> "Offline Azan & Masjid Timetable"
                        },
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // 2. WhatsApp Icon & Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openWhatsApp() },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E17)),
                    border = BorderStroke(1.5.dp, Color(0xFF25D366).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF25D366), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = "WhatsApp",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "For support / feedback drop ur message on whatsapp",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 17.sp
                            )
                            Text(
                                text = whatsappNumber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF25D366)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 3. QR Section: Heading "For support plz scan n donate"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827).copy(alpha = 0.95f)),
                    border = BorderStroke(1.5.dp, primaryGold.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "For support plz scan n donate",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryGold,
                            textAlign = TextAlign.Center
                        )

                        // QR Code image
                        Box(
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.qr_support_donate),
                                contentDescription = "Donation QR Code",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Direct UPI Pay button
                        Button(
                            onClick = {
                                try {
                                    val upiUri = Uri.parse("upi://pay?pa=9960171516@ybl&pn=A-tek%20Printing%20Press&cu=INR")
                                    val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Scan QR with PhonePe/GPay", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = primaryGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay via UPI (PhonePe / GPay)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGold
                            )
                        }
                    }
                }

                // 4. Add New Masjid Button
                Button(
                    onClick = { showAddMasjidDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F766E)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5EEAD4))
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (uiState.language) {
                            "ur" -> "➕ نئی مسجد شامل کرنے کی درخواست"
                            "hi" -> "➕ नई मस्जिद जोड़ने का अनुरोध"
                            else -> "➕ Request To Add New Masjid"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // 5. App Rating 5 Star ⭐⭐⭐⭐⭐ (Above version)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showRatingToast = true
                            try {
                                val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                                context.startActivity(rateIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "⭐⭐⭐⭐⭐ Thank you for 5-star rating!", Toast.LENGTH_SHORT).show()
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A29)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Rate Azan App",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryGold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < userRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Star",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clickable {
                                            userRating = index + 1
                                            Toast.makeText(context, "⭐⭐⭐⭐⭐ Thank you for rating!", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                        }

                        Text(
                            text = "⭐⭐⭐⭐⭐ 5 Star Rating",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // 6. Version Number & Powered by @tek (little bold)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "v${BuildConfig.VERSION_NAME}",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                    Text(
                        text = "Powered by @tek",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                }
            }
        }
    }

    // Add Masjid Form Dialog
    if (showAddMasjidDialog) {
        AddMasjidDialog(
            language = uiState.language,
            onDismiss = { showAddMasjidDialog = false },
            onSubmit = { name, loc, admin, contact, photoUri ->
                sendMasjidEmail(
                    context = context,
                    name = name,
                    location = loc,
                    adminName = admin,
                    contact = contact,
                    photoUri = photoUri
                )
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
                        else -> "Your masjid details have been sent to atikroshan@gmail.com. We will verify and add it to the Azan app soon!"
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
fun AddMasjidDialog(
    language: String,
    onDismiss: () -> Unit,
    onSubmit: (name: String, loc: String, admin: String, contact: String, photoUri: Uri?) -> Unit
) {
    var masjidName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminContact by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    label = { Text("Admin / Mutawalli Name *") },
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

                // Send Button
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
                            errorMessage = "Please enter Contact Number"
                            return@Button
                        }

                        onSubmit(masjidName, location, adminName, adminContact, selectedPhotoUri)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            "ur" -> "ای میل کے ذریعے بھیجیں"
                            "hi" -> "ई-मेल द्वारा भेजें"
                            else -> "Send Masjid Request"
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

private fun sendMasjidEmail(
    context: Context,
    name: String,
    location: String,
    adminName: String,
    contact: String,
    photoUri: Uri?
) {
    val targetEmail = "atikroshan@gmail.com"
    val subject = "New Masjid Request: $name ($location)"
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

    val emailBody = """
        Assalam-o-Alaikum,

        Request to add a new masjid to Azan App:

        🕌 Masjid Name: $name
        📍 Location: $location
        👤 Admin / Mutawalli Name: $adminName
        📞 Admin Contact Number: $contact
        📷 Photo Attached: ${if (photoUri != null) "Yes (see attachment)" else "No"}
        📱 App Version: v${BuildConfig.VERSION_NAME}
        📅 Submitted Date: $dateStr

        Please verify and add this masjid timetable.
    """.trimIndent()

    try {
        if (photoUri != null) {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, emailBody)
                putExtra(Intent.EXTRA_STREAM, photoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Send Email via:"))
        } else {
            val mailtoUri = Uri.parse("mailto:$targetEmail?subject=${Uri.encode(subject)}&body=${Uri.encode(emailBody)}")
            val sendIntent = Intent(Intent.ACTION_SENDTO, mailtoUri)
            context.startActivity(sendIntent)
        }
    } catch (e: Exception) {
        try {
            val fallback = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$targetEmail"))
            fallback.putExtra(Intent.EXTRA_SUBJECT, subject)
            fallback.putExtra(Intent.EXTRA_TEXT, emailBody)
            context.startActivity(fallback)
        } catch (e2: Exception) {
            Toast.makeText(context, "Please email to $targetEmail", Toast.LENGTH_LONG).show()
        }
    }
}
