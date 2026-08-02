package com.thamis.lab.performance.telemetry

/**
 * Immutable hardware telemetry data snapshot.
 */
public data class HardwareTelemetry(
    public val timestampMs: Long,
    public val deviceId: String,
    public val cpuPercent: Double = 0.0,
    public val ramUsedMb: Double = 0.0,
    public val gpuPercent: Double = 0.0,
    public val diskUsagePercent: Double = 0.0,
    public val networkBytesPerSec: Long = 0L
)
