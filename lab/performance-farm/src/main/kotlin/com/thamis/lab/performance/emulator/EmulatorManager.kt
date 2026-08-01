package com.thamis.lab.performance.emulator

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState

/**
 * Manages Android Emulator lifecycle (list AVDs, launch, shutdown).
 */
public class EmulatorManager {
    private val activeEmulators = mutableListOf<DeviceInfo>()

    public fun listAvailableAvds(): List<String> {
        return listOf("Pixel_7_API_34", "Medium_Phone_API_33", "Automotive_1080p_API_32")
    }

    public fun launchEmulator(avdName: String): LabResult<DeviceInfo> {
        val emuId = "emulator-${5554 + activeEmulators.size * 2}"
        val device = DeviceInfo(deviceId = emuId, modelName = avdName, isEmulator = true, state = DeviceState.ONLINE)
        activeEmulators.add(device)
        return LabResult.Success(device)
    }

    public fun shutdownEmulator(emulatorId: String): LabResult<Boolean> {
        activeEmulators.removeIf { it.deviceId == emulatorId }
        return LabResult.Success(true)
    }
}
