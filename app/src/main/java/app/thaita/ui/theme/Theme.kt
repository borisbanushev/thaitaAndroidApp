package app.thaita.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class ThaitaColors(
    val green: Color = ThaitaGreen,
    val red: Color = ThaitaRed,
    val brandRed: Color = ThaitaBrandRed,
    val yellow: Color = ThaitaYellow,
    val blue: Color = ThaitaBlue,
    val brandGreen: Color = ThaitaBrandGreen,
    val gradientStart: Color = GradientStart,
    val gradientEnd: Color = GradientEnd,
    val glassBg: Color = GlassBgLight,
    val glassBorder: Color = GlassBorder,
    val cardBg: Color = CardBgLight,
    val textPrimary: Color = ThaitaTextPrimary,
    val textSecondary: Color = ThaitaTextSecondary,
    val axisMuted: Color = AxisMuted,
    val tableHeaderBg: Color = TableHeaderBg,
)

val LocalThaitaColors = staticCompositionLocalOf { ThaitaColors() }

private val LightColorScheme = lightColorScheme(
    primary = ThaitaBrandRed,
    secondary = ThaitaYellow,
    tertiary = ThaitaGreen,
    background = ThaitaLightBg,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = ThaitaTextPrimary,
    onTertiary = Color.White,
    onBackground = ThaitaTextPrimary,
    onSurface = ThaitaTextPrimary,
    error = ThaitaRed,
    onError = Color.White,
    outline = Color(0xFFE0E0E0),
)

private val DarkColorScheme = darkColorScheme(
    primary = ThaitaBrandRed,
    secondary = ThaitaYellow,
    tertiary = ThaitaGreen,
    background = ThaitaDarkBg,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = ThaitaRed,
    onError = Color.White,
    outline = Color(0xFF2D3A52),
)

@Composable
fun ThaitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val thaitaColors = if (darkTheme) {
        ThaitaColors(
            glassBg = GlassBgDark,
            cardBg = CardBgDark,
            textPrimary = Color.White,
            textSecondary = Color(0xFFB0B8C8),
        )
    } else {
        ThaitaColors()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalThaitaColors provides thaitaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ThaitaTypography,
            content = content,
        )
    }
}
