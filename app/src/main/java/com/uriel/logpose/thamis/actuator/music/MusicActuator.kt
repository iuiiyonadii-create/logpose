package com.uriel.logpose.thamis.actuator.music

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.thamis.actuator.CognitiveActionExecutor

/**
 * Puente entre el cerebro cognitivo y el motor de música de Spotify.
 * Implementa la autoridad real delegada por THAMIS.
 */
class MusicActuator : CognitiveActionExecutor {
    override fun execute(decision: ThamisDecision) {
        val query = decision.winningEvaluation?.hypothesis?.entities?.get("media") ?: ""
        if (query.isNotEmpty()) {
            MusicManager.play(query)
        } else {
            // Si no hay query (ej: Pausa, Next), el MusicManager ya tiene métodos directos
            // Pero por ahora ThamisDecision.intent mapea a la acción.
            // Para simplificar esta fase, delegamos a MusicManager según el intent.
            when (decision.intent) {
                com.uriel.logpose.thamis.intent.Intent.PAUSE_MUSIC -> MusicManager.pause()
                com.uriel.logpose.thamis.intent.Intent.NEXT_TRACK -> MusicManager.next()
                com.uriel.logpose.thamis.intent.Intent.PREVIOUS_TRACK -> MusicManager.previous()
                com.uriel.logpose.thamis.intent.Intent.SET_VOLUME -> {
                    val level = decision.winningEvaluation?.hypothesis?.entities?.get("level")?.toIntOrNull() ?: 70
                    com.uriel.logpose.features.music.VolumeController.setVolume(level)
                }
                else -> {}
            }
        }
    }
}
