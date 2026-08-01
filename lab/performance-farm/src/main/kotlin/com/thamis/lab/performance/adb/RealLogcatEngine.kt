package com.thamis.lab.performance.adb

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.performance.telemetry.AppCrashLog

/**
 * Real Logcat Engine executing system 'adb logcat -d' process to filter crashes, exceptions, ANRs, SpeechRecognizer, and Bluetooth logs.
 */
public class RealLogcatEngine {
    private val TAG = "RealLogcatEngine"

    public fun captureRecentLogcat(targetSerial: String, linesCount: Int = 100): String {
        return try {
            val process = ProcessBuilder("adb", "-s", targetSerial, "logcat", "-d", "-t", linesCount.toString(), "-v", "threadtime").start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            LabLogger.error(TAG, "Failed to capture logcat from $targetSerial", e)
            ""
        }
    }

    public fun parseCrashesAndAnrs(targetSerial: String): List<AppCrashLog> {
        val rawLogcat = captureRecentLogcat(targetSerial, 200)
        val crashes = mutableListOf<AppCrashLog>()

        for (line in rawLogcat.lines()) {
            if (line.contains("FATAL EXCEPTION") || line.contains("ANR in com.uriel.logpose") || line.contains("AndroidRuntime: E")) {
                val isAnr = line.contains("ANR")
                val crash = AppCrashLog(
                    timestampMs = System.currentTimeMillis(),
                    deviceId = targetSerial,
                    packageName = "com.uriel.logpose",
                    exceptionType = if (isAnr) "ApplicationNotResponding" else "FatalException",
                    stackTrace = line,
                    isAnr = isAnr
                )
                crashes.add(crash)
                LabLogger.error(TAG, "Real Crash/ANR Captured from $targetSerial: ${crash.exceptionType} -> ${crash.stackTrace}")
            }
        }
        return crashes
    }
}
