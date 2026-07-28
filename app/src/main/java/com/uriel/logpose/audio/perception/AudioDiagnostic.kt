package com.uriel.logpose.audio.perception

/**
 * Representa el estado técnico y de calidad del sistema de captura de audio.
 */
data class AudioDiagnostic(
    val inputSource: String,
    val sampleRate: Int,
    val channelCount: Int,
    val encoding: String,
    val isBluetooth: Boolean,
    val isScoActive: Boolean,
    val noiseLevel: Float, // 0.0 a 1.0
    val voiceLevel: Float, // 0.0 a 1.0
    val clippingDetected: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
