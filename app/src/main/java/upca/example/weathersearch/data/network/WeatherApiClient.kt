package upca.example.weathersearch.data.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import upca.example.weathersearch.BuildConfig
import upca.example.weathersearch.data.model.WeatherApiResponse
import upca.example.weathersearch.data.model.WeatherDomain
import upca.example.weathersearch.data.model.toDomain
import com.google.gson.Gson
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

// Cliente HTTP para pedidos assíncronos à WeatherAPI com OkHttp
class WeatherApiClient(
    private val apiKey: String = BuildConfig.WEATHER_API_KEY
) {
    // Cliente OkHttp configurado com timeouts de 15 segundos
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val gson = Gson()

    // Formata a pesquisa para priorizar localidades de Portugal
    private fun formatPortugalQuery(rawQuery: String): String {
        val trimmed = rawQuery.trim()
        val isCoordinates = trimmed.matches(Regex("^-?\\d+(\\.\\d+)?,\\s*-?\\d+(\\.\\d+)?$"))
        return if (!isCoordinates && !trimmed.contains(",")) {
            "$trimmed, Portugal"
        } else {
            trimmed
        }
    }

    // Executa pedido HTTP GET assíncrono na thread de background via enqueue
    fun fetchForecastAsync(
        query: String,
        days: Int = 5,
        onSuccess: (WeatherDomain) -> Unit,
        onError: (String) -> Unit
    ) {
        if (apiKey.isBlank()) {
            onError("Chave da API (API Key) não encontrada no local.properties.")
            return
        }

        if (query.isBlank()) {
            onError("Por favor introduza o nome de uma localidade.")
            return
        }

        val targetQuery = formatPortugalQuery(query)
        val encodedQuery = try {
            URLEncoder.encode(targetQuery, "UTF-8")
        } catch (e: Exception) {
            targetQuery
        }

        val url = "https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=$encodedQuery&days=$days&aqi=no&alerts=no&lang=pt"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        // Execução assíncrona fora da Main Thread com callback OkHttp
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "Sem ligação à Internet. Verifique a rede."
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Tempo limite excedido. Tente novamente."
                    else ->
                        "Falha de rede: ${e.localizedMessage ?: "Erro desconhecido"}"
                }
                onError(errorMsg)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val responseBody = res.body?.string()

                    // Validação de códigos HTTP de resposta
                    if (!res.isSuccessful) {
                        val message = extractErrorMessage(responseBody, res.code)
                        onError(message)
                        return
                    }

                    if (responseBody.isNullOrBlank()) {
                        onError("A API retornou uma resposta vazia.")
                        return
                    }

                    // Conversão do JSON para os modelos de domínio Kotlin
                    try {
                        val apiResponse = gson.fromJson(responseBody, WeatherApiResponse::class.java)
                        val domainData = apiResponse.toDomain()
                        onSuccess(domainData)
                    } catch (e: Exception) {
                        onError("Erro ao processar dados do tempo: ${e.localizedMessage}")
                    }
                }
            }
        })
    }

    // Extrai mensagem descritiva de erro devolvida pela API ou código HTTP
    private fun extractErrorMessage(responseBody: String?, statusCode: Int): String {
        if (!responseBody.isNullOrBlank()) {
            try {
                val jsonObject = JSONObject(responseBody)
                if (jsonObject.has("error")) {
                    val errorObj = jsonObject.getJSONObject("error")
                    val apiErrorMsg = errorObj.optString("message", "")
                    if (apiErrorMsg.isNotBlank()) {
                        return when {
                            apiErrorMsg.contains("No matching location found", ignoreCase = true) ->
                                "Localidade não encontrada em Portugal."
                            apiErrorMsg.contains("API key is invalid", ignoreCase = true) ->
                                "Chave da API inválida ou expirada."
                            else -> apiErrorMsg
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback para código de status
            }
        }
        return when (statusCode) {
            400 -> "Pedido inválido. Verifique o nome da cidade."
            401, 403 -> "Acesso não autorizado à WeatherAPI."
            404 -> "Localidade não encontrada."
            500, 502, 503 -> "Servidor da WeatherAPI temporariamente indisponível."
            else -> "Erro no servidor (HTTP $statusCode)."
        }
    }
}
