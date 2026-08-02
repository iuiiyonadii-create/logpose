package com.thamis.lab.performance.analyzer

import com.thamis.lab.performance.telemetry.HardwareTelemetryCollector

public data class PerformanceAnalysisReport(
    public val deviceId: String,
    public val avgCpuPercent: Double,
    public val peakRamMb: Double,
    public val isCpuPass: Boolean,
    public val isRamPass: Boolean
)

/**
 * Performance Analyzer to evaluate real-time hardware telemetry against quality thresholds.
 */
public class PerformanceAnalyzer(
    public val collector: HardwareTelemetryCollector,
    public val maxCpuThresholdPercent: Double = 80.0,
    public val maxRamThresholdMb: Double = 512.0
) {
    public fun analyzeDevice(deviceId: String): PerformanceAnalysisReport {
        val avgCpu = collector.getAverageCpuUsage(deviceId)
        val peakRam = collector.getPeakRamUsageMb(deviceId)

        return PerformanceAnalysisReport(
            deviceId = deviceId,
            avgCpuPercent = avgCpu,
            peakRamMb = peakRam,
            isCpuPass = avgCpu <= maxCpuThresholdPercent,
            isRamPass = peakRam <= maxRamThresholdMb
        )
    }
}
