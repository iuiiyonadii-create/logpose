package com.uriel.logpose.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.domain.models.LogPoseDevice
import kotlinx.coroutines.launch


class BluetoothViewModel : ViewModel() {


    private val repository =
        AppContainer.bluetoothRepository


    var uiState by mutableStateOf(
        BluetoothUiState()
    )
        private set


    init {

        refresh()

    }


    fun refresh() {

        viewModelScope.launch {


            uiState = uiState.copy(
                loading = true
            )


            try {


                val devices =
                    repository.getPairedDevices()


                uiState =
                    uiState.copy(
                        devices = devices,
                        bluetoothEnabled = true,
                        loading = false
                    )


            } catch (e: Exception) {


                uiState =
                    uiState.copy(
                        loading = false,
                        error = e.message
                    )

            }


        }

    }


    fun startDiscovery() {

        refresh()

    }


    fun selectDevice(
        device: LogPoseDevice
    ) {

        uiState =
            uiState.copy(
                selectedDevice = device
            )

    }


    fun saveSelectedDevice() {

        uiState.selectedDevice?.let { device ->

            repository.saveSelectedDevice(
                device.mac
            )

        }

    }

}