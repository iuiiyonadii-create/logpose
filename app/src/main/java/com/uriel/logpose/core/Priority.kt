package com.uriel.logpose.core

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 10: NOTIFICATION PRIORITY
 *
 * Define los niveles de prioridad para eventos y notificaciones.
 */
enum class Priority {
    /**
     * Llamadas entrantes o alertas de seguridad críticas.
     */
    CRITICAL,

    /**
     * Mensajes de contactos importantes o navegación.
     */
    IMPORTANT,

    /**
     * Mensajes estándar, actualizaciones de música, etc.
     */
    NORMAL,

    /**
     * Información de fondo.
     */
    LOW
}
