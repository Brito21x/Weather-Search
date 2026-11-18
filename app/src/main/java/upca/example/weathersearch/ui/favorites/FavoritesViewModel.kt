package upca.example.weathersearch.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import upca.example.weathersearch.data.repository.WeatherRepository

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    val favoriteCities: StateFlow<Set<String>> = repository.favoriteCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun removeFavorite(cityName: String) {
        repository.toggleFavorite(cityName)
    }
}
