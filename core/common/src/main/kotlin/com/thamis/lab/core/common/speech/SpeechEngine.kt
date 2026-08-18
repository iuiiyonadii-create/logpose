package com.thamis.lab.core.common.speech

import com.thamis.lab.core.common.result.LabResult

/**
 * Common interface for all STT engines in LogPose.
 * v70.0: Standardization for consensus systems.
 */
public interface SpeechEngine {
    public val engineName: String
    
    public suspend fun transcribe(pcmData: ShortArray): LabResult<SpeechResult>
}

public data class SpeechResult(
    public val text: String,
    public val confidence: Float,
    public val latencyMs: Long,
    public val engineName: String,
    public val metadata: Map<String, Any> = emptyMap()
)

public data class AudioQualityReport(
    public val snr: Double,
    public val noiseLevel: Float,
    public val isSpeechDetected: Boolean,
    public val durationMs: Long
)
