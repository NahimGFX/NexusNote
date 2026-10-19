package com.example.nexusnote.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NexusColorScheme = lightColorScheme(
    primary = NexusGreen,
    onPrimary = NexusWhite,
    primaryContainer = NexusGreenLight,
    onPrimaryContainer = NexusTextPrimary,
    secondary = NexusGreen,
    onSecondary = NexusWhite,
    secondaryContainer = NexusGreenLight,
    onSecondaryContainer = NexusTextPrimary,
    tertiary = NexusGreen,
    onTertiary = NexusWhite,
    background = NexusWhite,
    onBackground = NexusTextPrimary,
    surface = NexusWhite,
    onSurface = NexusTextPrimary,
    surfaceVariant = NexusSurfaceVariant,
    onSurfaceVariant = NexusTextSecondary,
    outline = NexusBorder,
    outlineVariant = NexusBorder,
    error = NexusError,
    onError = NexusWhite
)

private val NexusShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NexusNoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NexusColorScheme,
        typography = Typography,
        shapes = NexusShapes,
        content = content
    )
}
