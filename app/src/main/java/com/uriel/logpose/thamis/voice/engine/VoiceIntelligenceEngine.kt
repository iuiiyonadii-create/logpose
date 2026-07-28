package com.uriel.logpose.thamis.voice.engine

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.voice.confidence.ConfidenceEvaluator
import com.uriel.logpose.thamis.voice.context.VoiceContextManager
import com.uriel.logpose.thamis.voice.intent.IntentRecognizer
import com.uriel.logpose.thamis.voice.language.LanguageUnderstandingEngine
import com.uriel.logpose.thamis.voice.model.*
import com.uriel.logpose.thamis.voice.trace.VoiceAudit
import com.uriel.logpose.thamis.voice.trace.VoiceTrace

/**
 * Motor principal de inteligencia vocal de THAMIS v1.0.
 */
object VoiceIntelligenceEngine {

    fun processVoice(rawText: String, sttConfidence: Float): VoiceRecognitionResult {
        val startTime = System.currentTimeMillis()
        val context = VoiceContextManager.getCurrentContext()
        
        // 1. Comprensión Lingüística
        val canonicalText = LanguageUnderstandingEngine.understand(rawText)
        
        // 2. Reconocimiento de Intención
        val intent = IntentRecognizer.recognize(canonicalText)
        
        // 3. Evaluación de Confianza
        val confidenceLevel = ConfidenceEvaluator.evaluate(intent.confidence * sttConfidence)
        
        // 4. Auditoría
        VoiceAudit.record(VoiceTrace(
            input = rawText,
            canonical = canonicalText,
            intent = intent.action,
            confidence = intent.confidence,
            level = confidenceLevel,
            latencyMs = System.currentTimeMillis() - startTime
        ))

        LogPoseLogger.i("THAMIS_VOICE: Interpreted '$rawText' as ${intent.action} ($confidenceLevel)")

        return VoiceRecognitionResult(
            bestMatch = intent.action,
            confidenceLevel = confidenceLevel,
            suggestion = if (confidenceLevel == ConfidenceLevel.MEDIUM_CONFIDENCE) "¿Quisiste decir ${intent.action}?" else null
        )
    }
}
