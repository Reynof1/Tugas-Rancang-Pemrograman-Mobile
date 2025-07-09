package com.example.tugasrancang.ui.theme


import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/* ---------- Palet warna dasar ---------- */
val Purple80       = Color(0xFFD0BCFF)
val PurpleGrey80   = Color(0xFFCCC2DC)
val Pink80         = Color(0xFFEFB8C8)

val Purple40       = Color(0xFF6650a4)
val PurpleGrey40   = Color(0xFF625b71)
val Pink40         = Color(0xFF7D5260)

/* ---------- Warna kustom SIASAT ---------- */
val SiasatBlue        = Color(0xFF00838F)  // Primary
val SiasatLightBlue   = Color(0xFF4FB3BF)  // Secondary
val SiasatBackground  = Color(0xFFF0F4F5)  // Background

/* ---------- Skema warna ---------- */
private val DarkColorScheme = darkColorScheme(
    primary   = SiasatBlue,
    secondary = SiasatLightBlue,
    tertiary  = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary        = SiasatBlue,
    secondary      = SiasatLightBlue,
    tertiary       = Pink40,
    background     = SiasatBackground,
    surface        = Color.White,
    onPrimary      = Color.White,
    onSecondary    = Color.White,
    onTertiary     = Color.White,
    onBackground   = Color(0xFF1C1B1F),
    onSurface      = Color(0xFF1C1B1F),
)

/* ---------- Theme wrapper ---------- */
@Composable
fun TugasRancangTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Non‑aktifkan warna dinamis (Android 12+)
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> LightColorScheme
        darkTheme    -> DarkColorScheme
        else         -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor =
                colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}