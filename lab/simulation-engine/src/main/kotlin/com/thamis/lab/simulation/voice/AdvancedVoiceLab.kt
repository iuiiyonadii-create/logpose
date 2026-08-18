package com.thamis.lab.simulation.voice

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.phonetic.PhoneticUtils

public data class VoiceProfile(
    public val locale: String, // es-AR, es-MX, es-ES, en-US
    public val gender: String, // MALE, FEMALE
    public val speechStyle: String, // FAST, SLOW, WHISPER, SHOUT
    public val acousticNoiseProfile: String // MOTORCYCLE_HELMET, WIND, TRAFFIC, RAIN
)

public data class VoiceRecognitionMetrics(
    public val engineName: String,
    public val recognitionAccuracyPercent: Double,
    public val latencyMs: Long,
    public val confidenceScore: Double,
    public val denoisingGainPercent: Double = 0.0 // v68.0: Mejora por supresión de ruido
)

/**
 * Advanced Voice Recognition Testing Platform evaluating phonetics, accents, and noise profiles for LogPose.
 */
public class AdvancedVoiceLab {
    private val TAG = "AdvancedVoiceLab"

    public fun testVoiceProfile(
        profile: VoiceProfile, 
        commandText: String,
        enableNeuralDenoising: Boolean = false
    ): VoiceRecognitionMetrics {
        LabLogger.info(TAG, "Testing voice profile '${profile.locale}' (${profile.gender}, ${profile.speechStyle}, ${profile.acousticNoiseProfile}) for '$commandText' (Denoising: $enableNeuralDenoising)...")

        // v60.0: Realistic DNA Matching Simulation
        val isExtremeNoise = profile.acousticNoiseProfile == "WIND" || profile.acousticNoiseProfile == "RAIN"
        val phoneticADN = PhoneticUtils.normalizeToDNA(commandText, extremeNoise = isExtremeNoise)
        
        LabLogger.debug(TAG, "Simulated Phonetic ADN: $phoneticADN")

        // Simulated realistic degradation based on noise profile
        val baseAccuracy = 0.99
        var noisePenalty = when (profile.acousticNoiseProfile) {
            "WIND" -> 0.15
            "TRAFFIC" -> 0.05
            "RAIN" -> 0.10
            "MOTORCYCLE_HELMET" -> 0.02
            else -> 0.0
        }
        
        // v68.0: Impacto real del Neural Denoising (Simulación Staff)
        val denoisingGain = if (enableNeuralDenoising && noisePenalty > 0.0) {
            val gain = noisePenalty * 0.4 // Reduce el ruido en un 40%
            noisePenalty -= gain
            gain
        } else 0.0

        val stylePenalty = if (profile.speechStyle == "FAST") 0.05 else 0.0
        
        val finalAccuracy = (baseAccuracy - noisePenalty - stylePenalty).coerceAtLeast(0.60)
        val simulatedLatency = 80L + (noisePenalty * 1000).toLong()

        return VoiceRecognitionMetrics(
            engineName = "Sherpa-ONNX (v68.0 Denoised)",
            recognitionAccuracyPercent = finalAccuracy * 100.0,
            latencyMs = simulatedLatency,
            confidenceScore = finalAccuracy,
            denoisingGainPercent = denoisingGain * 100.0
        )
    }
}
