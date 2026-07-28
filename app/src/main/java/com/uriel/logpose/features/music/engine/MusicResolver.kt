package com.uriel.logpose.features.music.engine

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * MusicResolver v7.0: Motor de resolución inteligente.
 * Ahora detecta el dispositivo activo para asegurar reproducción local.
 */
class MusicResolver {

    private val api: SpotifySearchApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifySearchApi::class.java)
    }

    suspend fun play(query: String): Boolean {
        val token = SpotifyAuthManager.getAccessToken()
        if (token == null) {
            Log.w("MusicResolver", "No hay token de Spotify. Abortando Web API.")
            return false
        }
        val authHeader = "Bearer $token"

        return try {
            // 1. Obtener el Device ID del teléfono actual
            val devices = api.getDevices(authHeader)
            val deviceId = devices.devices.firstOrNull { it.isActive }?.id 
                ?: devices.devices.firstOrNull { it.type.lowercase() == "smartphone" }?.id

            // 2. Buscar el track
            val searchResponse = api.search(authHeader, query, "track", 1)
            val trackUri = searchResponse.tracks.items.firstOrNull()?.uri

            if (trackUri != null) {
                // 3. Reproducir con DeviceID (para evitar errores 404 de Spotify)
                api.play(authHeader, deviceId, PlayRequest(listOf(trackUri)))
                Log.i("MusicResolver", "Reproduciendo track: $trackUri en dispositivo: $deviceId")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("MusicResolver", "Fallo en Web API: ${e.message}")
            false
        }
    }
}

// --- API DEFINITIONS ---

interface SpotifySearchApi {
    @GET("search")
    suspend fun search(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int
    ): SpotifySearchResponse

    @PUT("me/player/play")
    suspend fun play(
        @Header("Authorization") auth: String,
        @Query("device_id") deviceId: String?,
        @Body request: PlayRequest
    )

    @GET("me/player/devices")
    suspend fun getDevices(
        @Header("Authorization") auth: String
    ): SpotifyDeviceResponse
}

data class PlayRequest(@SerializedName("uris") val uris: List<String>)
data class SpotifySearchResponse(@SerializedName("tracks") val tracks: SpotifyTracks)
data class SpotifyTracks(@SerializedName("items") val items: List<SpotifyTrackItem>)
data class SpotifyTrackItem(@SerializedName("uri") val uri: String)

data class SpotifyDeviceResponse(@SerializedName("devices") val devices: List<SpotifyDevice>)
data class SpotifyDevice(
    @SerializedName("id") val id: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("type") val type: String
)
