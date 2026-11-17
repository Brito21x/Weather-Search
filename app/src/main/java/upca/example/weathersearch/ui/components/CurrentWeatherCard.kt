package upca.example.weathersearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.ui.theme.WeatherBluePrimary
import upca.example.weathersearch.ui.theme.WeatherBlueSecondary
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

// Cartão de destaque com o tempo atual e detalhes rápidos da localidade
@Composable
fun CurrentWeatherCard(
    weather: WeatherDomain,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(WeatherBluePrimary, WeatherBlueSecondary)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cabeçalho com o nome da localidade pesquisada
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Localização",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${weather.cityName}, ${weather.country}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Temperatura atual, descrição e ícone da condição
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${weather.currentTemp.toInt()}°C",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Sensação térmica: ${weather.feelsLike.toInt()}°C",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = weather.conditionText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    WeatherIcon(
                        iconUrl = weather.iconUrl,
                        contentDescription = weather.conditionText,
                        modifier = Modifier.size(90.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Detalhes rápidos: Sensação térmica, Humidade e Velocidade do vento
                Text(
                    text = "Detalhes Rápidos",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherMetricItem(
                        icon = Icons.Default.Thermostat,
                        label = "Sensação",
                        value = "${weather.feelsLike.toInt()}°C"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.WaterDrop,
                        label = "Humidade",
                        value = "${weather.humidity}%"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.Air,
                        label = "Vento",
                        value = "${weather.windSpeedKph.toInt()} km/h"
                    )
                }
            }
        }
    }
}

// Item indicador de métrica para os detalhes rápidos
@Composable
private fun WeatherMetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// Pré-visualização do cartão do tempo atual
@Preview(showBackground = true)
@Composable
fun CurrentWeatherCardPreview() {
    WeatherSearchTheme {
        CurrentWeatherCard(
            weather = WeatherDomain(
                cityName = "Barcelos",
                country = "Portugal",
                currentTemp = 16.0,
                feelsLike = 15.0,
                conditionText = "Parcialmente nublado",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                humidity = 68,
                windSpeedKph = 14.0,
                forecastDays = emptyList()
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
