package com.uriel.logpose.domain.models

/**
 * Contenido completo de una notificacion del sistema, tal como lo necesita
 * Notification Core (mostrarla al usuario, categorizarla, priorizarla y, a
 * futuro, entregarsela a Voice/THAMIS).
 *
 * Deliberadamente distinto de logprobe.models.NotificationSnapshot: ese tipo
 * es metadata-only por diseno de privacidad de LogProbe (ver
 * NotificationParser.parse). Este tipo SI incluye el texto visible de la
 * notificacion porque Notification Core es una feature de producto, no una
 * herramienta de diagnostico (ver Decision 6 / Decision 8 del Sprint
 * Notification Core, engineering/00_Project).
 *
 * Vive en domain.models (no en features.notifications) para que tanto
 * logprobe.notification.NotificationParser (infraestructura) como
 * features.notifications (producto) puedan depender de el sin invertir la
 * direccion de dependencias del proyecto.
 */
data class NotificationContent(
    val key: String,
    val packageName: String,
    val postedAtMillis: Long,
    val title: String?,
    val text: String?,
    val subText: String?,
    val bigText: String?,
    val systemCategory: String?,
    val isOngoing: Boolean,
    val isClearable: Boolean,
    val actionCount: Int,
    val hasContentIntent: Boolean,
    val removed: Boolean = false
) {
    /** Mejor texto disponible para mostrar en una vista previa corta. */
    val previewText: String?
        get() = text ?: bigText ?: subText
}
