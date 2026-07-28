package com.uriel.logpose.features.voice

import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * VoiceMusicController: Único punto de entrada para "reproducir música por voz".
 * Utiliza gramática restringida y lógica de fallback.
 */
class VoiceMusicController(private val context: android.content.Context) {

    private val anchors = setOf("pone", "poneme", "ponme", "ponele", "reproduci", "reproduce", "reproducime")
    private val resumeAliases = setOf("reproducir", "reproduce", "segui", "continua", "dale", "play")

    sealed class Outcome {
        data class Playing(val entity: String) : Outcome()
        object Resumed : Outcome()
        object NotUnderstood : Outcome()
    }

    fun handleTranscript(transcript: String): Outcome {
        val normalized = MusicVocabulary.normalize(transcript)

        // 1. Resume / Play a secas
        if (normalized in resumeAliases) {
            MusicManager.play("") // El manager v5.0 maneja "" como resume
            return Outcome.Resumed
        }

        // 2. Buscar ancla
        val matchedAnchor = anchors.firstOrNull { normalized.startsWith(it) } 
        
        // Si no hay ancla, probamos si el texto es directamente un artista (gracias al grammar lock)
        val searchBody = if (matchedAnchor != null) {
            normalized.removePrefix(matchedAnchor).trim()
        } else {
            normalized
        }

        if (searchBody.isBlank() || searchBody.length < 3) {
            // Si es muy corto y no hay match exacto, probablemente sea ruido o resume
            if (matchedAnchor != null) {
                MusicManager.play("")
                return Outcome.Resumed
            }
            return Outcome.NotUnderstood
        }

        // 3. Match exacto (Camino feliz)
        val exact = MusicVocabulary.findExact(searchBody)
        if (exact != null) {
            LogPoseLogger.i("MusicController: Match exacto -> $exact")
            MusicManager.play(exact)
            return Outcome.Playing(exact)
        }

        // 4. Fallback: Búsqueda difusa (Ya integrada en MusicVocabulary)
        val fuzzy = MusicVocabulary.findBestMatch(searchBody, threshold = 0.70)
        if (fuzzy != null) {
            LogPoseLogger.i("MusicController: Match difuso (${fuzzy.second}) -> ${fuzzy.first}")
            MusicManager.play(fuzzy.first)
            return Outcome.Playing(fuzzy.first)
        }

        return Outcome.NotUnderstood
    }
}
