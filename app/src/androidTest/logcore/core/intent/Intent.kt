package com.uriel.logpose.logcore.core.intent

/**
 * Representa la intención detectada por el motor de voz.
 * No ejecuta acciones; solamente describe qué quiso hacer el usuario.
 */
enum class Intent {

    // Música
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    REPEAT_TRACK,
    SET_VOLUME,
    MUTE,
    SILENCE,

    // Navegación
    NAVIGATION_STATUS,
    BETTER_ROUTE,
    TRAFFIC_STATUS,

    // Llamadas
    CALL_CONTACT,
    ANSWER_CALL,
    REJECT_CALL,
    END_CALL,
    MUTE_CONTACT,

    // Mensajes
    SEND_MESSAGE,
    READ_MESSAGE,

    // Notificaciones
    READ_NOTIFICATIONS,
    IGNORE_NOTIFICATIONS,

    // Emergencia
    EMERGENCY
}