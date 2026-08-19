package com.uriel.logpose.core.data

import com.uriel.logpose.data.local.LogPoseDao
import com.uriel.logpose.data.local.DriverPreferenceEntity
import kotlinx.coroutines.runBlocking

/**
 * DriverProfileStore v83.0: Arquitectura Room.
 * Perfil del conductor persistido en Room.
 */
class DriverProfileStore(
    private val logPoseDao: LogPoseDao
) {

    companion object {
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_MUSIC_APP = "preferred_music_app"
        private const val KEY_GAS_STATION = "preferred_gas_station"
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
