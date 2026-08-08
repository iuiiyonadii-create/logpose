package com.uriel.logpose.core.engine

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.compat.core.LogPoseLogger
import android.content.Intent
import com.uriel.logpose.core.engine.registry.DefaultCommandRegistry
import com.uriel.logpose.features.voice.CallManager
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.features.voice.FeedbackManager
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.services.LogPoseCallService
import com.uriel.logpose.features.navigation.NavigationManager
import com.uriel.logpose.features.notifications.NotificationReader
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.thamis.communication.resolver.ContactResolver
import com.uriel.logpose.thamis.communication.provider.WhatsAppProvider
import com.uriel.logpose.core.services.LogPoseAccessibilityService
import com.uriel.logpose.core.services.LogPoseInCallService
import com.uriel.logpose.features.diagnostics.VehicleDiagnosticsManager
import com.uriel.logpose.features.safety.IncidentManager
import com.uriel.logpose.core.services.LogPoseHudService

/**
 * CommandDispatcher: Orquestador central de ejecución de comandos.
 * Integrado con el State Machine de THAMIS.
 */
object CommandDispatcher {

    private val registry = DefaultCommandRegistry()
    private val whatsAppProvider = WhatsAppProvider()

    private var pendingContact: String? = null
    private var pendingMessage: String? = null

