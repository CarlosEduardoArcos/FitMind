package com.example.fitmind.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF06D6A0), // Verde neón para modo oscuro
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80CBC4),
    secondary = Color(0xFF3A86FF), // Azul FitMind
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0D47A1),
    onSecondaryContainer = Color(0xFFBBDEFB),
    tertiary = Color(0xFFFF6B35), // Naranja fitness
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF8B2C00),
    onTertiaryContainer = Color(0xFFFFCCBC),
    background = Color(0xFF121212), // Fondo oscuro Material 3
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E), // Superficie oscura Material 3
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF06D6A0), // Verde neón para bordes
    outlineVariant = Color(0xFF404040),
    surfaceTint = Color(0xFF06D6A0),
    inverseSurface = Color(0xFFF7F9FB),
    inverseOnSurface = Color(0xFF2D3748),
    inversePrimary = Color(0xFF00A86B)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3A86FF), // Azul FitMind
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF001A2C),
    secondary = Color(0xFF06D6A0), // Verde energía
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE0F7F4),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFFFF6B35), // Naranja fitness
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE5E0),
    onTertiaryContainer = Color(0xFF2C0B00),
    background = Color(0xFFF7F9FB), // Fondo claro fitness
    onBackground = Color(0xFF2D3748),
    surface = Color.White,
    onSurface = Color(0xFF2D3748),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFF718096),
    outlineVariant = Color(0xFFCBD5E0)
)

@Composable
fun FitMindTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            
            // Use modern approach for Android 12+ (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // For Android 12+, use the modern edge-to-edge approach
                WindowCompat.setDecorFitsSystemWindows(window, false)
            } else {
                // For older versions, use the traditional approach
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.primary.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}