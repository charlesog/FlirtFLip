package com.example.flirtflip.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    error = error,
    errorContainer = errorContainer,
    onError = onError,
    onErrorContainer = onErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6BAD),
    onPrimary = Color(0xFF3D0020),
    primaryContainer = Color(0xFF6B0039),
    onPrimaryContainer = Color(0xFFFFD6EC),
    secondary = Color(0xFFFF9DC6),
    onSecondary = Color(0xFF2D0018),
    secondaryContainer = Color(0xFF4A0030),
    onSecondaryContainer = Color(0xFFFFD6EC),
    tertiary = Color(0xFFFFB3D9),
    onTertiary = Color(0xFF1E0012),
    tertiaryContainer = Color(0xFF3B0018),
    onTertiaryContainer = Color(0xFFFFD6EC),
    background = Color(0xFF1A0E14),
    onBackground = Color(0xFFFADEED),
    surface = Color(0xFF261318),
    onSurface = Color(0xFFFADEED),
    surfaceVariant = Color(0xFF3D2530),
    onSurfaceVariant = Color(0xFFD4AABE),
    error = Color(0xFFFF6B6B),
    errorContainer = Color(0xFF5C1A1A),
    onError = Color(0xFF1A0000),
    onErrorContainer = Color(0xFFFFDAD6)
)

enum class ThemePreference { LIGHT, DARK, SYSTEM }

val LocalThemePreference = compositionLocalOf { ThemePreference.SYSTEM }

@Composable
fun FlirtFlipTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themePreference) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}