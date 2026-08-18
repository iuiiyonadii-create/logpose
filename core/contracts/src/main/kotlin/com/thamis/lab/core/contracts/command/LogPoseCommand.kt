package com.thamis.lab.core.contracts.command

import com.thamis.lab.core.contracts.version.MessageVersion

/**
 * LogPoseCommand: Unified action contract for rider interactions.
 * Refactored v2.0: Grouped by domain and versioned.
 */
public sealed interface LogPoseCommand {
    public val version: MessageVersion get() = MessageVersion()

    // --- 1. MEDIA COMMANDS ---
    public sealed interface Media : LogPoseCommand {
        public object PauseMusic : Media
        public object NextTrack : Media
        public object PreviousTrack : Media
        public object RepeatMusic : Media
        public data class PlayMusic(val query: String) : Media
    }

    // --- 2. NAVIGATION COMMANDS ---
    public sealed interface Navigation : LogPoseCommand {
        public object StopNavigation : Navigation
        public object WhereAmI : Navigation
        public data class Navigate(val destination: String) : Navigation
        public data class TrafficStatus(val location: String) : Navigation
    }

    // --- 3. COMMUNICATION COMMANDS ---
    public sealed interface Communication : LogPoseCommand {
        public object AcceptCall : Communication
        public object RejectCall : Communication
        public object EndCall : Communication
        public object ReadNotifications : Communication
        public data class Call(val contact: String) : Communication
        public data class SendMessage(val contact: String, val message: String) : Communication
    }

    // --- 4. SYSTEM & HUD COMMANDS ---
    public sealed interface System : LogPoseCommand {
        public object VolumeUp : System
        public object VolumeDown : System
        public object Mute : System
        public object GetVehicleStatus : System
        public object GetFuelLevel : System
        public object GetMaintenanceInfo : System
        public object GetEngineTemp : System
        public object GetWeather : System
        public object RecordIncident : System
        public object YieldControl : System
        public object SwitchTab : System
        public data class VolumeAbsolute(val level: Int) : System
        public data class ToggleHud(val visible: Boolean) : System
        public data class PCAction(val action: String) : System
    }

    // --- 5. INTERNAL & GENERIC ---
    public object StartListening : LogPoseCommand
    public object StopListening : LogPoseCommand
    public object ConfirmAction : LogPoseCommand
    public object CancelAction : LogPoseCommand
    public object EndTrip : LogPoseCommand
    public object Multi : LogPoseCommand
    public object Help : LogPoseCommand
    public object Ignore : LogPoseCommand
    public object Unknown : LogPoseCommand
    
    public data class MessageContent(val content: String) : LogPoseCommand
    public data class OpenApp(val appName: String) : LogPoseCommand
    public data class CloseApp(val appName: String) : LogPoseCommand
    public data class Search(val platform: String, val query: String) : LogPoseCommand
    public data class SafetyAlert(val alertType: String) : LogPoseCommand
    public data class RestaurantSearch(val query: String) : LogPoseCommand
    public data class TransportInfo(val type: String) : LogPoseCommand
    public data class Feedback(val text: String) : LogPoseCommand
}
