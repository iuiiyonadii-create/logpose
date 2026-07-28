package com.uriel.logpose.core.engine

import com.uriel.logpose.core.app.AppManager
import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.context.CommandContext
import com.uriel.logpose.core.context.CommandHistory
import com.uriel.logpose.core.engine.registry.DefaultCommandRegistry
import com.uriel.logpose.core.memory.CommandMemory
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.features.voice.CallManager
import com.uriel.logpose.features.voice.FeedbackManager
import com.uriel.logpose.features.voice.VoiceManager
import com.uriel.logpose.features.notifications.NotificationReader
import com.uriel.logpose.features.navigation.NavigationManager
import com.uriel.logpose.core.services.LogPoseCallService

/**
 * Despacha los comandos a los manejadores correspondientes.
 */
object CommandDispatcher {

    private val registry = DefaultCommandRegistry()

    init {
        registry.register(Command.StartListening::class) {
            com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(com.uriel.logpose.core.app.LogPoseApplication.instance).startListening()
        }

        registry.register(Command.StopListening::class) {
            com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(com.uriel.logpose.core.app.LogPoseApplication.instance).stopListening()
        }

        registry.register(Command.PauseMusic::class) {
            MusicManager.pause()
            com.uriel.logpose.core.network.PCBridge.sendCommand("pausa")
        }

        registry.register(Command.NextTrack::class) {
            MusicManager.next()
            com.uriel.logpose.core.services.AlertManager.beep()
        }

        registry.register(Command.PreviousTrack::class) {
            MusicManager.previous()
            com.uriel.logpose.core.services.AlertManager.beep()
        }

        registry.register(Command.VolumeUp::class) {
            MusicManager.volumeUp()
        }

        registry.register(Command.VolumeDown::class) {
            MusicManager.volumeDown()
        }

        registry.register(Command.Navigate::class) { command ->
            val navigate = command as Command.Navigate
            NavigationManager.navigateTo(navigate.destination)
        }

        registry.register(Command.StopNavigation::class) {
            NavigationManager.stopNavigation()
            com.uriel.logpose.core.network.PCBridge.sendCommand("cerrar mapas")
        }

        registry.register(Command.EndTrip::class) {
            LogPoseCallService.instance?.endTrip()
        }

        registry.register(Command.WhereAmI::class) {
            NavigationManager.announceLocation()
        }

        registry.register(Command.PlayMusic::class) { command ->
            val music = command as Command.PlayMusic
            
            // SINCRO CLAUDE: Limpiamos antes de enviar al Bridge para que la búsqueda sea exitosa
            val cleanQueryForPC = music.query.replace(Regex("(?i)^sí |^si |^ok "), "").trim()
            
            MusicManager.play(music.query)
            val pcCmd = if (cleanQueryForPC.isBlank()) "reproducir" else "poner $cleanQueryForPC"
            com.uriel.logpose.core.network.PCBridge.sendCommand(pcCmd)
        }

        registry.register(Command.OpenApp::class) { command ->
            val openApp = command as Command.OpenApp
            AppManager.openApp(openApp.appName)
        }

        registry.register(Command.CloseApp::class) { command ->
            val closeApp = command as Command.CloseApp
            AppManager.closeApp(closeApp.appName)
        }

        registry.register(Command.PrivacyModeOn::class) {
            FeedbackManager.speak("Listo, privacidad.")
        }

        registry.register(Command.PrivacyModeOff::class) {
            FeedbackManager.speak("Listo.")
        }

        registry.register(Command.GetStatus::class) {
            NotificationReader.readFull()
        }

        registry.register(Command.ReadNotifications::class) {
            NotificationReader.readFull()
        }

        registry.register(Command.SendMessage::class) { command ->
            val send = command as Command.SendMessage
            val targetContact = if (send.contact.isBlank()) {
                NotificationReader.getLastSender() ?: ""
            } else {
                send.contact
            }

            if (targetContact.isBlank()) {
                FeedbackManager.speak("¿A quién le mando el mensaje?")
                return@register
            }

            if (send.message.isBlank()) {
                FeedbackManager.speak("¿Qué querés que le diga a $targetContact?")
                return@register
            }

            val confirmPrompt = "Entendido. Le voy a mandar a $targetContact: ${send.message}. ¿Lo mando?"
            FeedbackManager.speak(confirmPrompt) {
                LogPoseEngine.setWaitingSendConfirmation()
            }
        }

        @Suppress("MissingPermission")
        registry.register(Command.Call::class) { command ->
            val call = command as Command.Call
            CallManager.makeCall(call.contact)
        }

        registry.register(Command.Multi::class) {
            LogPoseLogger.i("Multi-comando ejecutado")
        }

        registry.register(Command.AcceptCall::class) {
            CallManager.acceptCall()
        }

        registry.register(Command.RejectCall::class) {
            CallManager.rejectCall()
        }

        registry.register(Command.EndCall::class) {
            CallManager.onCallEnded()
        }

        registry.register(Command.VolumeAbsolute::class) { command ->
            val vol = command as Command.VolumeAbsolute
            MusicManager.setVolumeAbsolute(vol.level)
            com.uriel.logpose.core.network.PCBridge.sendCommand("volumen ${vol.level}")
        }

        registry.register(Command.Search::class) { command ->
            val search = command as Command.Search
            val platformStr = when(search.platform) {
                Command.Platform.NETFLIX -> "netflix"
                Command.Platform.SPOTIFY -> "spotify"
                Command.Platform.YOUTUBE -> "youtube"
                Command.Platform.DISCORD -> "discord"
                Command.Platform.INSTAGRAM -> "instagram"
                Command.Platform.WHATSAPP -> "whatsapp"
            }
            com.uriel.logpose.core.network.PCBridge.sendCommand("$platformStr ${search.query}")
        }

        registry.register(Command.Feedback::class) { command ->
            val feedback = command as Command.Feedback
            FeedbackManager.speak(feedback.text)
        }

        registry.register(Command.PCAction::class) { command ->
            val pc = command as Command.PCAction
            com.uriel.logpose.core.network.PCBridge.sendCommand(pc.action)
        }

        registry.register(Command.Help::class) {
            val helpText = "Puedes decir: subir o bajar volumen, siguiente o atrás, pausa, detener, o buscar música."
            FeedbackManager.speak(helpText)
        }

        registry.register(Command.GetWeather::class) {
            com.uriel.logpose.core.weather.WeatherManager.reportCurrentWeather()
        }

        registry.register(Command.Unknown::class) {
            LogPoseLogger.w("Comando desconocido")
        }

        registry.register(Command.Ignore::class) {
            // Silencio total
        }
    }

