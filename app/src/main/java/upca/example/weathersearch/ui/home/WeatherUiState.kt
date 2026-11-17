package upca.example.weathersearch.ui.home

import upca.example.weathersearch.data.model.WeatherDomain

// Estados de interface para controlo de fluxo visual
sealed interface WeatherUiState {
    // Estado inicial sem dados carregados
    object Empty : WeatherUiState

    // Estado durante a execução do pedido assíncrono
    object Loading : WeatherUiState

    // Estado quando os dados meteorológicos foram carregados
    data class Success(val weather: WeatherDomain) : WeatherUiState

    // Estado com mensagem descritiva em caso de falha de rede ou HTTP
    data class Error(val message: String) : WeatherUiState
}
