package upca.example.weathersearch.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

// DTOs mapeados diretamente da resposta JSON da WeatherAPI
data class WeatherApiResponse(
    @SerializedName("location") val location: LocationDto? = null,
    @SerializedName("current") val current: CurrentDto? = null,
    @SerializedName("forecast") val forecast: ForecastDto? = null
)

data class LocationDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("country") val country: String? = null
)

data class ConditionDto(
    @SerializedName("text") val text: String? = null,
    @SerializedName("icon") val icon: String? = null
)

data class CurrentDto(
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("feelslike_c") val feelsLikeC: Double? = null,
    @SerializedName("condition") val condition: ConditionDto? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("humidity") val humidity: Int? = null
)

data class ForecastDto(
    @SerializedName("forecastday") val forecastday: List<ForecastDayDto>? = null
)

data class ForecastDayDto(
    @SerializedName("date") val date: String? = null,
    @SerializedName("day") val day: DayDto? = null
)

data class DayDto(
    @SerializedName("maxtemp_c") val maxTempC: Double? = null,
    @SerializedName("mintemp_c") val minTempC: Double? = null,
    @SerializedName("avgtemp_c") val avgTempC: Double? = null,
    @SerializedName("maxwind_kph") val maxWindKph: Double? = null,
    @SerializedName("avghumidity") val avgHumidity: Double? = null,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int? = null,
    @SerializedName("condition") val condition: ConditionDto? = null
)

// Modelos de Domínio limpos consumidos pela UI (isolam a UI das alterações na estrutura da API)
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

// A WeatherAPI devolve URLs protocol-relative ("//..."), convertendo-os para HTTPS explícito
fun sanitizeIconUrl(rawUrl: String?): String {
    if (rawUrl.isNullOrBlank()) return ""
    return if (rawUrl.startsWith("//")) "https:$rawUrl" else rawUrl
}

// Converte a data (yyyy-MM-dd) para formato por extenso em português (ex: "Quarta-feira, 18 de Novembro")
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

// Converte os dados brutos da API em modelos de domínio prontos e seguros para a interface
fun WeatherApiResponse.toDomain(): WeatherDomain {
    val loc = this.location
    val cur = this.current

    val daysDomain = this.forecast?.forecastday?.mapIndexed { index, dayDto ->
        // Define rótulos contextuais para os primeiros dias em vez da data crua
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
