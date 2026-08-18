package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.MusicVocabulary
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.thamis.intent.IntentDetector

enum class Criticality { LOW, HIGH }

sealed class FilterResult {
    data class Confirmed(val detection: IntentDetector.DetectionResult) : FilterResult()
    object TooUncertain : FilterResult()
    object NoIntent : FilterResult()
}

/**
 * SurvivalFilter (Muro de Verbos) v2.0: Protege al sistema contra falsos positivos 
 * disparados por el ruido del viento o interferencias en el casco.
 * Refactorizado para usar el nuevo IntentDetector rioplatense.
 */
object SurvivalFilter {
    private const val BASE_THRESHOLD = 0.80f
    private const val MAX_THRESHOLD = 0.94f

    fun evaluate(result: VoskVoiceEngine.RecognizedCommand, ignoreWakeWord: Boolean = false): FilterResult {
        // 1. Detección de intención mediante el motor unificado (Pasamos el flag de sesión)
        val detection = IntentDetector.detect(result.text, ignoreWakeWord)
        
        // v108.0: Resiliencia STAFF - Si el WakeWord fue detectado, permitimos el flujo aunque el intent sea UNKNOWN
        val hasWakeWord = detection.type != "PRIVACY_MUTED"
        
        if (detection.intent == Intent.UNKNOWN && !hasWakeWord) {
            return FilterResult.NoIntent
        }

        // 2. Calculamos el umbral dinámico basado en el ruido ambiente actual
        val noiseLevel = VoskVoiceEngine.getAmbientNoiseLevel()
        val currentThreshold = (BASE_THRESHOLD + (MAX_THRESHOLD - BASE_THRESHOLD) * noiseLevel)

        // SINCRO CLAUDE: Boost Semántico para artistas conocidos
        val mediaPayload = detection.entities["media"] ?: ""
        val semanticBoost = if (mediaPayload.isNotEmpty() && MusicVocabulary.isKnown(mediaPayload)) {
            0.15f 
        } else 0f

        val finalScore = detection.score + semanticBoost

        // 3. Evaluación de confianza contra umbral de ruido
        if (finalScore < currentThreshold) {
            LogPoseLogger.w("SurvivalFilter", "Dudoso (Score: $finalScore < Thres: $currentThreshold) -> '${result.text}'")
            
            // v67.7: Reporte de Fallo Nivel 4 (Seguridad/Filtro)
            val blockTrace = org.json.JSONObject().apply {
                put("type", "FALLO_INCERTIDUMBRE")
                put("final_clean_text", result.text)
                put("detected_intent", detection.intent.name)
                put("failure_level", 4) // Nivel 4: Security/Filter Block
                put("noise_level", noiseLevel)
                put("confidence", result.confidence)
                put("threshold", currentThreshold)
            }
            com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(blockTrace)
            
            return FilterResult.TooUncertain
        }

        // 4. Validación de criticidad Staff
        val criticality = getCriticality(detection.intent)
        if (criticality == Criticality.HIGH && finalScore < 0.90f) {
            LogPoseLogger.w("SurvivalFilter", "Bloqueo por Alta Criticidad (Score: $finalScore < 0.90)")
            return FilterResult.TooUncertain
        }

        return FilterResult.Confirmed(detection)
    }

    private fun getCriticality(intent: Intent): Criticality {
        return when (intent) {
            Intent.CALL_CONTACT, Intent.ANSWER_CALL, Intent.SEND_MESSAGE -> Criticality.HIGH
            else -> Criticality.LOW
        }
    }
}
