package com.uriel.logpose.core.compat.core

sealed class Command {

    object StartListening : Command()

    object StopListening : Command()

    object PauseMusic : Command()
    object NextTrack : Command()
    object PreviousTrack : Command()
    object GetWeather : Command()
    object ReadNotifications : Command()
    data class Call(val contact: String) : Command()
    data class SendMessage(val contact: String, val message: String = "") : Command()

    object VolumeUp : Command()

    object VolumeDown : Command()

    object PrivacyModeOn : Command()
    object PrivacyModeOff : Command()
    object GetStatus : Command()

    data class Navigate(
        val destination: String
    ) : Command()

    object StopNavigation : Command()
    object EndTrip : Command()

    object WhereAmI : Command()

    data class PlayMusic(
        val query: String
    ) : Command()

    data class VolumeAbsolute(val level: Int) : Command()

    data class Search(val platform: Platform, val query: String) : Command()
    enum class Platform { NETFLIX, SPOTIFY, YOUTUBE, DISCORD, INSTAGRAM, WHATSAPP }

    data class OpenApp(val appName: String) : Command()
    data class CloseApp(val appName: String) : Command()
    // Borramos Call duplicado aquí

    // Sector 4 - Llamadas
    object AcceptCall : Command()
    object RejectCall : Command()
    object EndCall : Command()
    // Borramos ReadNotifications y SendMessage duplicados aquí
    data class Feedback(val text: String) : Command()

    data class PCAction(
        val action: String
    ) : Command()

    object SwitchTab : Command()
    object RepeatMusic : Command()

    object Multi : Command()

    object Help : Command()

    object Unknown : Command()

    object Ignore : Command()

    // Handover
    object YieldControl : Command()
}
