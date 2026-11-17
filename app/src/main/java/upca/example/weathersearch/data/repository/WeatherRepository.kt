package upca.example.weathersearch.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.data.network.WeatherApiClient

// Repositório que coordena os pedidos de rede OkHttp e a persistência local
class WeatherRepository(
    context: Context,
    private val apiClient: WeatherApiClient = WeatherApiClient()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_search_prefs", Context.MODE_PRIVATE)

    // StateFlow com o conjunto de cidades favoritas
    private val _favoriteCities = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteCities: StateFlow<Set<String>> = _favoriteCities.asStateFlow()

    // Carrega a lista de favoritos de SharedPreferences com fallback padrão
    private fun loadFavorites(): Set<String> {
        val saved = prefs.getStringSet(KEY_FAVORITES, null)
        return saved?.toSet() ?: setOf("Barcelos", "Braga", "Porto")
    }

    // Alterna o estado de favorito de uma localidade e persiste as alterações
    fun toggleFavorite(cityName: String) {
        val current = _favoriteCities.value.toMutableSet()
        val normalized = cityName.trim()
        if (normalized.isBlank()) return

        if (current.any { it.equals(normalized, ignoreCase = true) }) {
            current.removeAll { it.equals(normalized, ignoreCase = true) }
        } else {
            current.add(normalized)
        }
        _favoriteCities.value = current
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    // Verifica se a cidade indicada já se encontra na lista de favoritos
    fun isFavorite(cityName: String): Boolean {
        return _favoriteCities.value.any { it.equals(cityName.trim(), ignoreCase = true) }
    }

    // Encaminha o pedido de previsão meteorológica para o cliente OkHttp
    fun fetchWeather(
        query: String,
        days: Int = 5,
        onSuccess: (WeatherDomain) -> Unit,
        onError: (String) -> Unit
    ) {
        apiClient.fetchForecastAsync(
            query = query,
            days = days,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    companion object {
        private const val KEY_FAVORITES = "key_favorite_cities"
    }
}
