package upca.example.weathersearch.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.ui.components.CurrentWeatherCard
import upca.example.weathersearch.ui.components.EmptyView
import upca.example.weathersearch.ui.components.ErrorView
import upca.example.weathersearch.ui.components.ForecastDayRow
import upca.example.weathersearch.ui.components.LoadingView
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

// Lista de cidades rápidas declarada no topo para evitar alocações de memória a cada recomposição
private val QUICK_CITIES = listOf("Barcelos", "Braga", "Porto", "Lisboa", "Coimbra", "Faro", "Funchal")

@Composable
fun HomeScreen(
    onNavigateToDetail: (ForecastDayDomain) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Gestor do pedido de permissões de localização (Fine e Coarse GPS) em tempo de execução
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            requestDeviceLocation(context) { lat, lon ->
                viewModel.searchByCoordinates(lat, lon)
            }
        } else {
            Toast.makeText(
                context,
                "Permissão de localização recusada. Introduza a localidade manualmente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    HomeScreenContent(
        searchQuery = searchQuery,
        uiState = uiState,
        quickCities = QUICK_CITIES,
        onQueryChange = { viewModel.onQueryChange(it) },
        onSearch = {
            focusManager.clearFocus()
            viewModel.searchWeather(searchQuery)
        },
        onClearQuery = {
            viewModel.onQueryChange("")
        },
        onGpsClick = {
            focusManager.clearFocus()
            // Se as permissões já estiverem concedidas, obtém a localização; caso contrário, abre o diálogo de permissões
            val fineCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
                requestDeviceLocation(context) { lat, lon ->
                    viewModel.searchByCoordinates(lat, lon)
                }
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        onCityQuickClick = { city ->
            focusManager.clearFocus()
            viewModel.searchWeather(city)
        },
        onRetry = {
            viewModel.retryLastSearch()
        },
        onForecastDayClick = { day ->
            onNavigateToDetail(day)
        },
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    searchQuery: String,
    uiState: HomeUiState,
    quickCities: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onGpsClick: () -> Unit,
    onCityQuickClick: (String) -> Unit,
    onRetry: () -> Unit,
    onForecastDayClick: (ForecastDayDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Pesquisar cidade em Portugal...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    IconButton(onClick = onSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpar texto"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onGpsClick,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Obter Localização Atual",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickCities.forEach { city ->
                AssistChip(
                    onClick = { onCityQuickClick(city) },
                    label = { Text(city) },
                    shape = RoundedCornerShape(12.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Renderização declarativa consoante o estado atual da UI
        when (uiState) {
            is HomeUiState.Loading -> {
                LoadingView(
                    modifier = Modifier.weight(1f),
                    message = "A obter dados meteorológicos..."
                )
            }

            is HomeUiState.Error -> {
                ErrorView(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.weight(1f)
                )
            }

            is HomeUiState.Empty -> {
                EmptyView(
                    modifier = Modifier.weight(1f)
                )
            }

            is HomeUiState.Success -> {
                val weather = uiState.weather

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        CurrentWeatherCard(weather = weather)
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    item {
                        Text(
                            text = "Previsão dos Próximos Dias",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = "Toque num dia para ver os dados básicos detalhados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    items(
                        items = weather.forecastDays,
                        key = { it.date }
                    ) { forecastDay ->
                        ForecastDayRow(
                            forecastDay = forecastDay,
                            onClick = { onForecastDayClick(forecastDay) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

// Obtém as coordenadas GPS via FusedLocationProviderClient com salvaguarda no LocationManager padrão
@SuppressLint("MissingPermission")
private fun requestDeviceLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            } else {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val bestLocation = gpsLocation ?: networkLocation

                if (bestLocation != null) {
                    onLocationReceived(bestLocation.latitude, bestLocation.longitude)
                } else {
                    Toast.makeText(
                        context,
                        "Não foi possível obter a posição GPS. Verifique se o GPS está ativo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Erro ao obter coordenadas GPS: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Erro de localização: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true, name = "HomeScreen Sucesso", heightDp = 800)
@Composable
fun HomeScreenPreview() {
    WeatherSearchTheme {
        HomeScreenContent(
            searchQuery = "Barcelos",
            uiState = HomeUiState.Success(
                weather = WeatherDomain(
                    cityName = "Barcelos",
                    country = "Portugal",
                    currentTemp = 16.0,
                    feelsLike = 15.0,
                    conditionText = "Parcialmente nublado",
                    iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    humidity = 68,
                    windSpeedKph = 14.0,
                    forecastDays = listOf(
                        ForecastDayDomain(
                            dayIndex = 0,
                            dayLabel = "Hoje",
                            date = "2026-11-18",
                            dayOfWeek = "Quarta-feira",
                            maxTemp = 18.0,
                            minTemp = 11.0,
                            avgTemp = 14.5,
                            conditionText = "Parcialmente nublado",
                            iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                            chanceOfRain = 20,
                            windKph = 16.0,
                            humidity = 70
                        ),
                        ForecastDayDomain(
                            dayIndex = 1,
                            dayLabel = "Amanhã",
                            date = "2026-11-19",
                            dayOfWeek = "Quinta-feira",
                            maxTemp = 17.0,
                            minTemp = 10.0,
                            avgTemp = 13.5,
                            conditionText = "Chuva moderada",
                            iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/302.png",
                            chanceOfRain = 75,
                            windKph = 22.0,
                            humidity = 82
                        )
                    )
                )
            ),
            quickCities = listOf("Barcelos", "Braga", "Porto", "Lisboa"),
            onQueryChange = {},
            onSearch = {},
            onClearQuery = {},
            onGpsClick = {},
            onCityQuickClick = {},
            onRetry = {},
            onForecastDayClick = {}
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen Vazio", heightDp = 800)
@Composable
fun HomeScreenEmptyPreview() {
    WeatherSearchTheme {
        HomeScreenContent(
            searchQuery = "",
            uiState = HomeUiState.Empty,
            quickCities = listOf("Barcelos", "Braga", "Porto", "Lisboa"),
            onQueryChange = {},
            onSearch = {},
            onClearQuery = {},
            onGpsClick = {},
            onCityQuickClick = {},
            onRetry = {},
            onForecastDayClick = {}
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen Carregamento", heightDp = 800)
@Composable
fun HomeScreenLoadingPreview() {
    WeatherSearchTheme {
        HomeScreenContent(
            searchQuery = "Barcelos",
            uiState = HomeUiState.Loading,
            quickCities = listOf("Barcelos", "Braga", "Porto", "Lisboa"),
            onQueryChange = {},
            onSearch = {},
            onClearQuery = {},
            onGpsClick = {},
            onCityQuickClick = {},
            onRetry = {},
            onForecastDayClick = {}
        )
    }
}
