package com.timenw.healthtracker.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val HealthRed = Color(0xFFE57373)
val HealthDark = Color(0xFF1A2A3A)
val HealthBlue = Color(0xFF5C9EAD)
val HealthTeal = Color(0xFF4DB6AC)
val HealthLight = Color(0xFFB2DFDB)
val HealthGold = Color(0xFFD4A574)
val HealthCream = Color(0xFFE8F5E9)
val HealthSafe = Color(0xFF4CAF50)
val HealthWarning = Color(0xFFFF9800)
val HealthDanger = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = HealthTeal, onPrimary = Color.White, primaryContainer = HealthBlue, onPrimaryContainer = HealthCream,
    secondary = HealthRed, onSecondary = HealthCream, secondaryContainer = HealthDark, onSecondaryContainer = HealthLight,
    background = Color(0xFF0D1B21), onBackground = HealthCream, surface = Color(0xFF162530), onSurface = HealthCream,
    surfaceVariant = Color(0xFF1A2A3A), onSurfaceVariant = HealthLight, error = HealthDanger, outline = HealthBlue
)

@Composable
fun HealthTrackerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { val window = (view.context as Activity).window; window.statusBarColor = DarkColorScheme.background.toArgb(); WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false }
    }
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography(), content = content)
}
