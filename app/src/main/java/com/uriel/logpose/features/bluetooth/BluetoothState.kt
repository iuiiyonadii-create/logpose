package com.uriel.logpose.features.bluetooth


import com.uriel.logpose.domain.models.LogPoseDevice



data class BluetoothState(


    val bluetoothEnabled: Boolean = false,


    val discovering: Boolean = false,


    val devices: List<LogPoseDevice> = emptyList(),


    val selectedDevice: LogPoseDevice? = null,


    val connected: Boolean = false,


    val error: String? = null


)