package com.uriel.logpose.logcore.core.intent.capability

/**
 * Represents an action that LogCore is capable of performing.
 *
 * A capability describes WHAT the system can do,
 * independent of HOW it is implemented.
 */
sealed interface Capability {

    // Música
    data object PlayMusic : Capability
    data object PauseMusic : Capability
    data object NextTrack : Capability
    data object PreviousTrack : Capability
    data object RepeatTrack : Capability
    data object SetVolume : Capability
    data object Mute : Capability
    data object Silence : Capability

    // Navegación
    data object NavigationStatus : Capability
    data object BetterRoute : Capability
    data object TrafficStatus : Capability

    // Llamadas
    data object CallContact : Capability
    data object AnswerCall : Capability
    data object RejectCall : Capability
    data object EndCall : Capability
    data object MuteContact : Capability

    // Mensajes
    data object SendMessage : Capability
    data object ReadMessage : Capability

    // Notificaciones
    data object ReadNotifications : Capability
    data object IgnoreNotifications : Capability

    // Emergencia
    data object Emergency : Capability
}