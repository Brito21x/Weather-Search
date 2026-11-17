package upca.example.weathersearch.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

// DTO para a resposta da WeatherAPI
data class WeatherApiResponse(
    @SerializedName("location") val location: LocationDto? = null,
    @SerializedName("current") val current: CurrentDto? = null,
    @SerializedName("forecast") val forecast: ForecastDto? = null
)

// DTO com os dados de localização
data class LocationDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("country") val country: String? = null
)

// DTO com a condição e ícone meteorológico
data class ConditionDto(
    @SerializedName("text") val text: String? = null,
    @SerializedName("icon") val icon: String? = null
)

// DTO com as métricas do tempo atual
data class CurrentDto(
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("feelslike_c") val feelsLikeC: Double? = null,
    @SerializedName("condition") val condition: ConditionDto? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("humidity") val humidity: Int? = null
)

// DTO com a lista de dias de previsão
data class ForecastDto(
    @SerializedName("forecastday") val forecastday: List<ForecastDayDto>? = null
)

// DTO de cada dia da previsão
data class ForecastDayDto(
    @SerializedName("date") val date: String? = null,
    @SerializedName("day") val day: DayDto? = null
)

// DTO com os dados básicos do dia da previsão
data class DayDto(
    @SerializedName("maxtemp_c") val maxTempC: Double? = null,
    @SerializedName("mintemp_c") val minTempC: Double? = null,
    @SerializedName("avgtemp_c") val avgTempC: Double? = null,
    @SerializedName("maxwind_kph") val maxWindKph: Double? = null,
    @SerializedName("avghumidity") val avgHumidity: Double? = null,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int? = null,
    @SerializedName("condition") val condition: ConditionDto? = null
)

// Modelo de domínio para o tempo atual e previsão
data class WeatherDomain(
    val cityName: String,
    val country: String,
    val currentTemp: Double,
    val feelsLike: Double,
    val conditionText: String,
    val iconUrl: String,
    val humidity: Int,
    val windSpeedKph: Double,
    val forecastDays: List<ForecastDayDomain>
)

// Modelo de domínio com dados básicos do dia de previsão
data class ForecastDayDomain(
    val dayIndex: Int,
    val dayLabel: String,
    val date: String,
    val dayOfWeek: String,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double,
    val conditionText: String,
    val iconUrl: String,
    val chanceOfRain: Int,
    val windKph: Double,
    val humidity: Int
)

// Garante o prefixo https nos ícones da WeatherAPI
fun sanitizeIconUrl(rawUrl: String?): String {
    if (rawUrl.isNullOrBlank()) return ""
    return if (rawUrl.startsWith("//")) "https:$rawUrl" else rawUrl
}

// Converte a data da API para dia da semana legível em português
fun formatDateToDayOfWeek(dateString: String?): String {
    if (dateString.isNullOrBlank()) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString) ?: return dateString
        val formatter = SimpleDateFormat("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-PT"))
        formatter.format(date).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("pt-PT")) else it.toString() }
    } catch (e: Exception) {
        dateString
    }
}

// Mapeia DTOs da API para o modelo de domínio da aplicação
fun WeatherApiResponse.toDomain(): WeatherDomain {
    val loc = this.location
    val cur = this.current

    val daysDomain = this.forecast?.forecastday?.mapIndexed { index, dayDto ->
        val label = when (index) {
            0 -> "Hoje"
            1 -> "Amanhã"
            2 -> "Depois de amanhã"
            else -> formatDateToDayOfWeek(dayDto.date)
        }

        ForecastDayDomain(
            dayIndex = index,
            dayLabel = label,
            date = dayDto.date ?: "",
            dayOfWeek = formatDateToDayOfWeek(dayDto.date),
            maxTemp = dayDto.day?.maxTempC ?: 0.0,
            minTemp = dayDto.day?.minTempC ?: 0.0,
            avgTemp = dayDto.day?.avgTempC ?: 0.0,
            conditionText = dayDto.day?.condition?.text ?: "N/D",
            iconUrl = sanitizeIconUrl(dayDto.day?.condition?.icon),
            chanceOfRain = dayDto.day?.dailyChanceOfRain ?: 0,
            windKph = dayDto.day?.maxWindKph ?: 0.0,
            humidity = (dayDto.day?.avgHumidity ?: 0.0).toInt()
        )
    } ?: emptyList()

    return WeatherDomain(
        cityName = loc?.name ?: "Localização desconhecida",
        country = loc?.country ?: "Portugal",
        currentTemp = cur?.tempC ?: 0.0,
        feelsLike = cur?.feelsLikeC ?: 0.0,
        conditionText = cur?.condition?.text ?: "Céu Limpo",
        iconUrl = sanitizeIconUrl(cur?.condition?.icon),
        humidity = cur?.humidity ?: 0,
        windSpeedKph = cur?.windKph ?: 0.0,
        forecastDays = daysDomain
    )
}
