package upca.example.weathersearch.ui.home

import upca.example.weathersearch.data.model.WeatherDomain

// Estados possíveis do ecrã para renderização determinística da UI (Empty, Loading, Success, Error)
sealed interface WeatherUiState {
    object Empty : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val weather: WeatherDomain) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
