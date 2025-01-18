package com.example.blackbeard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,            // #007FFF - Bright blue
    secondary = DarkSecondary,        // #00B7FA - Lighter blue
    tertiary = DarkAccent,            // #5C58D5 - Vibrant purple accent
    onBackground = DarkText,          // #EAE9FC - Light text on dark background
    onSurface = DarkText,             // #EAE9FC - Light text on dark surface
    background = DarkBackground,      // #010104 - Very dark background
    surface = DarkBackground,         // #010104 - Matches background
    secondaryContainer = DarkSecondary, // Same as secondary for containers
    onSecondaryContainer = DarkText,  // #EAE9FC - Light text on secondary container
    tertiaryContainer = DarkAccent,   // Same as tertiary for containers
    onTertiaryContainer = DarkText    // #EAE9FC - Light text on tertiary container
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,           // #007FFF - Bright blue
    secondary = LightSecondary,       // #05BCFF - Lighter blue
    tertiary = LightAccent,           // #2E2AA7 - Deep purple accent
    onBackground = LightText,         // #040316 - Dark text on light background
    onSurface = LightText,            // #040316 - Dark text on light surface
    background = LightBackground,     // #FBFBFE - Very light background
    surface = LightBackground,        // #FBFBFE - Matches background
    secondaryContainer = LightSecondary, // Same as secondary for containers
    onSecondaryContainer = LightText, // #040316 - Dark text on secondary container
    tertiaryContainer = LightAccent,  // Same as tertiary for containers
    onTertiaryContainer = LightText   // #040316 - Dark text on tertiary container
)

@Composable
fun BlackbeardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}