package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnFinished by rememberUpdatedState(onSplashFinished)
    val coroutineScope = rememberCoroutineScope()

    // 1. Entrance Animators (Medallion Bounce In)
    val medallionScale = remember { Animatable(0.25f) }
    val medallionAlpha = remember { Animatable(0f) }
    val medallionOffsetY = remember { Animatable(40f) }

    // 2. Title & Punchline Entrance Animators
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(30f) }

    // 3. Master Zoom Out & Exit Animators
    val masterScale = remember { Animatable(1f) }
    val masterAlpha = remember { Animatable(1f) }

    // Infinite transitions for rotating rings & pulsing glow
    val infiniteTransition = rememberInfiniteTransition(label = "SplashRings")
    val ring1Rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring1"
    )

    val ring2Rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring2"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    fun startExitSequence(fast: Boolean = false) {
        coroutineScope.launch {
            if (masterScale.value > 1.2f) return@launch // Already exiting
            if (fast) {
                launch { masterScale.animateTo(18f, tween(700, easing = CubicBezierEasing(0.68f, -0.05f, 0.85f, 0.05f))) }
                launch { masterAlpha.animateTo(0f, tween(600, easing = FastOutSlowInEasing)) }
                delay(650)
                currentOnFinished()
            } else {
                launch { masterScale.animateTo(1.06f, tween(250, easing = FastOutSlowInEasing)) }
                delay(220)
                launch { masterScale.animateTo(18f, tween(1000, easing = CubicBezierEasing(0.68f, -0.05f, 0.85f, 0.05f))) }
                launch { masterAlpha.animateTo(0f, tween(850, easing = FastOutSlowInEasing)) }
                delay(950)
                currentOnFinished()
            }
        }
    }

    LaunchedEffect(Unit) {
        // Step 1: Bounce in Medallion (0 - 1.1s)
        launch {
            medallionAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            medallionOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 1100
                    40f at 0
                    -8f at 550 using CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
                    3f at 770
                    -1f at 935
                    0f at 1100
                }
            )
        }
        launch {
            medallionScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 1100
                    0.25f at 0
                    1.1f at 550 using CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
                    0.96f at 770
                    1.02f at 935
                    1.0f at 1100
                }
            )
        }

        // Step 2: Slide up Title & Punchline (starts at 450ms)
        delay(450)
        launch {
            textAlpha.animateTo(1f, tween(750, easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)))
        }
        launch {
            textOffsetY.animateTo(0f, tween(850, easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)))
        }

        // Step 3: Wait until 3.3s then trigger Zoom-Out Disappear Transition
        delay(2850) // Total ~3.3s
        startExitSequence(fast = false)
    }

    BackHandler {
        startExitSequence(fast = true)
    }

    val goldGradient = remember {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF3DE8E),
                Color(0xFFF3D66A),
                Color(0xFFB37C3C)
            )
        )
    }

    val radialBackground = remember {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF0F382E),
                Color(0xFF07241E),
                Color(0xFF00221A)
            ),
            radius = 1200f
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(radialBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                startExitSequence(fast = true)
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. ISLAMIC STAR GEOMETRIC PATTERN BACKGROUND
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.048f)
        ) {
            val step = 80.dp.toPx()
            val strokeWidth = 1.2.dp.toPx()
            val goldColor = Color(0xFFFED65B)
            val rows = (size.height / step).toInt() + 2
            val cols = (size.width / step).toInt() + 2

            for (r in 0..rows) {
                for (c in 0..cols) {
                    val cx = c * step
                    val cy = r * step
                    val path = Path()

                    // 8-pointed star
                    val starR1 = 28.dp.toPx()
                    val starR2 = 14.dp.toPx()
                    for (i in 0 until 16) {
                        val angle = (i * Math.PI / 8.0) - (Math.PI / 2.0)
                        val radius = if (i % 2 == 0) starR1 else starR2
                        val x = cx + (radius * cos(angle)).toFloat()
                        val y = cy + (radius * sin(angle)).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path, color = goldColor, style = Stroke(width = strokeWidth))
                    drawCircle(color = Color(0xFFFFE08B), radius = 14.dp.toPx(), center = Offset(cx, cy), style = Stroke(width = 0.8.dp.toPx()))
                }
            }
        }

        // 2. MASTER CENTRAL HERO GROUP (Scales and zooms out on transition)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = masterScale.value
                    scaleY = masterScale.value
                    alpha = masterAlpha.value
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // ==================== CIRCULAR MEDALLION ====================
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .graphicsLayer {
                            scaleX = medallionScale.value
                            scaleY = medallionScale.value
                            alpha = medallionAlpha.value
                            translationY = medallionOffsetY.value * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Soft Glowing Aura
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .scale(pulseGlow)
                            .alpha(pulseGlow)
                            .blur(26.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFED65B).copy(alpha = 0.35f),
                                        Color(0xFFC9A741).copy(alpha = 0.18f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // Outer Ring 1: Continuous Clockwise Rotation with 2 Orbital Gold Dots
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(ring1Rotation)
                    ) {
                        val stroke = 1.dp.toPx()
                        drawCircle(
                            color = Color(0xFFFED65B).copy(alpha = 0.28f),
                            style = Stroke(width = stroke)
                        )
                        // Top Golden Orbit Dot
                        drawCircle(
                            color = Color(0xFFFFE088),
                            radius = 4.dp.toPx(),
                            center = Offset(size.width / 2f, 0f)
                        )
                        drawCircle(
                            color = Color(0xFFFED65B).copy(alpha = 0.5f),
                            radius = 7.dp.toPx(),
                            center = Offset(size.width / 2f, 0f)
                        )

                        // Bottom Golden Orbit Dot
                        drawCircle(
                            color = Color(0xFFFFE088),
                            radius = 4.dp.toPx(),
                            center = Offset(size.width / 2f, size.height)
                        )
                        drawCircle(
                            color = Color(0xFFFED65B).copy(alpha = 0.5f),
                            radius = 7.dp.toPx(),
                            center = Offset(size.width / 2f, size.height)
                        )
                    }

                    // Outer Ring 2: Dashed Ring Counter-Clockwise Rotation
                    Canvas(
                        modifier = Modifier
                            .size(252.dp)
                            .rotate(ring2Rotation)
                    ) {
                        val stroke = 1.5.dp.toPx()
                        val intervals = floatArrayOf(8.dp.toPx(), 6.dp.toPx())
                        drawCircle(
                            color = Color(0xFFFED65B).copy(alpha = 0.38f),
                            style = Stroke(
                                width = stroke,
                                pathEffect = PathEffect.dashPathEffect(intervals, 0f)
                            )
                        )
                    }

                    // Inner Shield Disc with App Logo
                    Box(
                        modifier = Modifier
                            .size(226.dp)
                            .shadow(24.dp, shape = CircleShape, ambientColor = Color.Black, spotColor = Color(0xFFFED65B))
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF0F382E),
                                        Color(0xFF061C16)
                                    )
                                )
                            )
                            .border(1.5.dp, Color(0xFFFFE088).copy(alpha = 0.45f), CircleShape)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Azan Time Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ==================== AZAN TIME & PUNCHLINE ====================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = textAlpha.value
                            translationY = textOffsetY.value * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Real Graen Metal Font AZAN TIME Typography with Metallic Gold Gradient
                        Text(
                            text = "AZAN TIME",
                            fontFamily = GraenMetalFontFamily,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = GraenMetalGoldGradient,
                                shadow = GraenMetalGoldShadow
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Punchline in pure white constrained under title
                        Text(
                            text = "\"AAO ALLAH KI RAAH MEIN CHALE\"",
                            color = Color.White.copy(alpha = 0.96f),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.8.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .shadow(8.dp, spotColor = Color.White)
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-precision Vector Composable rendering the exact gothic AZAN TIME wordmark
 * with metallic gold gradient and serrated baseline.
 */
@Composable
fun AzanTimeVectorTitle(modifier: Modifier = Modifier) {
    val goldBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF3DE8E),
                Color(0xFFF3D66A),
                Color(0xFFB37C3C)
            )
        )
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scaleX = w / 680f
        val scaleY = h / 160f

        // Transform canvas coordinate system to 680x160 design space
        val pathA1 = Path().apply {
            moveTo(18f * scaleX, 140f * scaleY)
            lineTo(14f * scaleX, 146f * scaleY)
            lineTo(22f * scaleX, 148f * scaleY)
            lineTo(26f * scaleX, 140f * scaleY)
            lineTo(40f * scaleX, 32f * scaleY)
            lineTo(48f * scaleX, 16f * scaleY)
            lineTo(56f * scaleX, 32f * scaleY)
            lineTo(70f * scaleX, 140f * scaleY)
            lineTo(74f * scaleX, 148f * scaleY)
            lineTo(82f * scaleX, 146f * scaleY)
            lineTo(78f * scaleX, 140f * scaleY)
            lineTo(62f * scaleX, 140f * scaleY)
            lineTo(59f * scaleX, 114f * scaleY)
            lineTo(37f * scaleX, 114f * scaleY)
            lineTo(34f * scaleX, 140f * scaleY)
            close()
            moveTo(48f * scaleX, 46f * scaleY)
            lineTo(41f * scaleX, 96f * scaleY)
            lineTo(55f * scaleX, 96f * scaleY)
            close()
        }

        val pathZ = Path().apply {
            moveTo(88f * scaleX, 38f * scaleY)
            lineTo(94f * scaleX, 22f * scaleY)
            lineTo(152f * scaleX, 22f * scaleY)
            lineTo(156f * scaleX, 38f * scaleY)
            lineTo(114f * scaleX, 124f * scaleY)
            lineTo(156f * scaleX, 124f * scaleY)
            lineTo(160f * scaleX, 116f * scaleY)
            lineTo(162f * scaleX, 140f * scaleY)
            lineTo(156f * scaleX, 146f * scaleY)
            lineTo(90f * scaleX, 146f * scaleY)
            lineTo(86f * scaleX, 130f * scaleY)
            lineTo(128f * scaleX, 44f * scaleY)
            lineTo(88f * scaleX, 44f * scaleY)
            close()
        }

        val pathA2 = Path().apply {
            moveTo(168f * scaleX, 140f * scaleY)
            lineTo(164f * scaleX, 146f * scaleY)
            lineTo(172f * scaleX, 148f * scaleY)
            lineTo(176f * scaleX, 140f * scaleY)
            lineTo(190f * scaleX, 32f * scaleY)
            lineTo(198f * scaleX, 16f * scaleY)
            lineTo(206f * scaleX, 32f * scaleY)
            lineTo(220f * scaleX, 140f * scaleY)
            lineTo(224f * scaleX, 148f * scaleY)
            lineTo(232f * scaleX, 146f * scaleY)
            lineTo(228f * scaleX, 140f * scaleY)
            lineTo(212f * scaleX, 140f * scaleY)
            lineTo(209f * scaleX, 114f * scaleY)
            lineTo(187f * scaleX, 114f * scaleY)
            lineTo(184f * scaleX, 140f * scaleY)
            close()
            moveTo(198f * scaleX, 46f * scaleY)
            lineTo(191f * scaleX, 96f * scaleY)
            lineTo(205f * scaleX, 96f * scaleY)
            close()
        }

        val pathN = Path().apply {
            moveTo(238f * scaleX, 140f * scaleY)
            lineTo(234f * scaleX, 146f * scaleY)
            lineTo(244f * scaleX, 148f * scaleY)
            lineTo(248f * scaleX, 140f * scaleY)
            lineTo(248f * scaleX, 38f * scaleY)
            lineTo(238f * scaleX, 24f * scaleY)
            lineTo(254f * scaleX, 22f * scaleY)
            lineTo(290f * scaleX, 118f * scaleY)
            lineTo(290f * scaleX, 38f * scaleY)
            lineTo(282f * scaleX, 24f * scaleY)
            lineTo(302f * scaleX, 22f * scaleY)
            lineTo(306f * scaleX, 38f * scaleY)
            lineTo(306f * scaleX, 140f * scaleY)
            lineTo(310f * scaleX, 148f * scaleY)
            lineTo(300f * scaleX, 148f * scaleY)
            lineTo(262f * scaleX, 48f * scaleY)
            lineTo(262f * scaleX, 140f * scaleY)
            close()
        }

        val pathT = Path().apply {
            moveTo(346f * scaleX, 38f * scaleY)
            lineTo(350f * scaleX, 22f * scaleY)
            lineTo(424f * scaleX, 22f * scaleY)
            lineTo(428f * scaleX, 38f * scaleY)
            lineTo(396f * scaleX, 44f * scaleY)
            lineTo(396f * scaleX, 140f * scaleY)
            lineTo(392f * scaleX, 148f * scaleY)
            lineTo(400f * scaleX, 148f * scaleY)
            lineTo(404f * scaleX, 140f * scaleY)
            lineTo(404f * scaleX, 44f * scaleY)
            close()
            moveTo(382f * scaleX, 44f * scaleY)
            lineTo(382f * scaleX, 140f * scaleY)
            lineTo(378f * scaleX, 146f * scaleY)
            lineTo(388f * scaleX, 148f * scaleY)
            lineTo(392f * scaleX, 140f * scaleY)
            lineTo(392f * scaleX, 44f * scaleY)
            close()
        }

        val pathI = Path().apply {
            moveTo(436f * scaleX, 38f * scaleY)
            lineTo(442f * scaleX, 22f * scaleY)
            lineTo(458f * scaleX, 22f * scaleY)
            lineTo(464f * scaleX, 38f * scaleY)
            lineTo(464f * scaleX, 140f * scaleY)
            lineTo(468f * scaleX, 148f * scaleY)
            lineTo(458f * scaleX, 148f * scaleY)
            lineTo(454f * scaleX, 140f * scaleY)
            lineTo(454f * scaleX, 38f * scaleY)
            lineTo(446f * scaleX, 38f * scaleY)
            close()
        }

        val pathM = Path().apply {
            moveTo(474f * scaleX, 140f * scaleY)
            lineTo(470f * scaleX, 146f * scaleY)
            lineTo(480f * scaleX, 148f * scaleY)
            lineTo(484f * scaleX, 140f * scaleY)
            lineTo(484f * scaleX, 38f * scaleY)
            lineTo(476f * scaleX, 24f * scaleY)
            lineTo(492f * scaleX, 22f * scaleY)
            lineTo(522f * scaleX, 106f * scaleY)
            lineTo(552f * scaleX, 22f * scaleY)
            lineTo(568f * scaleX, 24f * scaleY)
            lineTo(560f * scaleX, 38f * scaleY)
            lineTo(560f * scaleX, 140f * scaleY)
            lineTo(564f * scaleX, 148f * scaleY)
            lineTo(554f * scaleX, 148f * scaleY)
            lineTo(550f * scaleX, 140f * scaleY)
            lineTo(550f * scaleX, 56f * scaleY)
            lineTo(526f * scaleX, 128f * scaleY)
            lineTo(518f * scaleX, 128f * scaleY)
            lineTo(494f * scaleX, 56f * scaleY)
            lineTo(494f * scaleX, 140f * scaleY)
            close()
        }

        val pathE = Path().apply {
            moveTo(576f * scaleX, 38f * scaleY)
            lineTo(582f * scaleX, 22f * scaleY)
            lineTo(642f * scaleX, 22f * scaleY)
            lineTo(646f * scaleX, 38f * scaleY)
            lineTo(598f * scaleX, 42f * scaleY)
            lineTo(598f * scaleX, 72f * scaleY)
            lineTo(634f * scaleX, 72f * scaleY)
            lineTo(638f * scaleX, 88f * scaleY)
            lineTo(598f * scaleX, 88f * scaleY)
            lineTo(598f * scaleX, 126f * scaleY)
            lineTo(646f * scaleX, 126f * scaleY)
            lineTo(650f * scaleX, 118f * scaleY)
            lineTo(652f * scaleX, 142f * scaleY)
            lineTo(646f * scaleX, 146f * scaleY)
            lineTo(580f * scaleX, 146f * scaleY)
            lineTo(576f * scaleX, 132f * scaleY)
            close()
        }

        // Draw Letters with metallic gold gradient
        drawPath(pathA1, brush = goldBrush)
        drawPath(pathZ, brush = goldBrush)
        drawPath(pathA2, brush = goldBrush)
        drawPath(pathN, brush = goldBrush)
        drawPath(pathT, brush = goldBrush)
        drawPath(pathI, brush = goldBrush)
        drawPath(pathM, brush = goldBrush)
        drawPath(pathE, brush = goldBrush)

        // Serrated baseline decorative teeth
        val serratedPath = Path()
        var currentX = 22f
        while (currentX <= 310f) {
            serratedPath.moveTo(currentX * scaleX, 148f * scaleY)
            serratedPath.lineTo((currentX + 8f) * scaleX, 154f * scaleY)
            serratedPath.lineTo((currentX + 16f) * scaleX, 148f * scaleY)
            currentX += 16f
        }
        currentX = 346f
        while (currentX <= 650f) {
            serratedPath.moveTo(currentX * scaleX, 148f * scaleY)
            serratedPath.lineTo((currentX + 8f) * scaleX, 154f * scaleY)
            serratedPath.lineTo((currentX + 16f) * scaleX, 148f * scaleY)
            currentX += 16f
        }
        drawPath(serratedPath, brush = goldBrush, style = Stroke(width = 2.2f * scaleX, cap = StrokeCap.Round))
    }
}
