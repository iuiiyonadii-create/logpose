package com.uriel.logpose.audio.perception

/**
 * Resumen de calidad de una sesión de audio completa.
 */
data class AudioSessionReport(
    val durationMs: Long,
    val totalSamples: Int,
    val averageNoiseLevel: Float,
    val overallQuality: AudioQualityAnalyzer.SignalQuality,
    val issuesDetected: List<String>
)
