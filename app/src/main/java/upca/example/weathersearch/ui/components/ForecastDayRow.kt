package upca.example.weathersearch.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.ui.theme.RainBlue
import upca.example.weathersearch.ui.theme.SunYellow
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

@Composable
fun ForecastDayRow(
    forecastDay: ForecastDayDomain,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1.3f)
            ) {
                Text(
                    text = forecastDay.dayLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${forecastDay.dayOfWeek} • ${forecastDay.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Chuva",
                        tint = RainBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${forecastDay.chanceOfRain}% chuva",
                        style = MaterialTheme.typography.labelSmall,
                        color = RainBlue
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(
                    iconUrl = forecastDay.iconUrl,
                    contentDescription = forecastDay.conditionText,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = forecastDay.conditionText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(0.9f)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${forecastDay.maxTemp.toInt()}°C",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SunYellow
                    )
                    Text(
                        text = "${forecastDay.minTemp.toInt()}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalhe do dia",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastDayRowPreview() {
    WeatherSearchTheme {
        ForecastDayRow(
            forecastDay = ForecastDayDomain(
                dayIndex = 0,
                dayLabel = "Hoje",
                date = "2026-11-18",
                dayOfWeek = "Quarta-feira",
                maxTemp = 18.0,
                minTemp = 11.0,
                avgTemp = 14.5,
                conditionText = "Parcialmente nublado",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                chanceOfRain = 25,
                windKph = 16.0,
                humidity = 72
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
