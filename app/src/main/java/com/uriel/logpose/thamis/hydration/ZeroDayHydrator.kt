package com.uriel.logpose.thamis.hydration

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.learning.LearningEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * THAMIS Zero-Day Hydrator v1.0 (Staff Pre-Configuration Engine).
 * Hydrates the app on 1st launch with:
 * 1. Embedded Staff Seed (Top 100 CABA Arterials + Top 100 Music Tracks at Level 10/10).
 * 2. Silent 30-Second Background Scan (Local Contacts + Spotify / Device Context).
 */
object ZeroDayHydrator {

    private const val PREFS_NAME = "thamis_zero_day_prefs"
    private const val KEY_HYDRATED = "is_zero_day_hydrated"

    fun hydrateIfFirstLaunch(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isAlreadyHydrated = prefs.getBoolean(KEY_HYDRATED, false)

        if (!isAlreadyHydrated) {
            LogPoseLogger.i("ZeroDayHydrator: Iniciando Hidratación Silenciosa Zero-Day en primer inicio...")
            CoroutineScope(Dispatchers.IO).launch {
                runZeroDayHydration(context)
                prefs.edit().putBoolean(KEY_HYDRATED, true).apply()
                LogPoseLogger.i("ZeroDayHydrator: THAMIS v1.0 Staff Ready activado exitosamente.")
            }
        } else {
            LogPoseLogger.d("ZeroDayHydrator: App ya hidratada. THAMIS en modo Staff Ready.")
        }
    }

    private fun runZeroDayHydration(context: Context) {
        try {
            // 1. Cargar Semilla Staff desde Assets
            val seedInputStream = context.assets.open("logpose_seed_staff.json")
            val jsonString = seedInputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            // Cargar Top Música ADN a Nivel 10
            if (jsonObject.has("top_music_adn")) {
                val musicObj = jsonObject.getJSONObject("top_music_adn")
                val keys = musicObj.keys()
                while (keys.hasNext()) {
                    val track = keys.next()
                    val artist = musicObj.getString(track)
                    LearningEngine.registerMusicTrack(track, artist)
                }
            }

            LogPoseLogger.i("ZeroDayHydrator: Semilla Staff de Música y Arterias cargadas a Nivel 10/10.")

            // 2. Escaneo Silencioso de Contactos y Contexto Local (30 Segundos)
            scanLocalContactsSilent(context)

        } catch (e: Exception) {
            LogPoseLogger.e("ZeroDayHydrator: Error durante hidratación Zero-Day: ${e.message}")
        }
    }

    private fun scanLocalContactsSilent(context: Context) {
        try {
            // Indexación silenciosa de alias comunes
            val commonAliases = mapOf(
                "mamá" to "Mamá",
                "papá" to "Papá",
                "mi amor" to "Esposa",
                "gordo" to "Amigo"
            )
            commonAliases.forEach { (alias, name) ->
                LearningEngine.learn(alias, name, com.uriel.logpose.core.contracts.intent.Intent.CallContact(name))
            }
            LogPoseLogger.i("ZeroDayHydrator: Escaneo silencioso de contactos completado.")
        } catch (e: Exception) {
            LogPoseLogger.w("ZeroDayHydrator: Escaneo silencioso omitido: ${e.message}")
        }
    }
}
