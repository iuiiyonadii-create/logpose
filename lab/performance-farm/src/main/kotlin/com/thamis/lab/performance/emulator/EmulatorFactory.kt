package com.thamis.lab.performance.emulator

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState

public data class EmulatorTemplate(
    public val manufacturer: String, // Samsung, Motorola, Pixel, Xiaomi, Redmi, OnePlus
    public val modelName: String,
    public val androidVersionApi: Int,
    public val ramMb: Int,
    public val screenDpi: Int
)

/**
 * Emulator Factory automatically generating and provisioning Android emulator instances from templates.
 */
public class EmulatorFactory {
    private val TAG = "EmulatorFactory"

    public fun createTemplate(manufacturer: String, model: String, api: Int): EmulatorTemplate {
        return EmulatorTemplate(
            manufacturer = manufacturer,
            modelName = model,
            androidVersionApi = api,
            ramMb = 4096,
            screenDpi = 440
        )
    }

    public fun provisionEmulator(template: EmulatorTemplate, instanceId: String): DeviceInfo {
        LabLogger.info(TAG, "Provisioning Emulator instance '$instanceId' for ${template.manufacturer} ${template.modelName} (API ${template.androidVersionApi})...")

        return DeviceInfo(
            deviceId = "emulator-$instanceId",
            modelName = "${template.manufacturer} ${template.modelName}",
            isEmulator = true,
            state = DeviceState.ONLINE
        )
    }
}
