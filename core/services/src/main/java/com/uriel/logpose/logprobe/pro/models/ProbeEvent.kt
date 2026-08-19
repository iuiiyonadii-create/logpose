package com.uriel.logpose.logprobe.models

/**
 * Tipos de evento tecnico que LogProbe puede registrar. Deliberadamente
 * NO incluye variantes de "texto libre de mensajeria": todo lo que entra
 * aca ya paso por ProbeGuard antes de construirse.
 */
enum class ProbeEventType {
    ACCESSIBILITY_EVENT,
    NOTIFICATION_POSTED,
    NOTIFICATION_REMOVED,
    BLUETOOTH_STATE,
    MEDIA_STATE,
    WINDOW_CHANGED
}

/**
 * Envoltorio comun para cualquier evento capturado durante una sesion.
 * El campo `snapshot` es uno de los *Snapshot definidos en este mismo
 * package, ya filtrado por ProbeGuard antes de llegar aca.
 */
data class ProbeEvent(
    val id: String,
    val sessionId: String,
    val type: ProbeEventType,
    val timestampMillis: Long,
    val sourcePackage: String,
    val snapshot: Any
)
