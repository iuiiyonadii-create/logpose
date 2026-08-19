package com.uriel.logpose.features.notifications

/**
 * Eventos puntuales que NotificationRepository emite hacia capas
 * superiores (Manager/ViewModel/futuro Voice-THAMIS), independientes del
 * NotificationState continuo. Utiles para efectos de un solo disparo,
 * como pedirle a Voice que anuncie una notificacion de alta prioridad.
 */
sealed class NotificationCoreEvent {

    data class Posted(val item: NotificationItem) : NotificationCoreEvent()

    data class Removed(val id: String) : NotificationCoreEvent()

    data object HistoryCleared : NotificationCoreEvent()
}
