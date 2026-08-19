package com.uriel.logpose.logprobe.media

object MediaDump {
    fun asText(): String {
        val items = MediaHistory.all()
        if (items.isEmpty()) return "Sin lecturas de Media registradas."
        return buildString {
            items.forEach { s ->
                appendLine("[${s.timestampMillis}] ${s.packageName} state=${s.playbackState}")
            }
        }
    }
}
