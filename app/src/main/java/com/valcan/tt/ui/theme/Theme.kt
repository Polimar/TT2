package com.valcan.tt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.valcan.tt.R

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9ECD),      // Rosa pastello
    secondary = Color(0xFFB5E6FF),    // Azzurro pastello
    tertiary = Color(0xFFFFC9E0),     // Rosa chiaro
    background = Color(0xFFFFF5F8),   // Bianco rosato
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB5E0),      // Rosa pastello più luminoso
    secondary = Color(0xFFC9E6FF),    // Azzurro pastello più luminoso
    tertiary = Color(0xFFFFD6E8),     // Rosa chiaro più luminoso
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF1C1B1F),
    onSecondary = Color(0xFF1C1B1F),
    onTertiary = Color(0xFF1C1B1F),
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun TTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}