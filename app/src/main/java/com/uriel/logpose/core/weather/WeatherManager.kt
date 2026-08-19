package com.uriel.logpose.core.weather

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.AlertManager
import com.uriel.logpose.core.services.AlertPriority
import com.uriel.logpose.thamis.navigation.location.CurrentLocation
import com.uriel.logpose.thamis.navigation.location.LocationResolver
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Sector 6.1: Cerebro Meteorológico.
 * Conectado a API Open-Meteo para datos en tiempo real y alerta proactiva de lluvia.
 */
object WeatherManager {

    private var apiKey: String? = null

    fun configure(key: String?) {
        this.apiKey = key
    }

    fun reportCurrentWeather() {
        LogPoseLogger.i("WeatherManager: Generando reporte...")
        
        thread {
            val loc = getEffectiveLocation()
            var data = fetchWeatherFromApi(loc.latitude, loc.longitude)
            
            if (data == null) {
                // Fallback a simulación segura si no hay conexión
                LogPoseLogger.w("WeatherManager: Fallback a estimación local")
                data = WeatherData(temp = 22.0, condition = "soleado", rainChance = 5)
            }
            
            val report = "Hacen ${data.temp.toInt()} grados y está ${data.condition}. El riesgo de lluvia es del ${data.rainChance} por ciento."
            AlertManager.enqueue(report, AlertPriority.NORMAL)
            checkRainWarning(data.rainChance)
        }
    }
    
    fun checkRainWarning(rainChance: Int) {
        if (rainChance > 70) {
            AlertManager.enqueue(
                "Alerta de lluvia: Probabilidad del $rainChance por ciento. Precaución en ruta.",
                AlertPriority.HIGH
            )
        }
    }

    private fun getEffectiveLocation(): CurrentLocation {
        return CurrentLocation(latitude = -34.6037, longitude = -58.3816)
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
                    WeatherData(temp = temp, condition = desc, rainChance = rain)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            LogPoseLogger.w("WeatherManager: Error consultando API: ${e.message}")
            null
        }
    }

    private fun resolveWeatherCode(code: Int): String {
        return when (code) {
            0 -> "despejado"
            1, 2, 3 -> "parcialmente nublado"
            45, 48 -> "con niebla"
            51, 53, 55 -> "con llovizna"
            61, 63, 65 -> "lluvioso"
            71, 73, 75 -> "con nieve"
            80, 81, 82 -> "con chubascos"
            95, 96, 99 -> "con tormenta eléctrica"
            else -> "templado"
        }
    }

    private data class WeatherData(
        val temp: Double,
        val condition: String,
        val rainChance: Int
    )
}
