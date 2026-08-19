package com.uriel.logpose.thamis.hardware.audio

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.hardware.model.AudioPerformanceReport

/**
 * Analiza los retrasos en la ruta de audio desde el comando hasta la reproducción.
 */
object AudioLatencyAnalyzer {
    private val latencyHistory = mutableListOf<Long>()

    fun recordLatency(ms: Long) {
        latencyHistory.add(ms)
        if (latencyHistory.size > 50) latencyHistory.removeAt(0)
        LogPoseLogger.d("THAMIS_AUDIO: Latencia de audio detectada: ${ms}ms")
    }

    fun generateReport(): AudioPerformanceReport {
        val avg = if (latencyHistory.isNotEmpty()) latencyHistory.average().toLong() else 0L
        return AudioPerformanceReport(
            latencyMs = avg,
            qualityScore = if (avg > 500) 0.4f else 0.9f,
            packetLossRate = 0.05f, // Placeholder
            detectedIssues = if (avg > 400) listOf("Handshake SCO lento") else emptyList()
        )
    }
}
