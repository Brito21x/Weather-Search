package upca.example.weathersearch.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.data.repository.WeatherRepository

data class ForecastDetailViewState(
    val forecastDay: ForecastDayDomain? = null,
    val isLoading: Boolean = false
)

class ForecastDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    private val _uiState = MutableStateFlow(ForecastDetailViewState())
    val uiState: StateFlow<ForecastDetailViewState> = _uiState.asStateFlow()

    fun loadForecastDayByDate(date: String) {
        val weather = repository.currentWeather.value
        val day = weather?.forecastDays?.find { it.date == date }
        _uiState.update { it.copy(forecastDay = day) }
    }
}
