package com.example.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

val GraenMetalFontFamily = FontFamily(
    Font(R.font.graen_metal, FontWeight.Normal)
)

val GraenMetalGoldGradient = Brush.verticalGradient(
    listOf(
        Color(0xFFF3DE8E),
        Color(0xFFF3D66A),
        Color(0xFFB37C3C)
    )
)

val GraenMetalGoldShadow = Shadow(
    color = Color(0xFFFED65B).copy(alpha = 0.55f),
    offset = Offset(0f, 0f),
    blurRadius = 14f
)
