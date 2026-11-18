package upca.example.weathersearch.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.ui.components.WeatherIcon
import upca.example.weathersearch.ui.theme.RainBlue
import upca.example.weathersearch.ui.theme.WeatherBluePrimary
import upca.example.weathersearch.ui.theme.WeatherBlueSecondary
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

@Composable
fun ForecastDetailScreen(
    dayDate: String,
    modifier: Modifier = Modifier,
    viewModel: ForecastDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dayDate) {
        viewModel.loadForecastDayByDate(dayDate)
    }

    ForecastDetailContent(
        forecastDay = uiState.forecastDay,
        modifier = modifier
    )
}

@Composable
fun ForecastDetailContent(
    forecastDay: ForecastDayDomain?,
    modifier: Modifier = Modifier
) {
    if (forecastDay == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Dados do dia não disponíveis.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Data",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${forecastDay.dayLabel} • ${forecastDay.dayOfWeek}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = forecastDay.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    WeatherIcon(
                        iconUrl = forecastDay.iconUrl,
                        contentDescription = forecastDay.conditionText,
                        modifier = Modifier.size(72.dp)
                    )

                    Text(
                        text = forecastDay.conditionText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Máx ${forecastDay.maxTemp.toInt()}°C",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Mín ${forecastDay.minTemp.toInt()}°C",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BasicMetricCard(
                title = "Chuva",
                value = "${forecastDay.chanceOfRain}%",
                subtitle = "Probabilidade",
                icon = Icons.Default.WaterDrop,
                iconColor = RainBlue,
                modifier = Modifier.weight(1f)
            )

            BasicMetricCard(
                title = "Temperatura Média",
                value = "${forecastDay.avgTemp.toInt()}°C",
                subtitle = "Ao longo do dia",
                icon = Icons.Default.Thermostat,
                iconColor = WeatherBluePrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BasicMetricCard(
                title = "Humidade",
                value = "${forecastDay.humidity}%",
                subtitle = "Humidade média",
                icon = Icons.Default.WaterDrop,
                iconColor = WeatherBlueSecondary,
                modifier = Modifier.weight(1f)
            )

            BasicMetricCard(
                title = "Vento",
                value = "${forecastDay.windKph.toInt()} km/h",
                subtitle = "Velocidade máxima",
                icon = Icons.Default.Air,
                iconColor = WeatherBluePrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BasicMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}

@Preview(showBackground = true, name = "ForecastDetail Light", heightDp = 720)
@Composable
fun ForecastDetailPreview() {
    WeatherSearchTheme {
        ForecastDetailContent(
            forecastDay = ForecastDayDomain(
                dayIndex = 1,
                dayLabel = "Amanhã",
                date = "2026-11-18",
                dayOfWeek = "Quarta-feira, 18 de Novembro",
                maxTemp = 19.4,
                minTemp = 11.2,
                avgTemp = 15.3,
                conditionText = "Parcialmente nublado",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                chanceOfRain = 25,
                windKph = 14.8,
                humidity = 72
            )
        )
    }
}

@Preview(showBackground = true, name = "ForecastDetail Empty", heightDp = 400)
@Composable
fun ForecastDetailEmptyPreview() {
    WeatherSearchTheme {
        ForecastDetailContent(
            forecastDay = null
        )
    }
}
