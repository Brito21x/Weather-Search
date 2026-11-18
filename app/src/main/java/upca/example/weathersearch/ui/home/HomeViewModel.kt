package upca.example.weathersearch.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.data.repository.WeatherRepository

data class HomeViewState(
    val searchQuery: String = "Barcelos",
    val weather: WeatherDomain? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    private val _uiState = MutableStateFlow(HomeViewState())
    val uiState: StateFlow<HomeViewState> = _uiState.asStateFlow()

    val isCurrentCityBookmarked: StateFlow<Boolean> = combine(
        _uiState,
        repository.favoriteCities
    ) { state, favorites ->
        val city = state.weather?.cityName?.trim()
        if (!city.isNullOrBlank()) {
            favorites.any { it.equals(city, ignoreCase = true) }
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var lastQueriedLocation: String = "Barcelos"

    init {
        searchWeather("Barcelos")
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun searchWeather(cityName: String) {
        val trimmed = cityName.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor escreva o nome de uma localidade.", isLoading = false) }
            return
        }

        lastQueriedLocation = trimmed
        _uiState.update { it.copy(searchQuery = trimmed, isLoading = true, errorMessage = null) }

        repository.fetchWeather(
            query = trimmed,
            days = 5,
            onSuccess = { domain ->
                _uiState.update { it.copy(weather = domain, isLoading = false, errorMessage = null) }
            },
            onError = { errorMessage ->
                _uiState.update { it.copy(errorMessage = errorMessage, isLoading = false) }
            }
        )
    }

    fun searchByCoordinates(lat: Double, lon: Double) {
        val query = "$lat,$lon"
        lastQueriedLocation = query
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        repository.fetchWeather(
            query = query,
            days = 5,
            onSuccess = { domain ->
                _uiState.update { it.copy(searchQuery = domain.cityName, weather = domain, isLoading = false, errorMessage = null) }
            },
            onError = { errorMessage ->
                _uiState.update { it.copy(errorMessage = errorMessage, isLoading = false) }
            }
        )
    }

    fun retryLastSearch() {
        searchWeather(lastQueriedLocation)
    }

    fun toggleFavoriteCurrentCity() {
        val weather = _uiState.value.weather ?: return
        repository.toggleFavorite(weather.cityName)
    }

    fun getShareSummary(): String? {
        val weather = _uiState.value.weather ?: return null

        return buildString {
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
    }
}
