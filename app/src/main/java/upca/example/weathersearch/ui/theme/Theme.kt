package upca.example.weathersearch.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WeatherBlueDark,
    secondary = WeatherBlueDarkSecondary,
    tertiary = SunYellow,
    background = WeatherSurfaceDark,
    surface = WeatherCardDark,
    onPrimary = Color(0xFF003548),
    onSecondary = Color(0xFF003548),
    onBackground = Color(0xFFE1E7EC),
    onSurface = Color(0xFFE1E7EC)
)

private val LightColorScheme = lightColorScheme(
    primary = WeatherBluePrimary,
    secondary = WeatherBlueSecondary,
    tertiary = SunYellow,
    background = WeatherSurfaceLight,
    surface = WeatherCardLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E)
)

@Composable
fun WeatherSearchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
