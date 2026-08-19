package com.uriel.logpose.logprobe.bluetooth

object BluetoothDump {
    fun asText(): String {
        val items = BluetoothHistory.all()
        if (items.isEmpty()) return "Sin lecturas de Bluetooth registradas."
        return buildString {
            items.forEach { s ->
                appendLine("[${s.timestampMillis}] enabled=${s.isEnabled} devices=${s.connectedDeviceNames}")
            }
        }
    }
}
