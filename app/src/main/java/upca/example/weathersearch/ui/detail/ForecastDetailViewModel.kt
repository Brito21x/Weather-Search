package upca.example.weathersearch.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import upca.example.weathersearch.data.model.ForecastDayDomain
import upca.example.weathersearch.data.repository.WeatherRepository

class ForecastDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance(application.applicationContext)

    private val _selectedForecastDay = MutableStateFlow<ForecastDayDomain?>(null)
    val selectedForecastDay: StateFlow<ForecastDayDomain?> = _selectedForecastDay.asStateFlow()

    fun loadForecastDayByDate(date: String) {
        val weather = repository.currentWeather.value
        val day = weather?.forecastDays?.find { it.date == date }
        _selectedForecastDay.value = day
    }
}
