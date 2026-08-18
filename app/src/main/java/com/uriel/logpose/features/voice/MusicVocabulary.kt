package com.uriel.logpose.features.voice

import androidx.collection.LruCache

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary

/**
 * MusicVocabulary V11: Fuente única de verdad consumiendo el Unified Language Core (ULC).
 */
object MusicVocabulary {

    private var _dictionary: PhoneticDictionary? = null
    private val dictionary: PhoneticDictionary 
        get() = _dictionary ?: PhoneticDictionary(LogPoseApplication.instance)

    fun setDictionaryForTesting(dict: PhoneticDictionary) {
        _dictionary = dict
    }

    fun clearCache() {
        normalizedCache.evictAll()
    }

    private val ARTISTS: List<String> get() = dictionary.listaDe("musica.artistas")
    private val SONGS: List<String> get() = dictionary.listaDe("musica.canciones")
    private val LEARNED: Set<String> get() = com.uriel.logpose.thamis.learning.LearningEngine.getLearnedMusicEntities()
    private val THAMIS_DICTIONARY: Map<String, String> get() = dictionary.mapaDe("musica.correcciones_foneticas")
    private val PLAYLISTS: List<String> get() = dictionary.listaDe("musica.playlists")

    // v57.2: Cache de Entidades Normalizadas para evitar 5000+ normalizaciones por segundo
    private val normalizedEntitiesCache: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        (ARTISTS + SONGS + PLAYLISTS).distinct().forEach {
            map[it] = normalize(it)
        }
        map
    }

    private val fastLookupSet: HashSet<String> by lazy {
        val set = HashSet<String>()
        normalizedEntitiesCache.values.forEach { set.add(it) }
        set
    }

    private val normalizedCache = LruCache<String, String>(512)

    // v57.0: Regex pre-compilados para ahorrar ciclos de CPU (Misión #057)
    private val DIACRITICS_A = Regex("[áàäâã]")
    private val DIACRITICS_E = Regex("[éèëê]")
    private val DIACRITICS_I = Regex("[íìïî]")
    private val DIACRITICS_O = Regex("[óòöôõ]")
    private val DIACRITICS_U = Regex("[úùüû]")
    private val CLEAN_CHAR_REGEX = Regex("[^a-z0-9ñ ]")
    private val SPACES_REGEX = Regex("\\s+")

    fun normalize(input: String): String {
        if (input.isBlank()) return ""
        
        // v22.9: Limpieza previa de caracteres para asegurar que la llave de caché sea pura
        val cleanInput = input.lowercase()
            .replace(DIACRITICS_A, "a")
            .replace(DIACRITICS_E, "e")
            .replace(DIACRITICS_I, "i")
            .replace(DIACRITICS_O, "o")
            .replace(DIACRITICS_U, "u")
            .replace(CLEAN_CHAR_REGEX, " ")
            .replace(SPACES_REGEX, " ")
            .trim()

        normalizedCache.get(cleanInput)?.let { return it }
        
        var result = cleanInput
        
        // v7.7/v22.9: ADN Musical Aprendido (Prioridad Staff Absoluta)
        com.uriel.logpose.thamis.learning.LearningEngine.getPhoneticCorrection(result)?.let {
            val corrected = it.trim()
            normalizedCache.put(cleanInput, corrected)
            return corrected
        }

        // Luego el diccionario estático (v99.6: STAFF FIX - Reemplazo por límites de palabra \b para evitar efecto eco)
        for ((hears, targets) in THAMIS_DICTIONARY) {
            val regex = Regex("\\b${Regex.escape(hears)}\\b", RegexOption.IGNORE_CASE)
            if (result.contains(regex)) {
                result = result.replace(regex, targets)
            }
        }
        
        normalizedCache.put(input, result.trim())
        return result.trim()
    }

    fun getGrammarPhases(): List<String> {
        return (ARTISTS + SONGS + PLAYLISTS + LEARNED.toList() + THAMIS_DICTIONARY.keys).distinct()
    }

    data class ResolutionResult(
        val canonical: String,
        val confidence: Double,
        val strategy: String
    )

    fun smartResolve(partial: String, threshold: Double = 0.65): ResolutionResult {
        if (partial.isBlank()) return ResolutionResult("", 0.0, "empty")
        val exact = findBestMatch(partial, threshold = 0.95)
        if (exact != null && exact.second >= 0.90) return ResolutionResult(exact.first, exact.second, "exact")
        val reconstructed = reconstructFromPartial(partial)
        if (reconstructed != null) return ResolutionResult(reconstructed, 0.92, "reconstruction")
        val fuzzy = findBestMatch(partial, threshold = threshold)
        if (fuzzy != null) return ResolutionResult(fuzzy.first, fuzzy.second, "weighted_fuzzy")
        return ResolutionResult(partial, 0.0, "unresolved")
    }

    fun findBestMatch(query: String, threshold: Double = 0.72): Pair<String, Double>? {
        if (query.isBlank()) return null
        val normalizedQuery = normalize(query)
        
        // v57.3: Bypass Instantáneo si el match es perfecto
        if (fastLookupSet.contains(normalizedQuery)) {
            // Buscamos la entidad original (pobre en performance pero solo ocurre una vez)
            val original = normalizedEntitiesCache.entries.find { it.value == normalizedQuery }?.key ?: normalizedQuery
            return original to 1.0
        }

        var bestEntity: String? = null
        var bestScore = threshold
        
        val stopwords = setOf("un", "una", "el", "la", "los", "las", "de", "con", "por", "en")
        val qTokens = if (normalizedQuery.contains(" ")) normalizedQuery.split(" ").filter { it.length >= 3 && it !in stopwords } else emptyList()
        
        // Iteramos sobre el cache pre-normalizado
        for ((entity, entityNorm) in normalizedEntitiesCache) {
            val affinityWeight = com.uriel.logpose.thamis.learning.LearningEngine.getAffinityWeight(entity).toDouble()
            val baseScore = weightedLevenshteinRatio(normalizedQuery, entityNorm)
            val finalScore = baseScore * affinityWeight

            if (finalScore > bestScore) {
                bestScore = finalScore
                bestEntity = entity
            }
            
            if (qTokens.isNotEmpty() && entityNorm.contains(" ")) {
                val eTokens = entityNorm.split(" ").filter { it.length >= 3 && it !in stopwords }
                for (qt in qTokens) {
                    for (et in eTokens) {
                        val tScore = weightedLevenshteinRatio(qt, et) * affinityWeight
                        if (tScore > bestScore) { 
                            bestScore = tScore
                            bestEntity = entity 
                        }
                    }
                }
            }
        }
        return bestEntity?.let { it to bestScore }
    }

    private fun weightedLevenshteinRatio(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        val n = s1.length
        val m = s2.length
        if (n == 0 || m == 0) return 0.0

        // v57.2: Optimización con FloatArray (Más liviano para CPU móvil)
        var prev = FloatArray(m + 1)
        var curr = FloatArray(m + 1)

        for (j in 0..m) prev[j] = j.toFloat()

        for (i in 1..n) {
            curr[0] = i.toFloat()
            val c1 = s1[i - 1]
            for (j in 1..m) {
                val c2 = s2[j - 1]
                val cost = if (c1 == c2) 0.0f else {
                    if (isSibilant(c1) && isSibilant(c2)) 0.3f else 1.0f
                }
                curr[j] = minOf(curr[j - 1] + 1.0f, prev[j] + 1.0f, prev[j - 1] + cost)
            }
            val temp = prev
            prev = curr
            curr = temp
        }

        return (maxOf(n, m).toDouble() - prev[m].toDouble()) / maxOf(n, m).toDouble()
    }

    fun reconstructFromPartial(partial: String): String? {
        if (partial.isBlank() || partial.length < 2) return null
        val normPartial = normalize(partial)
        val candidates = ARTISTS.filter { normalize(it).startsWith(normPartial) }
        return if (candidates.size == 1) candidates[0] else candidates.minByOrNull { it.length }
    }

    fun getAllNames(): List<String> = ARTISTS + SONGS + PLAYLISTS + LEARNED.toList()

    fun findExact(candidate: String): String? {
        val normCandidate = normalize(candidate)
        return (ARTISTS + SONGS + PLAYLISTS + LEARNED).find { normalize(it) == normCandidate }
    }

    fun getAllArtists(): List<String> = ARTISTS
    fun getAllSongs(): List<String> = SONGS
    fun getAllPlaylists(): List<String> = PLAYLISTS

    private fun isSibilant(c: Char): Boolean = c in setOf('s', 'z', 'x', 'j', 'c')
    
    fun isKnown(query: String): Boolean {
        if (query.isBlank()) return false
        val norm = normalize(query)
        // Match instantáneo O(1)
        if (fastLookupSet.contains(norm)) return true
        // Solo si no hay match exacto, hacemos el fuzzy pesado
        return findBestMatch(query, 0.90) != null
    }
}
