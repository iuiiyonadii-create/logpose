package com.uriel.logpose.logprobe.notification

object NotificationDump {

    fun asText(): String {
        val items = NotificationTimeline.ordered()
        if (items.isEmpty()) return "Sin notificaciones registradas."
        return buildString {
            items.forEach { n ->
                appendLine(
                    "[${n.postedAtMillis}] ${n.packageName} category=${n.category} " +
                        "ongoing=${n.isOngoing} actions=${n.actionCount}"
                )
            }
        }
    }
}
