package com.uriel.logpose.core.parser

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.language.PhoneticEngine

/**
 * VerbWall: Muro de seguridad para comandos críticos.
 * Aplica umbrales de confianza asimétricos según la intención.
 */
object VerbWall {

    private const val OPEN_APP_THRESHOLD = 0.85f // Exigente para evitar abrir apps por error
    private const val MUSIC_THRESHOLD = 0.60f    // Permisivo, Spotify maneja bien la ambigüedad
    private const val NAV_THRESHOLD = 0.70f      // Balanceado para navegación

    sealed class WallResult {
        object Confirmed : WallResult()      // Confianza alta, ejecutar
        object Ambiguous : WallResult()      // Confianza media, pedir confirmación
        object Rejected : WallResult()       // Confianza baja, ignorar
    }

    fun evaluate(intent: Intent, confidence: Float): WallResult {
        val threshold = when (intent) {
            Intent.OPEN_APP -> OPEN_APP_THRESHOLD
            Intent.PLAY_MUSIC -> MUSIC_THRESHOLD
            Intent.NAVIGATE -> NAV_THRESHOLD
            else -> 0.50f
        }

        return when {
            confidence >= threshold -> WallResult.Confirmed
            confidence >= (threshold - 0.15f) -> WallResult.Ambiguous
            else -> WallResult.Rejected
        }
    }
}
