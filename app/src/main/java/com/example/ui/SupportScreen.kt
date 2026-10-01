package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.appBackground
import com.example.islamicStarBackground

@Composable
fun SupportScreen(
    uiState: UIState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryGold = Color(0xFFF3DE8E)
    val curMasjid = uiState.selectedMasjid

    val supportTitle = when (uiState.language) {
        "ur" -> "مدد اور رابطہ"
        "hi" -> "सहायता और संपर्क"
        else -> "Support & Help"
    }

    val supportSubtitle = when (uiState.language) {
        "ur" -> "مسجد رابطہ، فیڈ بیک اور معلومات"
        "hi" -> "मस्जिद संपर्क, सुझाव और सहायता"
        else -> "Masjid Contacts, Feedback & Help"
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .border(1.dp, primaryGold.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = supportTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryGold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = supportSubtitle,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Selected Masjid Info & Committee Contacts
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, primaryGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .border(1.dp, primaryGold.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Mosque,
                                    contentDescription = null,
                                    tint = primaryGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = curMasjid.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryGold
                                )
                                Text(
                                    text = "${curMasjid.area} • ID: #${curMasjid.id}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        Text(
                            text = when (uiState.language) {
                                "ur" -> "مسجد کمیٹی رابطہ (امام / مؤذن / منتظم)"
                                "hi" -> "मस्जिद समिति संपर्क (इमाम / मुअज़्ज़िन / मुतवल्ली)"
                                else -> "Masjid Committee (Imam / Muazzin / Mutawalli)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF93C5FD)
                        )

                        Text(
                            text = when (uiState.language) {
                                "ur" -> "کسی بھی وقت نماز یا جماعت کے اوقات کی تصدیق کے لیے اپنی مسجد سے رابطہ کر سکتے ہیں۔"
                                "hi" -> "नमाज़ या जमात के समय की पुष्टि के लिए अपनी स्थानीय मस्जिद से संपर्क कर सकते हैं।"
                                else -> "Contact your local masjid committee for timing verifications or updates."
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val phone = curMasjid.adminPassword.filter { it.isDigit() }
                                    if (phone.length >= 10) {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Ignore
                                        }
                                    } else {
                                        android.widget.Toast.makeText(context, "Contact details will be updated soon", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }

                            Button(
                                onClick = {
                                    val phone = curMasjid.adminPassword.filter { it.isDigit() }
                                    val targetPhone = if (phone.length >= 10) phone else "919960171516"
                                    try {
                                        val text = Uri.encode("Assalam-o-Alaikum, mujhe ${curMasjid.name} ke timings ke bare me rabta karna hai.")
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$targetPhone?text=$text"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "WhatsApp not installed", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF25D366))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WhatsApp", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF25D366))
                            }
                        }
                    }
                }

                // Card 2: Report Wrong Timing & Correction
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522).copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditCalendar,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "وقت کی تصحیح / تجاویز"
                                    "hi" -> "समय सुधार / सुझाव"
                                    else -> "Report Time Correction / Feedback"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Text(
                            text = when (uiState.language) {
                                "ur" -> "اگر آپ کی مسجد کے اذان یا جماعت کے وقت میں کوئی تبدیلی ہوئی ہو تو ہمیں مطلع فرمائیں۔"
                                "hi" -> "यदि आपकी मस्जिद के अज़ान या जमात के समय में कोई बदलाव हुआ है तो हमें सूचित करें।"
                                else -> "If your masjid azan or jammat timing has changed, let us know to update it for all users."
                            },
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )

                        Button(
                            onClick = {
                                try {
                                    val text = Uri.encode("Assalam-o-Alaikum, ${curMasjid.name} (#${curMasjid.id}) ke timing me correction update:")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919960171516?text=$text"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "WhatsApp not available", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "وقت کا اپ ڈیٹ بھیجیں"
                                    "hi" -> "समय का अपडेट भेजें"
                                    else -> "Send Timing Update"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Card 3: Azan Sound & Background Battery Guide
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522).copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = when (uiState.language) {
                                    "ur" -> "اذان نہ بجنے پر رہنمائی (بیٹری سیٹنگ)"
                                    "hi" -> "अज़ान न बजने पर समाधान (बैटरी सेटिंग्स)"
                                    else -> "Azan Alarm Not Ringing? (Battery Fix)"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            )
                        }

                        Text(
                            text = when (uiState.language) {
                                "ur" -> "Xiaomi, Vivo, Oppo اور Samsung فونز میں پس منظر میں اذان جاری رکھنے کے لیے بیٹری سیور کو 'No Restrictions / Unrestricted' پر رکھیں اور آٹو اسٹارٹ آن رکھیں۔"
                                "hi" -> "Xiaomi, Vivo, Oppo और Samsung फोन में बैकग्राउंड में अज़ान बजने के लिए बैटरी सेवर को 'Unrestricted / कोई पाबंदी नहीं' पर रखें और Auto-Start चालू करें।"
                                else -> "For Xiaomi, Vivo, Oppo & Samsung devices, set Battery to 'Unrestricted' and enable 'Auto-start' so azan always plays on time."
                            },
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Card 4: App Info & Powered By
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090D15).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "AZAN TIME SOLAPUR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME} • Powered by @tek",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
