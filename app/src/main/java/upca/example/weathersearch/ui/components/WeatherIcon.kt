package upca.example.weathersearch.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import upca.example.weathersearch.ui.theme.SunYellow
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

@Composable
fun WeatherIcon(
    iconUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        // No modo Preview, usa ícone vetorial para evitar chamadas de rede no LayoutLib
        Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = contentDescription,
            tint = SunYellow,
            modifier = modifier
        )
    } else if (iconUrl.isNotBlank()) {
        AsyncImage(
            model = iconUrl,
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        // Fallback quando não há URL disponível
        Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = contentDescription,
            tint = SunYellow,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherIconPreview() {
    WeatherSearchTheme {
        WeatherIcon(
            iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
            contentDescription = "Ícone de pré-visualização",
            modifier = Modifier.size(64.dp)
        )
    }
}
