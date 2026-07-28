package com.uriel.logpose.core.bluetooth

/**
 * Domain-level states for Bluetooth connectivity.
 */
enum class BluetoothState {
    IDLE,
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    FAILED
}
