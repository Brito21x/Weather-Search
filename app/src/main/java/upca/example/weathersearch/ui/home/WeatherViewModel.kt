package upca.example.weathersearch.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.data.repository.WeatherRepository

// ViewModel responsável pelo estado da UI e orquestração dos pedidos
class WeatherViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application.applicationContext)

    // Estado da UI exposto como StateFlow para a Compose UI
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // Texto da caixa de pesquisa
    private val _searchQuery = MutableStateFlow("Barcelos")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Dia selecionado para navegação lista-detalhe
    private val _selectedForecastDay = MutableStateFlow<ForecastDayDomain?>(null)
    val selectedForecastDay: StateFlow<ForecastDayDomain?> = _selectedForecastDay.asStateFlow()

    // Lista de cidades favoritas guardadas
    val favoriteCities: StateFlow<Set<String>> = repository.favoriteCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private var lastQueriedLocation: String = "Barcelos"

    init {
        // Inicializa com a cidade de Barcelos
        searchWeather("Barcelos")
    }

    // Atualiza o texto da pesquisa
    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Executa a pesquisa para uma localidade
    fun searchWeather(cityName: String) {
        val trimmed = cityName.trim()
        if (trimmed.isBlank()) {
            _uiState.value = WeatherUiState.Error("Por favor escreva o nome de uma localidade.")
            return
        }

        lastQueriedLocation = trimmed
        _searchQuery.value = trimmed
        _uiState.value = WeatherUiState.Loading

        repository.fetchWeather(
            query = trimmed,
            days = 5,
            onSuccess = { domain ->
                _uiState.value = WeatherUiState.Success(domain)
            },
            onError = { errorMessage ->
                _uiState.value = WeatherUiState.Error(errorMessage)
            }
        )
    }

    // Pesquisa a meteorologia com recurso a coordenadas de GPS
    fun searchByCoordinates(lat: Double, lon: Double) {
        val query = "$lat,$lon"
        lastQueriedLocation = query
        _uiState.value = WeatherUiState.Loading

        repository.fetchWeather(
            query = query,
            days = 5,
            onSuccess = { domain ->
                _searchQuery.value = domain.cityName
                _uiState.value = WeatherUiState.Success(domain)
            },
            onError = { errorMessage ->
                _uiState.value = WeatherUiState.Error(errorMessage)
            }
        )
    }

    // Repete a pesquisa da última localidade
    fun retryLastSearch() {
        searchWeather(lastQueriedLocation)
    }

    // Define o dia selecionado para o ecrã de detalhe
    fun selectForecastDay(day: ForecastDayDomain) {
        _selectedForecastDay.value = day
    }

    // Procura o dia selecionado pela data correspondente
    fun selectForecastDayByDate(date: String) {
        val currentSuccess = _uiState.value as? WeatherUiState.Success
        val found = currentSuccess?.weather?.forecastDays?.find { it.date == date }
        if (found != null) {
            _selectedForecastDay.value = found
        }
    }

    // Alterna o favorito da cidade atual
    fun toggleFavoriteCurrentCity() {
        val currentSuccess = _uiState.value as? WeatherUiState.Success ?: return
        repository.toggleFavorite(currentSuccess.weather.cityName)
    }

    // Alterna o favorito de uma cidade especificada
    fun toggleFavoriteCity(cityName: String) {
        repository.toggleFavorite(cityName)
    }

    // Indica se a localidade atual é favorita
    fun isCurrentCityBookmarked(): Boolean {
        val currentSuccess = _uiState.value as? WeatherUiState.Success ?: return false
        return repository.isFavorite(currentSuccess.weather.cityName)
    }

    // Partilha os dados do tempo via Intent ACTION_SEND
    fun shareCurrentWeather(context: Context) {
        val currentSuccess = _uiState.value as? WeatherUiState.Success ?: return
        val weather = currentSuccess.weather

        val shareText = buildString {
            append("Previsão do Tempo para ${weather.cityName}, ${weather.country}:\n")
            append("• Temperatura: ${weather.currentTemp.toInt()}°C (Sensação: ${weather.feelsLike.toInt()}°C)\n")
            append("• Condição: ${weather.conditionText}\n")
            append("• Humidade: ${weather.humidity}%\n")
            append("• Vento: ${weather.windSpeedKph.toInt()} km/h\n")
            if (weather.forecastDays.isNotEmpty()) {
                val nextDay = weather.forecastDays.first()
                append("• Próximo dia: Máx ${nextDay.maxTemp.toInt()}°C / Mín ${nextDay.minTemp.toInt()}°C (${nextDay.conditionText})\n")
            }
            append("\nEnviado através da app Weather Search.")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Partilhar Meteorologia")
        context.startActivity(shareIntent)
    }
}
