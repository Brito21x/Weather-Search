package upca.example.weathersearch.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import upca.example.weathersearch.ui.theme.SunYellow
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

// Componente para ícones meteorológicos com salvaguarda nativa para o modo Preview
@Composable
fun WeatherIcon(
    iconUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        // Exibe ícone vetorial no preview sem tentar chamadas de rede no LayoutLib
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
    }
}

// Pré-visualização do componente de ícone meteorológico
@Preview(showBackground = true)
@Composable
fun WeatherIconPreview() {
    WeatherSearchTheme {
        WeatherIcon(
            iconUrl = "",
            contentDescription = "Ícone de pré-visualização"
        )
    }
}
