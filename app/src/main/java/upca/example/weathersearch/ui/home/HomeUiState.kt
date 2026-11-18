package upca.example.weathersearch.ui.home

import upca.example.weathersearch.data.model.WeatherDomain

sealed interface HomeUiState {
    object Empty : HomeUiState
    object Loading : HomeUiState
    data class Success(val weather: WeatherDomain) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
