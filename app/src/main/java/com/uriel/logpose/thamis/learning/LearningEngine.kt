package com.uriel.logpose.thamis.learning

import android.content.Context
import android.content.SharedPreferences
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.contracts.intent.Intent
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * LearningEngine v22.8: Full API Staff Sincronizada.
 * Restauración de compatibilidad total y blindaje Thread-Safe.
 */
object LearningEngine {

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
    private var prefs: SharedPreferences? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun initialize(context: Context) {
        if (initDeferred.isCompleted) return
        try {
            val p = context.getSharedPreferences("thamis_learning_v22", Context.MODE_PRIVATE)
            prefs = p
            loadMemory(p)
            initDeferred.complete(Unit)
            LogPoseLogger.i("🧠 LearningEngine v22.8: Memoria Staff Integral lista.")
        } catch (e: Exception) {
            LogPoseLogger.e("LearningEngine: Error: ${e.message}")
        }
    }

    private fun String.toStaffKey() = this.lowercase().trim().replace("'", "").replace(" ", "")

    private fun loadMemory(p: SharedPreferences) {
        val phoneticJson = p.getString("phonetic_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(phoneticJson)
            json.keys().forEach { key -> learnedPhoneticMap[key] = json.getString(key) }
        } catch (e: Exception) {}

        val maturityJson = p.getString("maturity_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(maturityJson)
            json.keys().forEach { key -> maturityMap[key] = json.getInt(key) }
        } catch (e: Exception) {}

        p.getStringSet("learned_music", emptySet())?.let { learnedMusicEntities.addAll(it) }
        p.getStringSet("learned_apps", emptySet())?.let { learnedApps.addAll(it) }
        p.getStringSet("learned_contacts", emptySet())?.let { learnedContacts.addAll(it) }
        p.getStringSet("learned_playlists", emptySet())?.let { learnedPlaylists.addAll(it) }
        
        val correctionsJson = p.getString("user_corrections", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(correctionsJson)
            json.keys().forEach { key ->
                try { userCorrections[key] = Intent.valueOf(json.getString(key)) } catch (e: Exception) {}
            }
        } catch (e: Exception) {}
    }

    private fun saveMemoryImmediate() {
        prefs?.edit()?.apply {
            val phoneticJson = org.json.JSONObject()
            learnedPhoneticMap.forEach { (k, v) -> phoneticJson.put(k, v) }
            putString("phonetic_map", phoneticJson.toString())
            
            val matJson = org.json.JSONObject()
            maturityMap.forEach { (k, v) -> matJson.put(k, v) }
            putString("maturity_map", matJson.toString())

            putStringSet("learned_music", learnedMusicEntities.toSet())
            putStringSet("learned_apps", learnedApps.toSet())
            putStringSet("learned_contacts", learnedContacts.toSet())
            putStringSet("learned_playlists", learnedPlaylists.toSet())
            
            val correctionsJson = org.json.JSONObject()
            userCorrections.forEach { (k, v) -> correctionsJson.put(k, v.name) }
            putString("user_corrections", correctionsJson.toString())
            
            apply()
        }
    }

    suspend fun learn(hears: String, actual: String, domain: com.thamis.lab.core.contracts.intent.Intent) {
        if (hears.isBlank() || actual.isBlank()) return
        initDeferred.await()
        
        val key = hears.toStaffKey()
        val cleanActual = actual.lowercase().trim()
        
        // v22.10: Siempre limpiamos la caché ante un intento de aprendizaje 
        // para evitar "fantasmas" de normalización.
        fastCache.evictAll()
        com.uriel.logpose.features.voice.MusicVocabulary.clearCache()

        if (learnedPhoneticMap[key] == cleanActual) return
        
        learnedPhoneticMap[key] = cleanActual
        LogPoseLogger.i("🧠 ADN Staff Reforzado: '$key' -> '$cleanActual'")
        
        saveMemoryImmediate()
    }

    fun updateMaturity(hears: String, success: Boolean) {
        val key = hears.toStaffKey()
        val current = maturityMap[key] ?: 0
        if (success) {
            if (current < 10) maturityMap[key] = current + 1
        } else {
            maturityMap[key] = (current - 2).coerceAtLeast(0)
        }
        saveMemoryImmediate()
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
        scope.launch {
            initDeferred.await()
            userCorrections[spokenText.lowercase().trim()] = actualIntent
            saveMemoryImmediate()
        }
    }

    fun getLearnedIntent(spokenText: String): Intent? = userCorrections[spokenText.lowercase().trim()]
    
    fun registerUsage(intent: Intent) {
        val current = actionFrequency.getOrDefault(intent, 0)
        actionFrequency[intent] = current + 1
        if (current % 5 == 0) saveMemoryImmediate() 
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
        if (learnedMusicEntities.add(entity.lowercase().trim())) {
            saveMemoryImmediate()
        }
    }

    fun learnPlaylist(name: String) {
        if (name.length < 3) return
        if (learnedPlaylists.add(name.lowercase().trim())) {
            saveMemoryImmediate()
        }
    }

    fun getLearnedMusicEntities(): Set<String> = learnedMusicEntities.toSet()
    fun getLearnedPlaylists(): Set<String> = learnedPlaylists.toSet()
    fun getLearnedApps(): Set<String> = learnedApps.toSet()
    fun getLearnedContacts(): Set<String> = learnedContacts.toSet()

    fun getAffinityWeight(entity: String): Float = if (favoriteArtists.any { entity.lowercase().contains(it) }) 1.2f else 1.0f

    fun addFavoriteArtist(artist: String) {
        if (artist.length < 3) return
        if (favoriteArtists.add(artist.lowercase().trim())) {
            saveMemoryImmediate()
        }
    }

    fun learnTrackArtistRelation(track: String, artist: String) {
        if (track.isBlank() || artist.isBlank()) return
        trackToArtistMap[track.lowercase().trim()] = artist.lowercase().trim()
    }

    fun getArtistForTrack(track: String): String? = trackToArtistMap[track.lowercase().trim()]
    
    fun forget(spokenText: String) {
        val key = spokenText.toStaffKey()
        val keyWithSpaces = spokenText.lowercase().trim().replace(" ", "")
        learnedPhoneticMap.remove(key)
        learnedPhoneticMap.remove(keyWithSpaces)
        maturityMap.remove(key)
        maturityMap.remove(keyWithSpaces)
        saveMemoryImmediate()
        com.uriel.logpose.features.voice.MusicVocabulary.clearCache()
    }

    fun forgetLast() {
        learnedPhoneticMap.clear()
        maturityMap.clear()
        saveMemoryImmediate()
        com.uriel.logpose.features.voice.MusicVocabulary.clearCache()
    }

    fun isReady() = initDeferred.isCompleted
}
