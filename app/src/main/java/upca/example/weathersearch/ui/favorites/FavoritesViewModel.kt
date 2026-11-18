package upca.example.weathersearch.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import upca.example.weathersearch.data.repository.WeatherRepository

data class FavoritesViewState(
    val favoriteCities: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    val uiState: StateFlow<FavoritesViewState> = repository.favoriteCities
        .map { FavoritesViewState(favoriteCities = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoritesViewState())

    fun removeFavorite(cityName: String) {
        repository.toggleFavorite(cityName)
    }
}
