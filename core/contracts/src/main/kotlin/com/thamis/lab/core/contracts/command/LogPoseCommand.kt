package com.thamis.lab.core.contracts.command

/**
 * LogPoseCommand: Unified action contract for rider interactions.
 */
public sealed interface LogPoseCommand {
    public object StartListening : LogPoseCommand
    public object StopListening : LogPoseCommand
    public object PauseMusic : LogPoseCommand
    public object NextTrack : LogPoseCommand
    public object PreviousTrack : LogPoseCommand
    public object VolumeUp : LogPoseCommand
    public object VolumeDown : LogPoseCommand
    public object Mute : LogPoseCommand
    public object YieldControl : LogPoseCommand
    public object StopNavigation : LogPoseCommand
    public object ReadNotifications : LogPoseCommand
    public object AcceptCall : LogPoseCommand
    public object RejectCall : LogPoseCommand
    public object ConfirmAction : LogPoseCommand
    public object CancelAction : LogPoseCommand
    public object RecordIncident : LogPoseCommand
    public object GetVehicleStatus : LogPoseCommand
    public object GetFuelLevel : LogPoseCommand
    public object GetMaintenanceInfo : LogPoseCommand
    public object GetEngineTemp : LogPoseCommand
    public object GetWeather : LogPoseCommand
    public object SwitchTab : LogPoseCommand
    public object RepeatMusic : LogPoseCommand
    public object EndTrip : LogPoseCommand
    public object EndCall : LogPoseCommand
    public object WhereAmI : LogPoseCommand
    public object Multi : LogPoseCommand
    public object Help : LogPoseCommand
    public object Ignore : LogPoseCommand
    public object Unknown : LogPoseCommand

    public data class PlayMusic(val query: String) : LogPoseCommand
    public data class Navigate(val destination: String) : LogPoseCommand
    public data class SendMessage(val contact: String, val message: String) : LogPoseCommand
    public data class MessageContent(val content: String) : LogPoseCommand
    public data class Call(val contact: String) : LogPoseCommand
    public data class OpenApp(val appName: String) : LogPoseCommand
    public data class CloseApp(val appName: String) : LogPoseCommand
    public data class VolumeAbsolute(val level: Int) : LogPoseCommand
    public data class Search(val platform: String, val query: String) : LogPoseCommand
    public data class SafetyAlert(val alertType: String) : LogPoseCommand
    public data class TrafficStatus(val location: String) : LogPoseCommand
    public data class RestaurantSearch(val query: String) : LogPoseCommand
    public data class TransportInfo(val type: String) : LogPoseCommand
    public data class Feedback(val text: String) : LogPoseCommand
    public data class PCAction(val action: String) : LogPoseCommand
    public data class ToggleHud(val visible: Boolean) : LogPoseCommand
}
