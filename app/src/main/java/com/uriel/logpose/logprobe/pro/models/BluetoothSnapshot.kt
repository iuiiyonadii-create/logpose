package com.uriel.logpose.logprobe.models

data class BluetoothSnapshot(
    val timestampMillis: Long,
    val isEnabled: Boolean,
    val connectedDeviceNames: List<String>
)
