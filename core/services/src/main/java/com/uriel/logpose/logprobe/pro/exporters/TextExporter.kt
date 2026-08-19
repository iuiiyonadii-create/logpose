package com.uriel.logpose.logprobe.exporters

import com.uriel.logpose.logprobe.models.AccessibilitySnapshot
import com.uriel.logpose.logprobe.models.BluetoothSnapshot
import com.uriel.logpose.logprobe.models.MediaSnapshot
import com.uriel.logpose.logprobe.models.NotificationSnapshot
import com.uriel.logpose.logprobe.models.ProbeReport
import com.uriel.logpose.logprobe.models.WindowSnapshot

object TextExporter {

    fun export(report: ProbeReport): String = buildString {
        appendLine("LogProbe report")
        appendLine("session=${report.sessionId}")
        appendLine("started=${report.startedAtMillis} ended=${report.endedAtMillis}")
        appendLine("allowedPackages=${report.allowedPackages.joinToString()}")
        appendLine("events=${report.eventCount}")
        appendLine("---")
        report.events.forEach { event ->
            append("[${event.timestampMillis}] ${event.type} pkg=${event.sourcePackage} ")
            appendLine(describeSnapshot(event.snapshot))
        }
    }

    private fun describeSnapshot(snapshot: Any): String = when (snapshot) {
        is AccessibilitySnapshot -> "class=${snapshot.className} eventType=${snapshot.eventType}"
        is NotificationSnapshot -> "category=${snapshot.category} ongoing=${snapshot.isOngoing} removed=${snapshot.removed}"
        is MediaSnapshot -> "state=${snapshot.playbackState}"
        is BluetoothSnapshot -> "enabled=${snapshot.isEnabled} devices=${snapshot.connectedDeviceNames}"
        is WindowSnapshot -> "class=${snapshot.className}"
        else -> snapshot.toString()
    }
}
