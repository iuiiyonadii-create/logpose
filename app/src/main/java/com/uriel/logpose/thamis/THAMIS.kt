package com.uriel.logpose.thamis

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.THAMISContext
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.entity.EntityExtractor
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.policy.THAMISPolicy
import com.uriel.logpose.thamis.request.THAMISRequest

/**
 * Núcleo principal de THAMIS (Thinking & Human-Adaptive Mobile Intelligence System).
 */
object THAMIS {

    fun process(request: THAMISRequest): Decision {

        // 1. Detección de intención
        val detection = IntentDetector.detect(request.text)

        // 2. Cálculo de confianza base con soporte para Override
        val combinedConfidence = request.overrideConfidence ?: ((detection.score + request.speechConfidence) / 2f)

        // 3. Resolución contextual (Si la intención es vaga, miramos la memoria)
        val finalIntent = if (detection.intent == com.uriel.logpose.thamis.intent.Intent.UNKNOWN || detection.score < 0.5f) {
            resolveContextualIntent(request.text, THAMISContext.getActiveIntent())
        } else {
            detection.intent
        }

        // 4. Extracción de Entidades (Parámetros)
        val entities = EntityExtractor.extract(finalIntent, request.text)

        // 5. Actualizamos la memoria (Intención + Entidades)
        THAMISContext.update(finalIntent, request.text)

        // 6. Creación de decisión preliminar
        val preliminaryDecision = Decision(
            intent = finalIntent,
            confidence = combinedConfidence,
            entities = entities,
            requiresConfirmation = combinedConfidence < 0.75f && finalIntent != com.uriel.logpose.thamis.intent.Intent.UNKNOWN
        )

        // 7. Aplicación de políticas y constitución
        return THAMISPolicy.evaluate(preliminaryDecision)
    }

    private fun resolveContextualIntent(text: String, activeIntent: com.uriel.logpose.thamis.intent.Intent): com.uriel.logpose.thamis.intent.Intent {
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
        if (activeIntent != com.uriel.logpose.thamis.intent.Intent.UNKNOWN) {
            if (lowerText == "si" || lowerText == "sí" || lowerText == "dale" || lowerText == "bueno" || lowerText == "ok") {
                return activeIntent
            }
            if (lowerText == "no" || lowerText == "nones" || lowerText == "cancelar") {
                THAMISContext.clear()
                return com.uriel.logpose.thamis.intent.Intent.UNKNOWN
            }
        }

        // Si el usuario dice "cancela", "para", "no" y hay algo activo
        if (lowerText.contains("cancela") || lowerText.contains("para") || lowerText.contains("no")) {
            return when (activeIntent) {
                com.uriel.logpose.thamis.intent.Intent.CALL_CONTACT -> com.uriel.logpose.thamis.intent.Intent.REJECT_CALL
                com.uriel.logpose.thamis.intent.Intent.PLAY_MUSIC -> com.uriel.logpose.thamis.intent.Intent.PAUSE_MUSIC
                else -> com.uriel.logpose.thamis.intent.Intent.UNKNOWN
            }
        }
        
        return com.uriel.logpose.thamis.intent.Intent.UNKNOWN
    }
}