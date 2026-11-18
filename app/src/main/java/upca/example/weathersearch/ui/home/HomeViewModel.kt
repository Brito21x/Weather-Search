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
import upca.example.weathersearch.data.repository.WeatherRepository

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Empty)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("Barcelos")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val isCurrentCityBookmarked: StateFlow<Boolean> = combine(
        _uiState,
        repository.favoriteCities
    ) { state, favorites ->
        if (state is HomeUiState.Success) {
            favorites.any { it.equals(state.weather.cityName.trim(), ignoreCase = true) }
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var lastQueriedLocation: String = "Barcelos"

    init {
        searchWeather("Barcelos")
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun searchWeather(cityName: String) {
        val trimmed = cityName.trim()
        if (trimmed.isBlank()) {
            _uiState.value = HomeUiState.Error("Por favor escreva o nome de uma localidade.")
            return
        }

        lastQueriedLocation = trimmed
        _searchQuery.value = trimmed
        _uiState.value = HomeUiState.Loading

        repository.fetchWeather(
            query = trimmed,
            days = 5,
            onSuccess = { domain ->
                _uiState.value = HomeUiState.Success(domain)
            },
            onError = { errorMessage ->
                _uiState.value = HomeUiState.Error(errorMessage)
            }
        )
    }

    fun searchByCoordinates(lat: Double, lon: Double) {
        val query = "$lat,$lon"
        lastQueriedLocation = query
        _uiState.value = HomeUiState.Loading

        repository.fetchWeather(
            query = query,
            days = 5,
            onSuccess = { domain ->
                _searchQuery.value = domain.cityName
                _uiState.value = HomeUiState.Success(domain)
            },
            onError = { errorMessage ->
                _uiState.value = HomeUiState.Error(errorMessage)
            }
        )
    }

    fun retryLastSearch() {
        searchWeather(lastQueriedLocation)
    }

    fun toggleFavoriteCurrentCity() {
        val currentSuccess = _uiState.value as? HomeUiState.Success ?: return
        repository.toggleFavorite(currentSuccess.weather.cityName)
    }

    fun getShareSummary(): String? {
        val currentSuccess = _uiState.value as? HomeUiState.Success ?: return null
        val weather = currentSuccess.weather

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
