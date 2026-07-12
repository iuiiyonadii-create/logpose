package com.uriel.logpose.viewmodel

import com.uriel.logpose.model.LogPoseDevice

data class BluetoothUiState(

    val bluetoothEnabled: Boolean = false,

    val devices: List<LogPoseDevice> = emptyList(),

    val selectedDevice: LogPoseDevice? = null,

    val serviceRunning: Boolean = false,

    val loading: Boolean = false,

    val error: String? = null

)