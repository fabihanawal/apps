package com.example.amardokan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald800,
    secondary = Emerald600,
    onSecondary = White,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald800,
    tertiary = Amber600,
    onTertiary = White,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber600,
    background = Stone50,
    onBackground = Stone900,
    surface = White,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone700,
    outline = Stone200
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    onPrimary = Stone900,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = Emerald500,
    onSecondary = Stone900,
    background = Stone900,
    onBackground = Stone50,
    surface = Stone800,
    onSurface = Stone50,
    surfaceVariant = Stone700,
    onSurfaceVariant = Stone200,
    outline = Stone600
)

@Composable
fun AmarDokanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