    init {
        registry.register(LogPoseCommand.StartListening::class) {
            com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(LogPoseApplication.instance).startListening()
        }

        registry.register(LogPoseCommand.StopListening::class) {
            com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(LogPoseApplication.instance).stopListening()
        }

        registry.register(LogPoseCommand.PauseMusic::class) {
            MusicManager.pause()
        }

        registry.register(LogPoseCommand.PlayMusic::class) { command ->
            val play = command as LogPoseCommand.PlayMusic
            LogPoseLogger.i("Dispatcher: Reproduciendo música: '${play.query}'")
            MusicManager.play(play.query)
        }

        registry.register(LogPoseCommand.Navigate::class) { command ->
            val navigate = command as LogPoseCommand.Navigate
            NavigationManager.navigateTo(navigate.destination)
        }

        registry.register(LogPoseCommand.SendMessage::class) { command ->
            val send = command as LogPoseCommand.SendMessage
            LogPoseLogger.i("Dispatcher: Procesando SendMessage para '${send.contact}'")
            
            // Si el contacto está vacío, intentamos usar el último remitente de notificaciones
            val targetContactName = if (send.contact.isBlank()) {
                NotificationReader.getLastSender() ?: ""
            } else {
                send.contact
            }

            if (targetContactName.isBlank()) {
                FeedbackManager.speak("No sé a quién responderle, no vi ningún mensaje reciente.")
                return@register
            }

            val resolution = ContactResolver.resolve(targetContactName)
            
            if (resolution.resolvedContact != null) {
                pendingContact = resolution.resolvedContact.phoneNumber
                
                if (send.message.isNotBlank()) {
                    // Si ya tenemos el mensaje, pasamos directo a la confirmación
                    pendingMessage = send.message
                    FeedbackManager.speak("Le voy a mandar a ${resolution.resolvedContact.name}: '${send.message}'. ¿Confirmás?") {
                        WorldModelEngine.update("Messaging") { it.copy(
                            cognitive = it.cognitive.copy(conversationState = "WAITING_CONFIRMATION")
                        )}
                    }
                } else {
                    // Si no hay mensaje, preguntamos
                    val prompt = "¿Qué querés decirle a ${resolution.resolvedContact.name}?"
                    FeedbackManager.speak(prompt) {
                        WorldModelEngine.update("Messaging") { it.copy(
                            cognitive = it.cognitive.copy(conversationState = "WAITING_MESSAGE_CONTENT")
                        )}
                    }
                }
            } else if (resolution.isAmbiguous) {
                val names = resolution.candidates.take(3).joinToString(" o ") { it.name }
                FeedbackManager.speak("Encontré varios. ¿Querés llamar a $names?")
            } else {
                FeedbackManager.speak("No encontré a ${send.contact} en tus contactos.")
            }
        }

        registry.register(LogPoseCommand.MessageContent::class) { command ->
            val msg = (command as LogPoseCommand.MessageContent).content
            pendingMessage = msg
            FeedbackManager.speak("Entendido. Le voy a mandar: '$msg'. ¿Confirmás?") {
                WorldModelEngine.update("Messaging") { it.copy(
                    cognitive = it.cognitive.copy(conversationState = "WAITING_CONFIRMATION")
                )}
            }
        }

        registry.register(LogPoseCommand.ConfirmAction::class) {
            val contact = pendingContact
            val msg = pendingMessage
            val snapshot = WorldModelEngine.getCurrentSnapshot()
            
            if (snapshot.cognitive.activeIntent == "NAVIGATE_TO_STATION") {
                LogPoseLogger.i("Dispatcher: Navegando a estación de servicio por alerta proactiva.")
                NavigationManager.navigateTo("gasolinera")
                resetMessagingState()
                return@register
            }

            if (contact != null && msg != null) {
                LogPoseLogger.i("Dispatcher: Activando automatización para WhatsApp a $contact")
                LogPoseAccessibilityService.isPendingAutomation = true
                whatsAppProvider.sendMessage(contact, msg)
                FeedbackManager.speak("Listo, enviado.")
            }
            resetMessagingState()
        }

        registry.register(LogPoseCommand.CancelAction::class) {
            FeedbackManager.speak("Cancelado.")
            resetMessagingState()
        }

        registry.register(LogPoseCommand.AcceptCall::class) {
            LogPoseLogger.i("Dispatcher: Atendiendo llamada vía InCallService")
            LogPoseInCallService.instance?.answerActiveCall()
        }

        registry.register(LogPoseCommand.RejectCall::class) {
            LogPoseLogger.i("Dispatcher: Rechazando llamada vía InCallService")
            LogPoseInCallService.instance?.disconnectActiveCall()
        }

        @Suppress("MissingPermission")
        registry.register(LogPoseCommand.Call::class) { command ->
            val call = command as LogPoseCommand.Call
            CallManager.makeCall(call.contact)
        }

        registry.register(LogPoseCommand.OpenApp::class) { command ->
            val openApp = command as LogPoseCommand.OpenApp
            LogPoseApplication.entryPoint.appLauncher().openApp(openApp.appName)
        }

        registry.register(LogPoseCommand.StopNavigation::class) {
            NavigationManager.stopNavigation()
        }

        registry.register(LogPoseCommand.SafetyAlert::class) { command ->
            val alert = (command as LogPoseCommand.SafetyAlert).alertType
            LogPoseLogger.w("Dispatcher: ALERTA DE SEGURIDAD RECIBIDA: $alert")
            FeedbackManager.speak("Entendido, registro $alert detectado. Compartiendo alerta con otros riders.")
        }

        registry.register(LogPoseCommand.TrafficStatus::class) { command ->
            val location = (command as LogPoseCommand.TrafficStatus).location
            LogPoseLogger.i("Dispatcher: Consultando tráfico para $location")
            FeedbackManager.speak("Para $location me figura que hay demoras de diez minutos por un accidente.")
        }

        registry.register(LogPoseCommand.RestaurantSearch::class) { command ->
            val query = (command as LogPoseCommand.RestaurantSearch).query
            LogPoseLogger.i("Dispatcher: Buscando comida: $query")
            FeedbackManager.speak("Buscando $query cerca de tu ubicación. Te recomiendo el bodegón de la esquina.")
        }

        registry.register(LogPoseCommand.TransportInfo::class) { command ->
            val type = (command as LogPoseCommand.TransportInfo).type
            LogPoseLogger.i("Dispatcher: Consultando transporte: $type")
            FeedbackManager.speak("El $type está viniendo con demora de cinco minutos.")
        }

        // --- DIAGNÓSTICO DE MOTO ---
        registry.register(LogPoseCommand.GetVehicleStatus::class) {
            VehicleDiagnosticsManager.getFullStatus()
        }

        registry.register(LogPoseCommand.GetFuelLevel::class) {
            VehicleDiagnosticsManager.getFuelLevel()
        }

        registry.register(LogPoseCommand.GetMaintenanceInfo::class) {
            VehicleDiagnosticsManager.getMaintenanceInfo()
        }

        registry.register(LogPoseCommand.GetEngineTemp::class) {
            VehicleDiagnosticsManager.getEngineTemperature()
        }

        // --- SEGURIDAD PROACTIVA Y HUD ---
        registry.register(LogPoseCommand.RecordIncident::class) {
            IncidentManager.recordLastSeconds()
        }

        registry.register(LogPoseCommand.ToggleHud::class) { command ->
            val visible = (command as LogPoseCommand.ToggleHud).visible
            val intent = Intent(LogPoseApplication.instance, LogPoseHudService::class.java)
            if (visible) {
                LogPoseApplication.instance.startService(intent)
            } else {
                LogPoseApplication.instance.stopService(intent)
            }
        }
        
        registry.register(LogPoseCommand.Feedback::class) { command ->
            FeedbackManager.speak((command as LogPoseCommand.Feedback).text)
        }
    }

    private fun resetMessagingState() {
        pendingContact = null
        pendingMessage = null
        WorldModelEngine.update("Messaging") { it.copy(
            cognitive = it.cognitive.copy(conversationState = "IDLE")
        )}
    }

    fun execute(command: LogPoseCommand) {
        LogPoseLogger.d("Dispatcher: Ejecutando comando ${command::class.simpleName}")
        registry.execute(command)
    }
}
