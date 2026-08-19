package com.uriel.logpose.features.weather

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.alert.Alert
import com.uriel.logpose.features.alert.AlertManager
import com.uriel.logpose.features.alert.AlertPriority
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class WeatherData(
    val temperature: Double,
    val description: String,
    val rainChance: Int
)

object WeatherManager {

    private var apiKey: String? = null

    fun configure(key: String?) {
        this.apiKey = key
    }

    /**
     * Reporta el clima actual consultando la API pública Open-Meteo
     * en base a las coordenadas de LocationResolver.
     * Si falla la red o no hay conectividad, hace fallback seguro.
     */
    fun reportCurrentWeather(callback: (WeatherData) -> Unit = {}): WeatherData {
        val location = LocationResolver.resolveLocation()
        LogPoseLogger.i("Consultando clima para: ${location.latitude}, ${location.longitude}")

        var result = fetchWeatherFromApi(location.latitude, location.longitude)
        if (result == null) {
            LogPoseLogger.w("Fallback a estimación de clima local")
            result = WeatherData(temperature = 22.0, description = "Soleado", rainChance = 5)
        }

        LogPoseLogger.i("Clima actual: ${result.temperature}°C, ${result.description}, lluvia: ${result.rainChance}%")
        checkRainWarning(result.rainChance)
        callback(result)
        return result
    }

    /**
     * Evalúa el riesgo de lluvia. Si supera el 70%, dispara alerta HIGH en AlertManager.
     */
    fun checkRainWarning(rainChance: Int) {
        if (rainChance > 70) {
            AlertManager.enqueue(
                Alert(
                    title = "Alerta de Lluvia",
                    message = "Probabilidad alta de precipitaciones ($rainChance%). Precaución en ruta.",
                    priority = AlertPriority.HIGH
                )
            )
        }
    }

    private fun fetchWeatherFromApi(lat: Double, lon: Double): WeatherData? {
        return try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,precipitation_probability,weather_code"
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3000
                readTimeout = 3000
            }

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val current = json.optJSONObject("current")
                if (current != null) {
                    val temp = current.optDouble("temperature_2m", 22.0)
                    val rain = current.optInt("precipitation_probability", 10)
                    val code = current.optInt("weather_code", 0)
                    val desc = resolveWeatherCode(code)
                    WeatherData(temperature = temp, description = desc, rainChance = rain)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            LogPoseLogger.w("Error consultando API de clima: ${e.message}")
            null
        }
    }

    private fun resolveWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Despejado"
            1, 2, 3 -> "Parcialmente nublado"
            45, 48 -> "Niebla"
            51, 53, 55 -> "Llovizna"
            61, 63, 65 -> "Lluvia"
            71, 73, 75 -> "Nieve"
            80, 81, 82 -> "Chubascos"
            95, 96, 99 -> "Tormenta eléctrica"
            else -> "Templado"
        }
    }
}
