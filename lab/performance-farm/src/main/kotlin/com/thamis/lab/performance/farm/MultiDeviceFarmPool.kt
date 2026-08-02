package com.thamis.lab.performance.farm

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

public data class DeviceAllocationReservation(
    public val reservationId: String,
    public val assignedDeviceSerial: String,
    public val timestampMs: Long,
    public val isActive: Boolean
)

/**
 * Multi-Device Farm Pool managing concurrent execution across 1 to 32+ physical devices and emulators.
 */
public class MultiDeviceFarmPool {
    private val TAG = "MultiDeviceFarmPool"
    private val activePool = ConcurrentHashMap<String, DeviceInfo>()
    private val reservations = CopyOnWriteArrayList<DeviceAllocationReservation>()

    public fun registerDevice(device: DeviceInfo) {
        activePool[device.deviceId] = device
        LabLogger.info(TAG, "Registered device in Farm Pool: ${device.deviceId} (${device.displayName})")
    }

    public fun reserveDevice(targetSerial: String? = null): DeviceAllocationReservation? {
        val selected = if (targetSerial != null) {
            activePool[targetSerial]
        } else {
            activePool.values.firstOrNull { it.state == DeviceState.ONLINE }
        }

        if (selected == null) return null

        val reservation = DeviceAllocationReservation(
            reservationId = "res-${System.currentTimeMillis()}",
            assignedDeviceSerial = selected.deviceId,
            timestampMs = System.currentTimeMillis(),
            isActive = true
        )
        reservations.add(reservation)
        LabLogger.info(TAG, "Reserved device ${selected.deviceId} for execution (Reservation: ${reservation.reservationId})")
        return reservation
    }

    public fun getPoolSize(): Int = activePool.size
    public fun getOnlineDevices(): List<DeviceInfo> = activePool.values.filter { it.state == DeviceState.ONLINE }
}
