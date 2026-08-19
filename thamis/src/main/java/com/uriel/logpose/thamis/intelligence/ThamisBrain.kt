package com.uriel.logpose.thamis.intelligence

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.decision.Decision

/**
 * ThamisBrain: Motor de Inteligencia Híbrido (Rules + Edge-AI).
 * v67.0: Consolidación de Conciencia con ThamisNeuralEngine.
 */
object ThamisBrain {

    /**
     * Procesa una petición con el motor híbrido (Rules -> Edge-AI fallback).
     */
    fun process(request: THAMISRequest): Decision {
        val localDetection = IntentDetector.detect(request.text)

        // 🔒 CORTOCIRCUITO INMEDIATO POR PRIVACIDAD (Sin Wake-Word)
        if (localDetection.type == "PRIVACY_MUTED") {
            LogPoseLogger.d("ThamisBrain: PRIVACY_MUTED - Sin Wake-Word. Cortocircuito.")
            return Decision(intent = Intent.UNKNOWN, confidence = 0.0f, entities = emptyMap())
        }

        // v67.0: Hybrid Intelligence - Fallback al cerebro unificado si las reglas fallan
        if (localDetection.intent == Intent.UNKNOWN) {
            LogPoseLogger.i("ThamisBrain", "Reglas agotadas. Activando Neural Fallback...")
            
            val offlineReasoning = kotlinx.coroutines.runBlocking {
                ThamisNeuralEngine.generateResponse(request.text)
            }
            
            if (offlineReasoning.lowercase().contains("reproducir") || offlineReasoning.lowercase().contains("play")) {
                LogPoseLogger.i("ThamisBrain", "✅ Neural Engine rescató intención: PLAY_MUSIC")
                return Decision(intent = Intent.PLAY_MUSIC, confidence = 0.75f, entities = mapOf("parameter" to request.text))
            }
        }

        LogPoseLogger.d("ThamisBrain: Motor local offline (Source: Rules, Conf: ${localDetection.score})")
        return Decision(
            intent = localDetection.intent,
            confidence = localDetection.score,
            entities = localDetection.entities
        )
    }
}
