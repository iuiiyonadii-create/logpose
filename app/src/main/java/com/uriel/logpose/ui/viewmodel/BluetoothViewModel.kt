package com.uriel.logpose.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.domain.models.LogPoseDevice

class BluetoothViewModel : ViewModel() {

    private val repository = AppContainer.bluetoothRepository

    var uiState by mutableStateOf(BluetoothUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {

        uiState = uiState.copy(
            bluetoothEnabled = repository.isBluetoothEnabled(),
            devices = repository.getPairedDevices(),
            selectedDevice = repository.getSelectedDevice(),
            discovering = false,
            loading = false,
            error = null
        )

    }

    fun startDiscovery() {

        val devices =
            repository.getPairedDevices().toMutableList()

        uiState = uiState.copy(
            discovering = true,
            devices = devices
        )

        repository.startDiscovery(

            onDeviceFound = { device ->

                if (devices.none { it.mac == device.mac }) {

                    devices.add(device)

                    uiState = uiState.copy(
                        devices = devices.sortedBy { it.name }
                    )

                }

            },

            onFinished = {

                uiState = uiState.copy(
                    discovering = false
                )

            },

            onDeviceConnected = { connected ->

                val updated = devices.map {

                    if (it.mac == connected.mac)
                        it.copy(connected = true)
                    else
                        it.copy(connected = false)

                }

                uiState = uiState.copy(
                    devices = updated,
                    selectedDevice = updated.find {
                        it.mac == connected.mac
                    }
                )

            },

            onDeviceDisconnected = { disconnected ->

                val updated = devices.map {

                    if (it.mac == disconnected.mac)
                        it.copy(connected = false)
                    else
                        it

                }

                uiState = uiState.copy(
                    devices = updated
                )

            }

        )

    }

    fun stopDiscovery() {

        repository.stopDiscovery()

        uiState = uiState.copy(
            discovering = false
        )

    }

    fun selectDevice(device: LogPoseDevice) {

        uiState = uiState.copy(
            selectedDevice = device
        )

    }

    fun saveSelectedDevice() {

        uiState.selectedDevice?.let {

            repository.saveSelectedDevice(it.mac)

        }

    }

    override fun onCleared() {

        repository.stopDiscovery()

        super.onCleared()

    }

}