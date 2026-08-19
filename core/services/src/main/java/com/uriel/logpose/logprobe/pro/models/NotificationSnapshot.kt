package com.uriel.logpose.logprobe.models

/**
 * Metadatos tecnicos de una notificacion. Deliberadamente NO incluye
 * el texto/contenido de la notificacion: solo estructura (categoria,
 * cantidad de acciones, flags), suficiente para diagnostico de LogPose
 * sin exponer contenido potencialmente sensible.
 */
data class NotificationSnapshot(
    val packageName: String,
    val postedAtMillis: Long,
    val category: String?,
    val isOngoing: Boolean,
    val isClearable: Boolean,
    val actionCount: Int,
    val hasContentIntent: Boolean,
    val removed: Boolean = false
)
