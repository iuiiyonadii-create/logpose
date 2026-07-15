package com.uriel.logpose.features.bluetooth

import com.uriel.logpose.domain.models.DeviceType

data class FavoriteDevice(
    val mac: String,
    val name: String,
    val type: DeviceType = DeviceType.UNKNOWN,
    val priority: Int = 0,
    val autoConnect: Boolean = true,
    val enabled: Boolean = true,
    val lastConnected: Long = 0L
)