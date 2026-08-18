package com.uriel.logpose.thamis.learning

import android.content.Context
import android.content.SharedPreferences
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.data.local.*
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * LearningEngine v83.0: Arquitectura Room + DI (Misión #115).
 * ADN Staff persistido en base de datos local blindada.
 */
@Singleton
class LearningEngine @Inject constructor(
    private val logPoseDao: LogPoseDao
) {

    private val userCorrections = ConcurrentHashMap<String, Intent>()
    private val actionFrequency = ConcurrentHashMap<Intent, Int>()
    private val learnedMusicEntities = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedPhoneticMap = ConcurrentHashMap<String, String>()
    private val maturityMap = ConcurrentHashMap<String, Int>()
    private val trackToArtistMap = ConcurrentHashMap<String, String>()
    
    private val fastCache = androidx.collection.LruCache<String, String>(512)
    private val learnedApps = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedContacts = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedPlaylists = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    
    private val favoriteArtists = java.util.Collections.synchronizedSet(mutableSetOf("ysy a", "duki", "trueno", "bizarrap", "milo j", "wos"))

    private val initDeferred = CompletableDeferred<Unit>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {
        scope.launch {
            try {
                migrateFromSharedPrefs(context)
                loadFromDatabase()
                initDeferred.complete(Unit)
                LogPoseLogger.i("🧠 LearningEngine v83.0: ADN Staff inyectado y listo.")
            } catch (e: Exception) {
                LogPoseLogger.e("LearningEngine: Error en carga inicial: ${e.message}")
                initDeferred.complete(Unit)
            }
        }
    }

    private suspend fun loadFromDatabase() {
        logPoseDao.getAllPhonetics().forEach { learnedPhoneticMap[it.hears] = it.actual }
        logPoseDao.getAllMaturity().forEach { maturityMap[it.hears] = it.level }
        logPoseDao.getAllCorrections().forEach { userCorrections[it.spokenText] = Intent.valueOf(it.intentName) }
        logPoseDao.getAllFrequencies().forEach { actionFrequency[Intent.valueOf(it.intentName)] = it.count }
        
        logPoseDao.getAllLearnedEntities().forEach { record ->
            when (record.type) {
                "MUSIC" -> learnedMusicEntities.add(record.value)
                "APP" -> learnedApps.add(record.value)
                "CONTACT" -> learnedContacts.add(record.value)
                "PLAYLIST" -> learnedPlaylists.add(record.value)
            }
        }
        
        logPoseDao.getAllFavoriteArtists().forEach { favoriteArtists.add(it.name) }
        logPoseDao.getAllTrackArtistRelations().forEach { trackToArtistMap[it.track] = it.artist }
    }

    private suspend fun migrateFromSharedPrefs(context: Context) {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        val p = EncryptedSharedPreferences.create(
            context,
            "thamis_learning_secure_v23",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        if (!p.contains("phonetic_map")) return

        LogPoseLogger.w("LearningEngine", "Iniciando migración de SharedPreferences a Room...")

        // Migración de Phonetic Map
        val phoneticJson = p.getString("phonetic_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(phoneticJson)
            json.keys().forEach { key -> 
                logPoseDao.savePhonetic(LearnedPhoneticEntity(key, json.getString(key)))
            }
        } catch (e: Exception) {}

        // Migración de Maturity
        val maturityJson = p.getString("maturity_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(maturityJson)
            json.keys().forEach { key -> 
                logPoseDao.saveMaturity(CommandMaturityEntity(key, json.getInt(key)))
            }
        } catch (e: Exception) {}

        // Entidades
        p.getStringSet("learned_music", emptySet())?.forEach { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "MUSIC", value = it)) }
        p.getStringSet("learned_apps", emptySet())?.forEach { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "APP", value = it)) }
        p.getStringSet("learned_contacts", emptySet())?.forEach { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "CONTACT", value = it)) }
        p.getStringSet("learned_playlists", emptySet())?.forEach { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "PLAYLIST", value = it)) }

        // Correcciones
        val correctionsJson = p.getString("user_corrections", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(correctionsJson)
            json.keys().forEach { key ->
                logPoseDao.saveCorrection(UserCorrectionEntity(key, json.getString(key)))
            }
        } catch (e: Exception) {}

        // Limpiar SP tras migración exitosa
        p.edit().clear().apply()
        LogPoseLogger.i("LearningEngine", "Migración completada. SharedPreferences purgadas.")
    }

    private fun String.toStaffKey() = this.lowercase().trim().replace("'", "").replace(" ", "")

    fun learn(hears: String, actual: String, domain: com.thamis.lab.core.contracts.intent.Intent) {
        if (hears.isBlank() || actual.isBlank()) return
        val key = hears.toStaffKey()
        val cleanActual = actual.lowercase().trim()

        if (learnedPhoneticMap[key] == cleanActual) return

        scope.launch {
            learnedPhoneticMap[key] = cleanActual
            logPoseDao.savePhonetic(LearnedPhoneticEntity(key, cleanActual))
            fastCache.evictAll()
            LogPoseLogger.i("🧠 ADN Staff Reforzado (Room): '$key' -> '$cleanActual'")
        }
    }

    fun updateMaturity(hears: String, success: Boolean) {
        val key = hears.toStaffKey()
        val current = maturityMap[key] ?: 0
        val newLevel = if (success) (current + 1).coerceAtMost(10) else (current - 2).coerceAtLeast(0)
        
        maturityMap[key] = newLevel
        scope.launch { logPoseDao.saveMaturity(CommandMaturityEntity(key, newLevel)) }
    }

    fun getMaturityLevel(hears: String): Int = maturityMap[hears.toStaffKey()] ?: 0
    fun isGraduated(hears: String): Boolean = getMaturityLevel(hears) >= 10

    fun getPhoneticCorrection(text: String): String? {
        val key = text.toStaffKey()
        fastCache.get(key)?.let { return it }
        val actual = learnedPhoneticMap[key]
        if (actual != null) fastCache.put(key, actual)
        return actual
    }

    fun registerCorrection(spokenText: String, actualIntent: Intent) {
        val text = spokenText.lowercase().trim()
        userCorrections[text] = actualIntent
        scope.launch { logPoseDao.saveCorrection(UserCorrectionEntity(text, actualIntent.name)) }
    }

    fun getLearnedIntent(spokenText: String): Intent? = userCorrections[spokenText.lowercase().trim()]
    
    fun registerUsage(intent: Intent) {
        val current = actionFrequency.getOrDefault(intent, 0)
        val newCount = current + 1
        actionFrequency[intent] = newCount
        scope.launch { logPoseDao.saveFrequency(ActionFrequencyEntity(intent.name, newCount)) }
    }

    fun getHabitWeight(intent: Intent): Float {
        val freq = actionFrequency.getOrDefault(intent, 0)
        return when {
            freq > 50 -> 1.25f
            freq > 5 -> 1.05f
            else -> 1.0f
        }
    }

    fun learnMusicEntity(entity: String) {
        if (entity.length < 3) return
        val clean = entity.lowercase().trim()
        if (learnedMusicEntities.add(clean)) {
            scope.launch { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "MUSIC", value = clean)) }
        }
    }

    fun learnPlaylist(name: String) {
        if (name.length < 3) return
        val clean = name.lowercase().trim()
        if (learnedPlaylists.add(clean)) {
            scope.launch { logPoseDao.saveLearnedEntity(LearnedEntityRecord(type = "PLAYLIST", value = clean)) }
        }
    }

    fun getLearnedMusicEntities(): Set<String> = learnedMusicEntities.toSet()
    fun getLearnedPlaylists(): Set<String> = learnedPlaylists.toSet()
    fun getLearnedApps(): Set<String> = learnedApps.toSet()
    fun getLearnedContacts(): Set<String> = learnedContacts.toSet()

    fun getAffinityWeight(entity: String): Float = if (favoriteArtists.any { entity.lowercase().contains(it) }) 1.2f else 1.0f

    fun addFavoriteArtist(artist: String) {
        if (artist.length < 3) return
        val clean = artist.lowercase().trim()
        if (favoriteArtists.add(clean)) {
            scope.launch { logPoseDao.saveFavoriteArtist(FavoriteArtistEntity(clean)) }
        }
    }

    fun learnTrackArtistRelation(track: String, artist: String) {
        if (track.isBlank() || artist.isBlank()) return
        val t = track.lowercase().trim()
        val a = artist.lowercase().trim()
        trackToArtistMap[t] = a
        scope.launch { logPoseDao.saveTrackArtistRelation(TrackToArtistEntity(t, a)) }
    }

    fun getArtistForTrack(track: String): String? = trackToArtistMap[track.lowercase().trim()]
    
    fun forget(spokenText: String) {
        val key = spokenText.toStaffKey()
        learnedPhoneticMap.remove(key)
        maturityMap.remove(key)
        scope.launch { 
            logPoseDao.deletePhonetic(key)
            logPoseDao.deleteMaturity(key)
        }
    }

    fun forgetLast() {
        learnedPhoneticMap.clear()
        maturityMap.clear()
        scope.launch {
            logPoseDao.clearPhonetics()
            logPoseDao.clearMaturity()
        }
    }

    fun cleanOldCache() {
        // Implementación simplificada para v83.0
    }

    fun applyHotPatch(patchJson: String) {
        try {
            val json = org.json.JSONObject(patchJson)
            if (json.optString("type") == "PHONETIC_PATCH") {
                val data = json.getJSONObject("data")
                data.keys().forEach { key ->
                    val value = data.getString(key)
                    learn(key, value, Intent.UNKNOWN)
                    updateMaturity(key, true)
                }
            }
        } catch (e: Exception) {}
    }

    fun isReady() = initDeferred.isCompleted
}
