package com.thamis.lab.performance.telemetry

import java.util.concurrent.CopyOnWriteArrayList

public data class AppCrashLog(
    public val timestampMs: Long,
    public val deviceId: String,
    public val packageName: String,
    public val exceptionType: String,
    public val stackTrace: String,
    public val isAnr: Boolean = false
)

/**
 * Monitors and captures Logcat exceptions, crashes, and ANR events from devices.
 */
public class AppCrashLogMonitor {
    private val crashLogs = CopyOnWriteArrayList<AppCrashLog>()

    public fun recordCrash(log: AppCrashLog) {
        crashLogs.add(log)
    }

    public fun getCrashesForDevice(deviceId: String): List<AppCrashLog> {
        return crashLogs.filter { it.deviceId == deviceId }
    }

    public fun hasAnrOrCrash(deviceId: String): Boolean {
        return crashLogs.any { it.deviceId == deviceId }
    }

    public fun clear() {
        crashLogs.clear()
    }
}
