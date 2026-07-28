package com.uriel.logpose.domain.models

/**
 * Domain model representing the connection state of a Bluetooth device.
 */
enum class BluetoothState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class BluetoothStatus(
    val state: BluetoothState,
    val deviceName: String? = null,
    val deviceAddress: String? = null,
    val errorMessage: String? = null
)
