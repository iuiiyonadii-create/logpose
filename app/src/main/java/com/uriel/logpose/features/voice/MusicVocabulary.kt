package com.uriel.logpose.features.voice

import androidx.collection.LruCache

/**
 * MusicVocabulary V10: Fuente única de verdad con Weighted Levenshtein.
 * Las sibilancias (s, z, x, j) tienen costo reducido (0.3) por el ruido del viento.
 */
object MusicVocabulary {

    private val ARTISTS = listOf(
        "duki", "ysy a", "trueno", "soda stereo", "tiago pzk", "rockstar",
        "metallica", "ac dc", "charly garcia", "fito paez", "el kuelgue",
        "wos", "nicki nicole", "maria becerra", "emilia", "bizarrap",
        "quevedo", "bad bunny", "feid", "karol g", "anuel aa", "myke towers",
        "ozuna", "daddy yankee", "callejeros", "la renga", "los enanitos verdes",
        "patricio rey y sus redonditos de ricota", "sumo", "viejas locas"
    )

    // --- Capa de Corrección Thamis (Thamis Normalizer) ---
    // Mapea lo que Vosk "oye" (columna izquierda) a lo que Spotify entiende (columna derecha)
    private val THAMIS_DICTIONARY = mapOf(
        "duque" to "duki",
        "doce" to "duki",
        "donde" to "duki",
        "y si" to "ysy a",
        "isi" to "ysy a",
        "voz" to "wos",
        "vos" to "wos",
        "biza" to "bizarrap",
        "pisa" to "bizarrap",
        "tiago" to "tiago pzk",
        "estrella de rock" to "rockstar",
        "rock" to "rockstar",
        "conejo malo" to "bad bunny",
        "conga" to "la konga",
        "venga" to "la renga",
        "cuelgue" to "el kuelgue"
    )

    private val PLAYLISTS = listOf(
        "lista de viaje", "viaje", "ruta", "moto", "relax", "fiesta", "concentración", "rock", "trap", "pop"
    )

    private val normalizedCache = LruCache<String, String>(256)

    fun normalize(input: String): String {
        if (input.isBlank()) return ""
        normalizedCache.get(input)?.let { return it }
        
        val cleanInput = input.lowercase()
            .replace(Regex("[áàäâã]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöôõ]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace(Regex("[^a-z0-9ñ ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        
        // SINCRO: Reemplazo inteligente usando límites de palabra para no romper frases
        var result = cleanInput
        for ((hears, targets) in THAMIS_DICTIONARY) {
            val regex = Regex("\\b$hears\\b", RegexOption.IGNORE_CASE)
            if (result.contains(regex)) {
                result = result.replace(regex, targets)
            }
        }
        
        normalizedCache.put(input, result.trim())
        return result.trim()
    }

    fun getGrammarPhases(): List<String> {
        // IMPORTANTE: Solo enviamos a Vosk palabras que SI están en su diccionario.
        // Incluimos ARTISTS que sean palabras "seguras" o alias.
        val safeArtists = listOf("rockstar", "duki", "trueno", "wos", "emilia", "bizarrap")
        return (PLAYLISTS + THAMIS_DICTIONARY.keys + safeArtists).distinct()
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
        for (entity in (ARTISTS + PLAYLISTS)) {
            val entityNorm = normalize(entity)
            if (normalizedQuery == entityNorm) return entity to 1.0
            val wScore = weightedLevenshteinRatio(normalizedQuery, entityNorm)
            if (wScore > bestScore) { bestScore = wScore; bestEntity = entity }
            val qTokens = normalizedQuery.split(" ").filter { it.length >= 2 }
            val eTokens = entityNorm.split(" ").filter { it.length >= 2 }
            for (qt in qTokens) {
                for (et in eTokens) {
                    val tScore = weightedLevenshteinRatio(qt, et)
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

    fun getAllNames(): List<String> = ARTISTS + PLAYLISTS

    fun findExact(candidate: String): String? {
        val normCandidate = normalize(candidate)
        return (ARTISTS + PLAYLISTS).find { normalize(it) == normCandidate }
    }

    fun getAllArtists(): List<String> = ARTISTS

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
