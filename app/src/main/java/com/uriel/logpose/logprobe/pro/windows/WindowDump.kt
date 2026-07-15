package com.uriel.logpose.logprobe.windows

object WindowDump {

    fun asText(): String {
        val events = WindowProbe.currentWindowEvents()
        if (events.isEmpty()) return "Sin cambios de ventana registrados."
        return buildString {
            events.forEach { snap ->
                appendLine("[${snap.timestampMillis}] ${snap.packageName} / ${snap.className}")
            }
        }
    }
}
