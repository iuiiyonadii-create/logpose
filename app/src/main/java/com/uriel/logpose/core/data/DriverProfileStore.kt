package com.uriel.logpose.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.uriel.logpose.data.local.LogPoseDao
import com.uriel.logpose.data.local.DriverPreferenceEntity
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DriverProfileStore v83.0: Arquitectura Room + DI (Misión #115).
 * Perfil del conductor persistido en Room con migración automática.
 */
@Singleton
class DriverProfileStore @Inject constructor(
    private val logPoseDao: LogPoseDao
) {

    companion object {
        private const val PREFS_NAME = "driver_profile_secure_v2"
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_MUSIC_APP = "preferred_music_app"
        private const val KEY_GAS_STATION = "preferred_gas_station"
    }

    fun migrateIfNeeded(context: Context) {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        val p = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        if (p.contains(KEY_DRIVER_NAME)) {
            LogPoseLogger.w("DriverProfile", "Migrando perfil de SharedPreferences a Room...")
            val name = p.getString(KEY_DRIVER_NAME, "Rider") ?: "Rider"
            val music = p.getString(KEY_MUSIC_APP, "spotify") ?: "spotify"
            val gas = p.getString(KEY_GAS_STATION, "YPF") ?: "YPF"

            runBlocking {
                logPoseDao.savePreference(DriverPreferenceEntity(KEY_DRIVER_NAME, name))
                logPoseDao.savePreference(DriverPreferenceEntity(KEY_MUSIC_APP, music))
                logPoseDao.savePreference(DriverPreferenceEntity(KEY_GAS_STATION, gas))
            }
            p.edit().clear().apply()
            LogPoseLogger.i("DriverProfile", "Migración completada.")
        }
    }

    fun saveProfile(name: String, musicApp: String, gasStation: String) {
        runBlocking {
            logPoseDao.savePreference(DriverPreferenceEntity(KEY_DRIVER_NAME, name))
            logPoseDao.savePreference(DriverPreferenceEntity(KEY_MUSIC_APP, musicApp))
            logPoseDao.savePreference(DriverPreferenceEntity(KEY_GAS_STATION, gasStation))
        }
    }

    fun getDriverName(): String = runBlocking { logPoseDao.getPreference(KEY_DRIVER_NAME) ?: "Rider" }
    
    fun getPreferredMusicApp(): String = runBlocking { logPoseDao.getPreference(KEY_MUSIC_APP) ?: "spotify" }
    
    fun getPreferredGasStation(): String = runBlocking { logPoseDao.getPreference(KEY_GAS_STATION) ?: "YPF" }
}
