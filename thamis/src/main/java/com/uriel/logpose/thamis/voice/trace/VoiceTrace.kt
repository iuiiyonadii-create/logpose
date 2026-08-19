package com.uriel.logpose.thamis.voice.trace

import com.uriel.logpose.thamis.voice.model.ConfidenceLevel

/**
 * Registro forense de una interpretación vocal.
 */
data class VoiceTrace(
    val input: String,
    val canonical: String,
    val intent: String,
    val confidence: Float,
    val level: ConfidenceLevel,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

object VoiceAudit {
    private val logs = mutableListOf<VoiceTrace>()

    fun record(trace: VoiceTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<VoiceTrace> = logs.toList()
}
