package com.uriel.logpose.features.bluetooth

import com.uriel.logpose.domain.models.LogPoseDevice
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
        _state.value = buildState()
    }

    private fun buildState(): BluetoothState {

        val bluetoothEnabled =
            repository.isBluetoothEnabled()

        val devices =
            repository.getPairedDevices()

        val selected =
            repository.getSelectedDevice()

        return BluetoothState(

            bluetoothEnabled = bluetoothEnabled,

            discovering = false,

            activeDevice = selected?.let(::toFavoriteDevice),

            favorites = selected?.let {
                listOf(toFavoriteDevice(it))
            } ?: emptyList(),

            availableDevices =
                devices.map {
                    FavoriteDevice(
                        mac = it.mac,
                        name = it.name,
                        connected = selected?.mac == it.mac
                    )
                },

            lastError = null
        )
    }

    private fun toFavoriteDevice(
        device: LogPoseDevice
    ): FavoriteDevice =
        FavoriteDevice(
            mac = device.mac,
            name = device.name,
            connected = true
        )
}