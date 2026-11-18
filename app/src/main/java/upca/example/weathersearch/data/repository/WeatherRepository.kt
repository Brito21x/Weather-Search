package upca.example.weathersearch.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.data.network.WeatherApiClient

// Ponto único de acesso a dados (Singleton): partilhado entre os diferentes ViewModels da aplicação
class WeatherRepository private constructor(
    context: Context,
    private val apiClient: WeatherApiClient = WeatherApiClient()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_search_prefs", Context.MODE_PRIVATE)

    // StateFlow observável que notifica a UI de alterações imediatas na lista de favoritos
    private val _favoriteCities = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteCities: StateFlow<Set<String>> = _favoriteCities.asStateFlow()

    // Guarda a última previsão meteorológica obtida para partilha entre ecrãs (ex: detalhe)
    private val _currentWeather = MutableStateFlow<WeatherDomain?>(null)
    val currentWeather: StateFlow<WeatherDomain?> = _currentWeather.asStateFlow()

    private fun loadFavorites(): Set<String> {
        val saved = prefs.getStringSet(KEY_FAVORITES, null)
        return saved?.toSet() ?: setOf("Barcelos", "Braga", "Porto")
    }

    // Adiciona ou remove a cidade (case-insensitive), sincronizando a memória e o armazenamento local
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

    fun isFavorite(cityName: String): Boolean {
        return _favoriteCities.value.any { it.equals(cityName.trim(), ignoreCase = true) }
    }

    fun fetchWeather(
        query: String,
        days: Int = 5,
        onSuccess: (WeatherDomain) -> Unit,
        onError: (String) -> Unit
    ) {
        apiClient.fetchForecastAsync(
            query = query,
            days = days,
            onSuccess = { domain ->
                _currentWeather.value = domain
                onSuccess(domain)
            },
            onError = onError
        )
    }

    companion object {
        private const val KEY_FAVORITES = "key_favorite_cities"

        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(context: Context): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
