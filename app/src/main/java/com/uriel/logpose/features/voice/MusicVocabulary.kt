package com.uriel.logpose.features.voice

import androidx.collection.LruCache

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary

/**
 * MusicVocabulary V11: Fuente única de verdad consumiendo el Unified Language Core (ULC).
 */
object MusicVocabulary {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    private val ARTISTS: List<String> get() = dictionary.listaDe("musica.artistas")
    private val SONGS: List<String> get() = dictionary.listaDe("musica.canciones")
    private val LEARNED: Set<String> get() = com.uriel.logpose.thamis.learning.LearningEngine.getLearnedMusicEntities()
    private val THAMIS_DICTIONARY: Map<String, String> get() = dictionary.mapaDe("musica.correcciones_foneticas")
    private val PLAYLISTS: List<String> get() = dictionary.listaDe("musica.playlists")

    private val normalizedCache = LruCache<String, String>(256)

    fun clearCache() {
        normalizedCache.evictAll()
    }

    fun normalize(input: String): String {
        if (input.isBlank()) return ""
        
        // v22.9: Limpieza previa de caracteres para asegurar que la llave de caché sea pura
        val cleanInput = input.lowercase()
            .replace(Regex("[áàäâã]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöôõ]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace(Regex("[^a-z0-9ñ ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        normalizedCache.get(cleanInput)?.let { return it }
        
        var result = cleanInput
        
        // v7.7/v22.9: ADN Musical Aprendido (Prioridad Staff Absoluta)
        com.uriel.logpose.thamis.learning.LearningEngine.getPhoneticCorrection(result)?.let {
            val corrected = it.trim()
            normalizedCache.put(cleanInput, corrected)
            return corrected
        }

        // Luego el diccionario estático
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
        var bestEntity: String? = null
        var bestScore = threshold
        
        val stopwords = setOf("un", "una", "el", "la", "los", "las", "de", "con", "por", "en")
        
        for (entity in (ARTISTS + SONGS + PLAYLISTS + LEARNED)) {
            val entityNorm = normalize(entity)
            if (normalizedQuery == entityNorm) return entity to 1.0
            
            // v7.3: Aplicar peso de afinidad (User Preferences)
            val affinityWeight = com.uriel.logpose.thamis.learning.LearningEngine.getAffinityWeight(entity)
            val baseScore = weightedLevenshteinRatio(normalizedQuery, entityNorm)
            val finalScore = baseScore * affinityWeight // Boost Staff para favoritos

            if (finalScore > bestScore) {
                bestScore = finalScore
                bestEntity = entity
            }
            
            val qTokens = normalizedQuery.split(" ").filter { it.length >= 3 && it !in stopwords }
            val eTokens = entityNorm.split(" ").filter { it.length >= 3 && it !in stopwords }
            for (qt in qTokens) {
                for (et in eTokens) {
                    val tScore = weightedLevenshteinRatio(qt, et) * affinityWeight
                    if (tScore > bestScore) { bestScore = tScore; bestEntity = entity }
                }
            }
        }
        return bestEntity?.let { it to bestScore }
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

    private fun weightedLevenshteinRatio(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        val len1 = s1.length; val len2 = s2.length
        if (len1 == 0 || len2 == 0) return 0.0
        val prev = DoubleArray(len2 + 1)
        val curr = DoubleArray(len2 + 1)
        for (j in 0..len2) prev[j] = j.toDouble()
        for (i in 1..len1) {
            curr[0] = i.toDouble()
            for (j in 1..len2) {
                val c1 = s1[i - 1]; val c2 = s2[j - 1]
                val cost = when {
                    c1 == c2 -> 0.0
                    isSibilant(c1) && isSibilant(c2) -> 0.3
                    isSibilant(c1) || isSibilant(c2) -> 0.5
                    else -> 1.0
                }
                curr[j] = minOf(prev[j] + 1.0, curr[j - 1] + 1.0, prev[j - 1] + cost)
            }
            for (j in 0..len2) prev[j] = curr[j]
        }
        return (maxOf(len1, len2) - prev[len2]) / maxOf(len1, len2)
    }

    private fun isSibilant(c: Char): Boolean = c in setOf('s', 'z', 'x', 'j', 'c')
    fun isKnown(query: String): Boolean = findBestMatch(query, 0.90) != null
}
