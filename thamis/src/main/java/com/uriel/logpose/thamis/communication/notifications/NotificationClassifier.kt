package com.uriel.logpose.thamis.communication.notifications

/**
 * Clasificador semántico de notificaciones entrantes.
 */
enum class NotificationPriority {
    URGENT, IMPORTANT, NORMAL, SILENT, SPAM, SYSTEM, EMERGENCY
}

data class NotificationContext(
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

object NotificationClassifier {

    fun classify(context: NotificationContext): NotificationPriority {
        val content = "${context.title} ${context.text}".lowercase()

        return when {
            content.contains("emergencia") || content.contains("sos") -> NotificationPriority.EMERGENCY
            content.contains("urgente") || content.contains("ahora mismo") -> NotificationPriority.URGENT
            context.appName.contains("whatsapp") || context.appName.contains("telegram") -> NotificationPriority.IMPORTANT
            content.contains("oferta") || content.contains("descuento") -> NotificationPriority.SPAM
            else -> NotificationPriority.NORMAL
        }
    }
}
