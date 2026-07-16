package com.uriel.logpose.features.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothSessionManager(

    private val repository: BluetoothRepository

) {

    private val _state = MutableStateFlow(
        BluetoothState()
    )

    val state: StateFlow<BluetoothState> =
        _state.asStateFlow()

    fun refresh() {

        val bluetoothEnabled =
            repository.isBluetoothEnabled()

        val devices =
            repository.getPairedDevices()

        val selected =
            repository.getSelectedDevice()

        _state.value = BluetoothState(

            bluetoothEnabled = bluetoothEnabled,

            discovering = false,

            activeDevice = selected?.let {
                FavoriteDevice(
                    mac = it.mac,
                    name = it.name,
                    connected = true
                )
            },

            favorites = selected?.let {
                listOf(
                    FavoriteDevice(
                        mac = it.mac,
                        name = it.name,
                        connected = true
                    )
                )
            } ?: emptyList(),

            availableDevices =
                devices.map {

                    FavoriteDevice(
                        mac = it.mac,
                        name = it.name,
                        connected =
                            selected?.mac == it.mac
                    )

                },

            lastError = null

        )

    }

}