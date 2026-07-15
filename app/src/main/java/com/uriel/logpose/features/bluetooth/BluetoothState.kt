package com.uriel.logpose.features.bluetooth

data class BluetoothSessionState(
    val bluetoothEnabled: Boolean = false,
    val discovering: Boolean = false,
    val activeDevice: FavoriteDevice? = null,
    val favorites: List<FavoriteDevice> = emptyList(),
    val availableDevices: List<FavoriteDevice> = emptyList(),
    val lastError: String? = null
)