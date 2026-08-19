package com.uriel.logpose.logprobe.bluetooth

import com.uriel.logpose.logprobe.models.BluetoothSnapshot
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.reports.ProbeTimeline

object BluetoothHistory {
    fun all(): List<BluetoothSnapshot> =
        ProbeTimeline.snapshot()
            .filter { it.type == ProbeEventType.BLUETOOTH_STATE }
            .mapNotNull { it.snapshot as? BluetoothSnapshot }
}
