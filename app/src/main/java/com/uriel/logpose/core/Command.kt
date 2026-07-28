package com.uriel.logpose.core

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 6: COMMAND MODEL
 *
 * Representa los comandos básicos que el sistema puede procesar.
 */
enum class Command {
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    INCREASE_VOLUME,
    DECREASE_VOLUME,
    CALL_CONTACT,
    STOP_LOGPOSE,
    GET_LOCATION,
    READ_NOTIFICATIONS,
    UNKNOWN
}
