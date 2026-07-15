package com.uriel.logpose.features.bluetooth

class BluetoothSessionManager(
    private val repository: BluetoothRepository
) {

    private var state = BluetoothSessionState()

    fun getState(): BluetoothSessionState = state

    fun refresh() {

        val devices = repository
            .getPairedDevices()
            .map {
                FavoriteDevice(
                    mac = it.mac,
                    name = it.name,
                    type = it.type,
                    priority = 0,
                    autoConnect = true,
                    enabled = true
                )
            }

        state = state.copy(
            bluetoothEnabled = repository.isBluetoothEnabled(),
            availableDevices = devices
        )
    }

    fun setActiveDevice(device: FavoriteDevice) {

        repository.saveSelectedDevice(device.mac)

        state = state.copy(
            activeDevice = device
        )
    }

    fun clearActiveDevice() {

        state = state.copy(
            activeDevice = null
        )
    }

    fun favoriteDevices(): List<FavoriteDevice> =
        state.availableDevices
            .filter { it.enabled }
            .sortedByDescending { it.priority }

    fun isConnected(): Boolean =
        state.activeDevice != null
}