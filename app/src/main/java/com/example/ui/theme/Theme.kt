package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Golden,
    secondary = AccentGreen,
    background = DeepNightNavy,
    surface = CardBackground,
    onPrimary = DeepNightNavy,
    onSecondary = TextColor,
    onBackground = TextColor,
    onSurface = TextColor
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
