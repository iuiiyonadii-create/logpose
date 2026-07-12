package com.uriel.logpose.viewmodel

import androidx.lifecycle.ViewModel
import com.uriel.logpose.AppContainer
import com.uriel.logpose.model.LogPoseDevice

class BluetoothViewModel : ViewModel() {

    private val repository = AppContainer.bluetoothRepository

    fun isBluetoothEnabled(): Boolean {
        return repository.isBluetoothEnabled()
    }

    fun getPairedDevices(): List<LogPoseDevice> {
        return repository.getPairedDevices()
    }

    fun getSelectedDevice(): LogPoseDevice? {
        return repository.getSelectedDevice()
    }

    fun saveSelectedDevice(mac: String) {
        repository.saveSelectedDevice(mac)
    }
}