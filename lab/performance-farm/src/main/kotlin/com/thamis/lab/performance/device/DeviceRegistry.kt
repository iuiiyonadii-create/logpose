package com.thamis.lab.performance.device

import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe registry for connected physical devices and emulators in the farm.
 */
public class DeviceRegistry {
    private val devices = ConcurrentHashMap<String, DeviceInfo>()

    public fun registerDevice(device: DeviceInfo) {
        devices[device.deviceId] = device
    }

    public fun unregisterDevice(deviceId: String) {
        devices.remove(deviceId)
    }

    public fun getDevice(deviceId: String): DeviceInfo? {
        return devices[deviceId]
    }

    public fun getAvailableDevices(): List<DeviceInfo> {
        return devices.values.filter { it.state == DeviceState.ONLINE }
    }

    public fun getAllDevices(): List<DeviceInfo> {
        return devices.values.toList()
    }

    public fun clear() {
        devices.clear()
    }
}
