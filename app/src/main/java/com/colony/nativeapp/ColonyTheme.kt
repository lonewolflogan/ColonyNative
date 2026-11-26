package com.colony.nativeapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ColonyColors {
    val Background = Color(0xFF13111C)
    val Panel = Color(0xA61E192C)
    val Primary = Color(0xFFA855F7)
    val Accent = Color(0xFFC084FC)
    val Secondary = Color(0xFFD8B4FE)
    val Text = Color(0xFFF3E8FF)
    val Danger = Color(0xFFF43F5E)
    val Border = Color(0x80761D99)
    val Hover = Color(0xB32E2442)
}

private val ColonyDarkScheme = darkColorScheme(
    primary = ColonyColors.Primary,
    secondary = ColonyColors.Secondary,
    background = ColonyColors.Background,
    surface = ColonyColors.Panel,
    onPrimary = Color.White,
    onBackground = ColonyColors.Text,
    onSurface = ColonyColors.Text,
    error = ColonyColors.Danger
)

@Composable
fun ColonyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColonyDarkScheme,
        content = content
    )
}
