package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LilacAccent,
    onPrimary = LilacDeep,
    primaryContainer = LilacDeep,
    onPrimaryContainer = LilacSub,
    secondary = CyanAccent,
    onSecondary = ObsidianBg,
    secondaryContainer = SlateSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldAccent,
    onTertiary = ObsidianBg,
    tertiaryContainer = SlateSurfaceVariant,
    onTertiaryContainer = EmeraldLight,
    error = CrimsonRisk,
    onError = ObsidianBg,
    errorContainer = CrimsonDark,
    onErrorContainer = CrimsonLight,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    outlineVariant = SlateBorderLight
)

@Composable
fun CladRbTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ObsidianBg.toArgb()
                window.navigationBarColor = ObsidianBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
