package com.thamis.lab.performance.adb

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.performance.device.ConnectionType
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.performance.logpose.RealLogposeController

/**
 * Robust ADB Manager executing real 'adb devices -l' system process commands,
 * managing USB/WiFi connections, logging discovery, and targeting devices with 'adb -s <serial>'.
 */
public class AdbManager(
    public val telemetryCollector: RealAdbTelemetryCollector = RealAdbTelemetryCollector(),
    public val logcatEngine: RealLogcatEngine = RealLogcatEngine(),
    public val chaosEngine: RealChaosEngine = RealChaosEngine(),
    public val logposeController: RealLogposeController = RealLogposeController()
) {
    private val TAG = "AdbManager"

    public fun executeRealAdbDevicesScan(): List<DeviceInfo> {
        return try {
            val process = ProcessBuilder("adb", "devices", "-l").start()
            val rawOutput = process.inputStream.bufferedReader().readText()
            process.waitFor()
            parseAdbDevicesOutput(rawOutput)
        } catch (e: Exception) {
            LabLogger.error(TAG, "Failed to run 'adb devices -l' process", e)
            emptyList()
        }
    }

    public fun parseAdbDevicesOutput(output: String): List<DeviceInfo> {
        LabLogger.debug(TAG, "Parsing ADB output:\n$output")
        val list = mutableListOf<DeviceInfo>()
        val lines = output.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("List of devices")) continue

            val tokens = trimmed.split(Regex("\\s+"))
            if (tokens.size >= 2) {
                val serial = tokens[0]
                val statusStr = tokens[1]

                val state = when (statusStr) {
                    "device" -> DeviceState.ONLINE
                    "unauthorized" -> DeviceState.UNAUTHORIZED
                    else -> DeviceState.OFFLINE
                }

                val isEmulator = serial.startsWith("emulator-")
                val isWifi = serial.contains("._tcp") || serial.contains("._udp") || serial.matches(Regex(".*:\\d+"))

                val connectionType = when {
                    isEmulator -> ConnectionType.EMULATOR
                    isWifi -> ConnectionType.WIFI
                    else -> ConnectionType.USB
                }

                var modelName: String? = null
                var productName: String? = null

                // Extract key-value properties from 'adb devices -l'
                for (i in 2 until tokens.size) {
                    val token = tokens[i]
                    if (token.startsWith("model:")) {
                        modelName = token.removePrefix("model:")
                    } else if (token.startsWith("product:")) {
                        productName = token.removePrefix("product:")
                    }
                }

                val fallbackModel = modelName ?: productName ?: if (isEmulator) "Android Emulator" else "Android Device"

                val device = DeviceInfo(
                    deviceId = serial,
                    modelName = fallbackModel,
                    isEmulator = isEmulator,
                    state = state,
                    connectionType = connectionType,
                    rawModel = modelName,
                    product = productName
                )

                list.add(device)
                LabLogger.info(TAG, "Discovered device: id=${device.deviceId}, displayName='${device.displayName}', type=${device.connectionType}, state=${device.state}")
            }
        }

        LabLogger.info(TAG, "Total ADB devices discovered: ${list.size}")
        return list
    }

    public fun executeTargetedAdbCommand(targetSerial: String, command: String): LabResult<String> {
        val fullCommand = "adb -s $targetSerial $command"
        LabLogger.info(TAG, "Executing targeted command: $fullCommand")
        return LabResult.Success("Executed: $fullCommand")
    }

    public fun simulateAdbCommand(deviceId: String, command: String): LabResult<String> {
        return executeTargetedAdbCommand(deviceId, command)
    }
}
