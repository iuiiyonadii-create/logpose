package com.thamis.lab.performance.telemetry

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Real-time Hardware Telemetry Collector for CPU, RAM, GPU, Disk, and Network monitoring.
 */
public class HardwareTelemetryCollector {
    private val telemetryHistory = CopyOnWriteArrayList<HardwareTelemetry>()

    public fun recordTelemetry(telemetry: HardwareTelemetry) {
        telemetryHistory.add(telemetry)
    }

    public fun getHistoryForDevice(deviceId: String): List<HardwareTelemetry> {
        return telemetryHistory.filter { it.deviceId == deviceId }
    }

    public fun getAverageCpuUsage(deviceId: String): Double {
        val history = getHistoryForDevice(deviceId)
        if (history.isEmpty()) return 0.0
        return history.map { it.cpuPercent }.average()
    }

    public fun getPeakRamUsageMb(deviceId: String): Double {
        val history = getHistoryForDevice(deviceId)
        if (history.isEmpty()) return 0.0
        return history.maxOf { it.ramUsedMb }
    }

    public fun clear() {
        telemetryHistory.clear()
    }
}
