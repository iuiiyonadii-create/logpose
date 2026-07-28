package com.uriel.logpose.domain.models

import com.uriel.logpose.domain.models.DeviceType

/**
 * Domain representation of a Bluetooth device.
 */
data class LogPoseDevice(
    val name: String,
    val mac: String,
    val type: DeviceType = DeviceType.UNKNOWN,
    val connected: Boolean = false
)
