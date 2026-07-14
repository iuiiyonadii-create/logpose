package com.uriel.logpose.logcore.core.intent

import com.uriel.logpose.logcore.core.intent.capability.Capability

/**
 * Convierte una Intent en la Capability correspondiente.
 */
object IntentMapper {

    fun toCapability(intent: Intent): Capability {

        return when (intent) {

            // Música
            Intent.PLAY_MUSIC -> Capability.PlayMusic
            Intent.PAUSE_MUSIC -> Capability.PauseMusic
            Intent.NEXT_TRACK -> Capability.NextTrack
            Intent.PREVIOUS_TRACK -> Capability.PreviousTrack
            Intent.REPEAT_TRACK -> Capability.RepeatTrack
            Intent.SET_VOLUME -> Capability.SetVolume
            Intent.MUTE -> Capability.Mute
            Intent.SILENCE -> Capability.Silence

            // Navegación
            Intent.NAVIGATION_STATUS -> Capability.NavigationStatus
            Intent.BETTER_ROUTE -> Capability.BetterRoute
            Intent.TRAFFIC_STATUS -> Capability.TrafficStatus

            // Llamadas
            Intent.CALL_CONTACT -> Capability.CallContact
            Intent.ANSWER_CALL -> Capability.AnswerCall
            Intent.REJECT_CALL -> Capability.RejectCall
            Intent.END_CALL -> Capability.EndCall
            Intent.MUTE_CONTACT -> Capability.MuteContact

            // Mensajes
            Intent.SEND_MESSAGE -> Capability.SendMessage
            Intent.READ_MESSAGE -> Capability.ReadMessage

            // Notificaciones
            Intent.READ_NOTIFICATIONS -> Capability.ReadNotifications
            Intent.IGNORE_NOTIFICATIONS -> Capability.IgnoreNotifications

            // Emergencia
            Intent.EMERGENCY -> Capability.Emergency
        }
    }
}