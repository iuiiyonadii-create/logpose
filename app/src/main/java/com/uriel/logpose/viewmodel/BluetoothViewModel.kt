package com.uriel.logpose.viewmodel

import androidx.lifecycle.ViewModel
import com.uriel.logpose.AppContainer
import com.uriel.logpose.model.LogPoseDevice

class BluetoothViewModel : ViewModel() {

    private val repository = AppContainer.bluetoothRepository

    var uiState = BluetoothUiState()
        private set

    init {
        refresh()
    }

    fun refresh() {

        val devices = repository.getPairedDevices()
        val selectedDevice = repository.getSelectedDevice()

        uiState = uiState.copy(
            bluetoothEnabled = repository.isBluetoothEnabled(),
            devices = devices,
            selectedDevice = selectedDevice,
            loading = false,
            error = null
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

}