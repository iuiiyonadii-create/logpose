package com.uriel.logpose.thamis.learning

import android.content.Context
import android.content.SharedPreferences
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.contracts.intent.Intent
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * LearningEngine v23.0: ADN Staff Cifrado por Hardware (Misión #058).
 */
object LearningEngine {

    private val userCorrections = ConcurrentHashMap<String, Intent>()
    private val actionFrequency = ConcurrentHashMap<Intent, Int>()
    private val learnedMusicEntities = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedPhoneticMap = ConcurrentHashMap<String, String>()
    private val maturityMap = ConcurrentHashMap<String, Int>()
    private val trackToArtistMap = ConcurrentHashMap<String, String>().apply {
        put("morocha", "milo j")
        put("she don't give a fo", "duki")
        put("she dont give a fo", "duki")
        put("goteo", "duki")
        put("uzbekistan", "ysy a")
        put("platino y oro", "ysy a")
        put("arrancarmelo", "wos")
        put("dance criollo", "trueno")
        put("la razon que te demora", "la renga")
        put("spaghetti del rock", "divididos")
        put("nos siguen pegando abajo", "charly garcia")
        put("irresponsables", "babasonicos")
        put("crimen", "gustavo cerati")
        put("flaca", "andres calamaro")
    }
    
    private val fastCache = androidx.collection.LruCache<String, String>(512)
    private val learnedApps = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedContacts = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    private val learnedPlaylists = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    
    private val favoriteArtists = java.util.Collections.synchronizedSet(mutableSetOf("ysy a", "duki", "trueno", "bizarrap", "milo j", "wos"))

    /**
     * Limita el tamaño de la memoria en RAM (Máximo 2000 entradas LRU)
     */
    fun enforceMemoryCaps() {
        if (learnedPhoneticMap.size > 2000) {
            val keysToRemove = learnedPhoneticMap.keys.take(500)
            keysToRemove.forEach {
                learnedPhoneticMap.remove(it)
                maturityMap.remove(it)
            }
            LogPoseLogger.w("LearningEngine: Purga LRU ejecutada -> Se eliminaron 500 entradas antiguas para resguardar la RAM.")
        }
    }

    private val initDeferred = CompletableDeferred<Unit>()
    private var prefs: SharedPreferences? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun initialize(context: Context) {
        if (initDeferred.isCompleted) return
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val p = EncryptedSharedPreferences.create(
                context,
                "thamis_learning_secure_v23",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            prefs = p
            loadMemory(p)
            initDeferred.complete(Unit)
            LogPoseLogger.i("🧠 LearningEngine v23.0: Memoria Staff CIFRADA lista.")
        } catch (e: Exception) {
            LogPoseLogger.e("LearningEngine: Error crítico de cifrado: ${e.message}")
        }
    }

    private fun String.toStaffKey() = this.lowercase().trim().replace("'", "").replace(" ", "")

    private fun loadMemory(p: SharedPreferences) {
        val phoneticJson = p.getString("phonetic_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(phoneticJson)
            json.keys().forEach { key -> learnedPhoneticMap[key] = json.getString(key) }
        } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }

        val maturityJson = p.getString("maturity_map", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(maturityJson)
            json.keys().forEach { key -> maturityMap[key] = json.getInt(key) }
        } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }

        p.getStringSet("learned_music", emptySet())?.let { learnedMusicEntities.addAll(it) }
        p.getStringSet("learned_apps", emptySet())?.let { learnedApps.addAll(it) }
        p.getStringSet("learned_contacts", emptySet())?.let { learnedContacts.addAll(it) }
        p.getStringSet("learned_playlists", emptySet())?.let { learnedPlaylists.addAll(it) }
        
        val correctionsJson = p.getString("user_corrections", "{}") ?: "{}"
        try {
            val json = org.json.JSONObject(correctionsJson)
            json.keys().forEach { key ->
                try { userCorrections[key] = Intent.valueOf(json.getString(key)) } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }
            }
        } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }
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
        // com.uriel.logpose.features.voice.MusicVocabulary.clearCache() // v58.1: Temporalmente comentado para resolver circularidad de build

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
        // com.uriel.logpose.features.voice.MusicVocabulary.clearCache()
    }

    fun forgetLast() {
        learnedPhoneticMap.clear()
        maturityMap.clear()
        saveMemoryImmediate()
        // com.uriel.logpose.features.voice.MusicVocabulary.clearCache()
    }

    /**
     * Poda de Memoria Dinámica (Limpieza de caché vieja > 15 días)
     */
    fun cleanOldCache() {
        val fifteenDaysAgo = System.currentTimeMillis() - (15 * 24 * 60 * 60 * 1000L)
        val iterator = learnedPhoneticMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val isFavorite = favoriteArtists.any { entry.value.lowercase().contains(it) }
            if (!isFavorite) {
                iterator.remove()
            }
        }
        saveMemoryImmediate()
    }

    fun getUserRegistry(): Map<String, Intent> = userCorrections.toMap()

    /**
     * v67.6: Inyector de Parches Staff desde Labs (Misión #067).
     * Permite que el Dashboard de la PC actualice el ADN de Thamis en caliente.
     */
    fun applyHotPatch(patchJson: String) {
        try {
            val json = org.json.JSONObject(patchJson)
            val type = json.optString("type", "UNKNOWN")
            
            if (type == "PHONETIC_PATCH") {
                val data = json.getJSONObject("data")
                data.keys().forEach { key ->
                    val value = data.getString(key)
                    learnedPhoneticMap[key.toStaffKey()] = value
                    maturityMap[key.toStaffKey()] = 10 // Forzamos graduación inmediata
                    LogPoseLogger.i("🧠 MATRIX PATCH: '$key' graduada como '$value' por mando remoto.")
                }
                saveMemoryImmediate()
                fastCache.evictAll()
                
                // v68.0: Notificamos a Vosk para que actualice su oído en caliente
                try {
                    com.uriel.logpose.core.app.LogPoseApplication.entryPoint.voskVoiceEngine().updateGrammar()
                } catch (e: Exception) {
                    LogPoseLogger.w("LearningEngine: No se pudo actualizar gramática de Vosk en vivo.")
                }
            }
        } catch (e: Exception) {
            LogPoseLogger.e("LearningEngine: Error al aplicar parche de inteligencia: ${e.message}")
        }
    }

    fun isReady() = initDeferred.isCompleted
}
