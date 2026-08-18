package com.uriel.logpose.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * DriverProfileStore: Almacenamiento Cifrado de Perfil (Misión #058).
 * v2.0: Uso de Hardware-backed Keystore para blindaje contra extracción física.
 */
object DriverProfileStore {

    private const val PREFS_NAME = "driver_profile_secure_v2"
    private const val KEY_DRIVER_NAME = "driver_name"
    private const val KEY_MUSIC_APP = "preferred_music_app"
    private const val KEY_GAS_STATION = "preferred_gas_station"

    private fun getPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveProfile(context: Context, name: String, musicApp: String, gasStation: String) {
        getPrefs(context).edit().apply {
            putString(KEY_DRIVER_NAME, name)
            putString(KEY_MUSIC_APP, musicApp)
            putString(KEY_GAS_STATION, gasStation)
            apply()
        }
    }

    fun getDriverName(context: Context): String = getPrefs(context).getString(KEY_DRIVER_NAME, "Rider") ?: "Rider"
    
    fun getPreferredMusicApp(context: Context): String = getPrefs(context).getString(KEY_MUSIC_APP, "spotify") ?: "spotify"
    
    fun getPreferredGasStation(context: Context): String = getPrefs(context).getString(KEY_GAS_STATION, "YPF") ?: "YPF"
}