    fun execute(command: Command) {
        LogPoseLogger.i("Ejecutando: ${command::class.simpleName}")
        val executed = registry.execute(command)

        CommandHistory.add(CommandContext(command = command, success = executed))
        CommandMemory.remember(command)
        
        if (executed) {
            val intent = when(command) {
                is Command.PlayMusic -> com.uriel.logpose.thamis.intent.Intent.PLAY_MUSIC
                is Command.PauseMusic -> com.uriel.logpose.thamis.intent.Intent.PAUSE_MUSIC
                is Command.NextTrack -> com.uriel.logpose.thamis.intent.Intent.NEXT_TRACK
                is Command.PreviousTrack -> com.uriel.logpose.thamis.intent.Intent.PREVIOUS_TRACK
                is Command.Navigate -> com.uriel.logpose.thamis.intent.Intent.NAVIGATE
                is Command.StopNavigation -> com.uriel.logpose.thamis.intent.Intent.STOP_NAVIGATION
                is Command.Call -> com.uriel.logpose.thamis.intent.Intent.CALL_CONTACT
                is Command.OpenApp -> com.uriel.logpose.thamis.intent.Intent.OPEN_APP
                is Command.SendMessage -> com.uriel.logpose.thamis.intent.Intent.SEND_MESSAGE
                is Command.GetWeather -> com.uriel.logpose.thamis.intent.Intent.WEATHER
                is Command.ReadNotifications -> com.uriel.logpose.thamis.intent.Intent.READ_NOTIFICATION
                else -> com.uriel.logpose.thamis.intent.Intent.UNKNOWN
            }
            com.uriel.logpose.thamis.learning.LearningEngine.registerUsage(intent)
        }
    }
}
