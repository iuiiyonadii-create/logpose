package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.THAMISContext
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.entity.EntityExtractor
import com.uriel.logpose.thamis.intent.ComplexIntentDetector
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.policy.THAMISPolicy
import com.uriel.logpose.thamis.request.THAMISRequest

/**
 * Núcleo principal de THAMIS (Thinking & Human-Adaptive Mobile Intelligence System).
 */
object THAMIS {

    /**
     * Procesa una solicitud y devuelve una lista de decisiones (soporte multi-paso).
     */
    fun processSequence(request: THAMISRequest): List<Decision> {
        // 1. Detección de secuencia de intenciones
        val detections = ComplexIntentDetector.detectSequence(request.text)
        
        return detections.map { detection ->
            // 2. Cálculo de confianza base
            val combinedConfidence = request.overrideConfidence ?: ((detection.score + request.speechConfidence) / 2f)

            // 3. Resolución contextual
            val finalIntent = if (detection.intent == Intent.UNKNOWN || detection.score < 0.5f) {
                resolveContextualIntent(request.text, THAMISContext.getActiveIntent())
            } else {
                detection.intent
            }

            // 4. Extracción de Entidades
            val entities = EntityExtractor.extract(finalIntent, request.text)

            // 5. Actualización de contexto
            THAMISContext.update(finalIntent, request.text)

            // 6. Creación de decisión
            val preliminaryDecision = Decision(
                intent = finalIntent,
                confidence = combinedConfidence,
                entities = entities,
                requiresConfirmation = combinedConfidence < 0.75f && 
                                       finalIntent != Intent.UNKNOWN &&
                                       !entities.containsKey("prompt")
            )

            // 7. Evaluación de política
            THAMISPolicy.evaluate(preliminaryDecision)
        }
    }

    /**
     * Procesa una solicitud de forma tradicional (una única intención).
     * Mantenido para compatibilidad con VoiceManager v1.0.
     */
    fun process(request: THAMISRequest): Decision {
        return processSequence(request).firstOrNull() ?: Decision(Intent.UNKNOWN, 0f)
    }

    private fun resolveContextualIntent(text: String, activeIntent: Intent): Intent {
        val lowerText = text.lowercase().trim()
        
        // --- APRENDIZAJE NEGATIVO / CORRECCIÓN ---
        // Si el usuario rechaza la acción anterior, la "desaprendemos"
        if (lowerText == "no" || lowerText == "te equivocaste" || lowerText == "error") {
            val lastText = THAMISContext.getLastText()
            if (lastText != null) {
                com.uriel.logpose.thamis.learning.LearningEngine.forget(lastText)
                LogPoseLogger.i("🧠 THAMIS: Olvidando patrón erróneo: $lastText")
            }
        }

        // Manejo de confirmación (Sí / No)
        if (activeIntent != Intent.UNKNOWN) {
            if (lowerText == "si" || lowerText == "sí" || lowerText == "dale" || lowerText == "bueno" || lowerText == "ok") {
                return activeIntent
            }
            if (lowerText == "no" || lowerText == "nones" || lowerText == "cancelar") {
                THAMISContext.clear()
                return Intent.UNKNOWN
            }
        }

        // Si el usuario dice "cancela", "para", "no" y hay algo activo
        if (lowerText.contains("cancela") || lowerText.contains("para") || lowerText.contains("no")) {
            return when (activeIntent) {
                Intent.CALL_CONTACT -> Intent.REJECT_CALL
                Intent.PLAY_MUSIC -> Intent.PAUSE_MUSIC
                else -> Intent.UNKNOWN
            }
        }
        
        return Intent.UNKNOWN
    }
}