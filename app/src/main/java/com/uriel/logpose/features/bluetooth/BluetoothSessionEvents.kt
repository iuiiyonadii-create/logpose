package com.uriel.logpose.features.bluetooth

sealed interface BluetoothSessionEvent {

    data object BluetoothEnabled : BluetoothSessionEvent

    data object BluetoothDisabled : BluetoothSessionEvent

    data class DeviceConnected(
        val device: FavoriteDevice
    ) : BluetoothSessionEvent

    data class DeviceDisconnected(
        val device: FavoriteDevice
    ) : BluetoothSessionEvent

    data class DiscoveryStarted(
        val timestamp: Long = System.currentTimeMillis()
    ) : BluetoothSessionEvent

    data class DiscoveryFinished(
        val timestamp: Long = System.currentTimeMillis()
    ) : BluetoothSessionEvent

    data class Error(
        val message: String
    ) : BluetoothSessionEvent
}