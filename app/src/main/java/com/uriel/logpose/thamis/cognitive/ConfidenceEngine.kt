package com.uriel.logpose.thamis.cognitive

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.common.speech.SpeechResult
import com.thamis.lab.core.common.speech.AudioQualityReport
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.language.RioplatenseLinguisticNormalizer

/**
 * ConfidenceEngine: The final judge of acoustic integrity.
 * v72.0: Adaptive Dynamic Threshold Consensus Engine with Continuous SNR Penalty.
 */
object ConfidenceEngine {

    private const val BASE_CONSENSUS_THRESHOLD = 0.65f
    private const val HIGH_NOISE_THRESHOLD = 0.82f
    private const val GLOBAL_CONFIDENCE_THRESHOLD = 0.85f
    private const val MIN_CONSENSUS_MATCH = 2
    private const val OPTIMAL_SNR_DB = 15.0f
    private const val CRITICAL_SNR_DB = 4.0f

    data class ConfidenceReport(
        val isCommandValid: Boolean,
        val consensusIntent: Intent,
        val consensusEntities: Map<String, String>,
        val finalConfidence: Float,
        val rationale: String
    )

    /**
     * Evaluates results from multiple engines to reach a semantic consensus.
     */
    fun evaluateConsensus(
        results: List<SpeechResult>,
        audioQuality: AudioQualityReport
    ): ConfidenceReport {
        if (results.isEmpty()) return ConfidenceReport(false, Intent.UNKNOWN, emptyMap(), 0f, "No data.")

        // v76.0 STAFF: MODO MICRO LIBRE EXTREMO - Cero burocracia
        val semanticResults = results.filter { it.text.isNotBlank() }.map { 
            val detection = IntentDetector.detect(it.text, ignoreWakeWord = true)
            SemanticResult(detection.intent, detection.entities, it.confidence, it.engineName)
        }

        val winner = semanticResults.maxByOrNull { it.confidence }
        
        // Si hay un ganador con intención clara, se ejecuta. 
        // Bajamos el umbral a 0.4 para que incluso con ruido de viento (moto) actúe.
        val isValid = winner != null && winner.intent != Intent.UNKNOWN && winner.confidence >= 0.4f

        return ConfidenceReport(
            isCommandValid = isValid,
            consensusIntent = winner?.intent ?: Intent.UNKNOWN,
            consensusEntities = winner?.entities ?: emptyMap(),
            finalConfidence = winner?.confidence ?: 0f,
            rationale = "Micro Libre v76: Confianza directa en ${winner?.engineName ?: "None"}"
        )
    }

    /**
     * Computes dynamic consensus threshold as an inverse linear curve of SNR.
     * Clean audio (SNR >= 15 dB) -> Threshold = 0.65
     * Noisy audio (SNR <= 4 dB)  -> Threshold = 0.82
     */
    fun computeAdaptiveThreshold(snrDb: Float): Float {
        return when {
            snrDb >= OPTIMAL_SNR_DB -> BASE_CONSENSUS_THRESHOLD
            snrDb <= CRITICAL_SNR_DB -> HIGH_NOISE_THRESHOLD
            else -> {
                val factor = (OPTIMAL_SNR_DB - snrDb) / (OPTIMAL_SNR_DB - CRITICAL_SNR_DB)
                BASE_CONSENSUS_THRESHOLD + (factor * (HIGH_NOISE_THRESHOLD - BASE_CONSENSUS_THRESHOLD))
            }
        }
    }

    private data class SemanticResult(
        val intent: Intent,
        val entities: Map<String, String>,
        val confidence: Float,
        val engineName: String
    )

    /**
     * Validates a decision before allowing dispatcher execution.
     */
    fun isSafeToExecute(decision: Decision, report: ConfidenceReport): Boolean {
        if (decision.intent == Intent.UNKNOWN) return false
        
        // v71.0: Zero tolerance for mismatch between Decision and Consensus
        if (decision.intent != report.consensusIntent && report.isCommandValid) {
            return false 
        }

        return report.isCommandValid && report.finalConfidence >= 0.65f
    }
}
