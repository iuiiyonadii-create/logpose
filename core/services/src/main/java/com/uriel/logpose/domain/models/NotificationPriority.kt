package com.uriel.logpose.domain.models

/**
 * Prioridad de producto asignada por Notification Core. Ordenada de mayor
 * a menor urgencia via [ordinal]; SILENT nunca debe interrumpir al usuario
 * (p.ej. no debe activar Voice mientras el modo conduccion esta activo).
 */
enum class NotificationPriority {
    CRITICAL,
    HIGH,
    NORMAL,
    LOW,
    SILENT;

    companion object {

        private val CRITICAL_CATEGORIES = setOf(NotificationCategory.CALL, NotificationCategory.ALARM)
        private val LOW_CATEGORIES = setOf(NotificationCategory.MEDIA, NotificationCategory.SYSTEM)

        /**
         * Deriva una prioridad por defecto a partir de la categoria y del
         * estado ongoing/clearable de la notificacion. El resultado es solo
         * un punto de partida: NotificationFilter puede sobrescribirlo con
         * reglas de usuario (lista blanca de alta prioridad, etc.).
         */
        fun fromContent(
            category: NotificationCategory,
            isOngoing: Boolean,
            hasContentIntent: Boolean
        ): NotificationPriority = when {
            category in CRITICAL_CATEGORIES -> CRITICAL
            category == NotificationCategory.MESSAGE -> HIGH
            isOngoing && !hasContentIntent -> LOW
            category in LOW_CATEGORIES -> LOW
            else -> NORMAL
        }
    }
}
