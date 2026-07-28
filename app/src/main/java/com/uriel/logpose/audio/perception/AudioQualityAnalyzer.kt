package com.uriel.logpose.audio.perception

/**
 * Analiza la calidad de la señal de audio en tiempo real.
 */
object AudioQualityAnalyzer {

    enum class SignalQuality { BAD, MEDIUM, GOOD }

    fun calculateQuality(noiseLevel: Float, voiceLevel: Float): SignalQuality {
        val snr = if (noiseLevel > 0) voiceLevel / noiseLevel else voiceLevel * 10
        
        return when {
            snr > 5.0 -> SignalQuality.GOOD
            snr > 2.0 -> SignalQuality.MEDIUM
            else -> SignalQuality.BAD
        }
    }
}
