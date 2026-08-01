package com.thamis.lab.performance.adb

import com.thamis.lab.core.common.error.LabError
import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult

/**
 * Real Chaos Engine executing actual ADB system shell commands on connected Android devices.
 */
public class RealChaosEngine {
    private val TAG = "RealChaosEngine"

    public fun executeShell(targetSerial: String, shellCmd: String): LabResult<String> {
        return try {
            val process = ProcessBuilder("adb", "-s", targetSerial, "shell", shellCmd).start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            LabLogger.info(TAG, "Chaos Command 'adb -s $targetSerial shell $shellCmd' Output: ${output.trim()}")
            LabResult.Success(output.trim())
        } catch (e: Exception) {
            LabLogger.error(TAG, "Failed to execute chaos command '$shellCmd' on $targetSerial", e)
            LabResult.Failure(LabError.ExecutionError("Chaos command failed: ${e.message}", cause = e))
        }
    }

    public fun disableBluetooth(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "svc bluetooth disable")
    }

    public fun enableBluetooth(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "svc bluetooth enable")
    }

    public fun disableWifi(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "svc wifi disable")
    }

    public fun enableWifi(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "svc wifi enable")
    }

    public fun enableAirplaneMode(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "settings put global airplane_mode_on 1")
    }

    public fun lockScreen(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "input keyevent 26")
    }

    public fun expandNotifications(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "cmd statusbar expand-notifications")
    }

    public fun triggerMediaPlayPause(targetSerial: String): LabResult<String> {
        return executeShell(targetSerial, "input keyevent 85")
    }
}
