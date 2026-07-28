package com.uriel.logpose.thamis.action

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.language.LanguageProcessor

/**
 * Mapea decisiones de THAMIS a comandos ejecutables del sistema.
 */
object ActionMapper {

    fun map(decision: Decision, originalText: String): Command {
        // Usamos el procesador de lenguaje centralizado
        var normalizedText = LanguageProcessor.process(originalText).lowercase()
        
        // --- Inteligencia Colectiva de Thamis ---
        // SINCRO: Eliminamos lógica hardcodeada de "abrir" para que THAMIS decida
        
        // SINCRO CLAUDE: Prioridad de texto para el comando cerrar
        if (normalizedText.contains("cerrar") || normalizedText.contains("cierra")) {
            if (normalizedText.contains("spotify") || normalizedText.contains("musica") || normalizedText.contains("música")) {
                return Command.PauseMusic
            }
            if (normalizedText.contains("mapas") || normalizedText.contains("gps") || 
                normalizedText.contains("navegacion") || normalizedText.contains("navegación")) {
                return Command.StopNavigation
            }
        }

        return when (decision.intent) {
            Intent.PLAY_MUSIC -> Command.PlayMusic(normalizedText)
            Intent.PAUSE_MUSIC -> Command.PauseMusic
            Intent.NEXT_TRACK -> Command.NextTrack
            Intent.PREVIOUS_TRACK -> Command.PreviousTrack
            
            Intent.SET_VOLUME -> {
                if (normalizedText.contains("subir") || normalizedText.contains("sube") || normalizedText.contains("mas")) {
                    Command.VolumeUp
                } else {
                    Command.VolumeDown
                }
            }
            
            Intent.CALL_CONTACT -> {
                val contact = decision.entities["contact"] ?: normalizedText.replace("llamar a", "").trim()
                Command.Call(contact)
            }
            
            Intent.NAVIGATE -> {
                // Desvío especial para "cómo vamos" o "ir" solo -> Abrir Maps sin destino
                if (normalizedText.contains("como vamos") || normalizedText.contains("cómo vamos") || normalizedText == "ir") {
                    return Command.Navigate("")
                }

                // Si la intención es navegar, limpiamos los prefijos comunes para obtener el destino
                var destination = decision.entities["destination"] ?: normalizedText
                    .replace(Regex("(?i)^ir a |^ir |^llevame |^navegar |^guiame |^ruta |^poner gps |^anda |^encara |^vamos |^buscá |^buscar |^donde hay "), "")
                    .trim()
                
                // Mapeo dinámico de POIs críticos (Modo Gasolinera / Emergencias)
                val searchTerms = listOf("nafta", "gasolinera", "estacion de servicio", "estación de servicio", "ypf", "shell", "axion", "puma")
                if (searchTerms.any { normalizedText.contains(it) }) {
                    destination = when {
                        normalizedText.contains("shell") -> "shell"
                        normalizedText.contains("ypf") -> "ypf"
                        normalizedText.contains("axion") -> "axion"
                        normalizedText.contains("puma") -> "puma"
                        else -> "gasolinera"
                    }
                } else if (normalizedText.contains("cajero")) {
                    destination = if (normalizedText.contains("banelco")) "cajero banelco" else "cajero link"
                } else if (normalizedText.contains("gomeria") || normalizedText.contains("gomero") || normalizedText.contains("parche")) {
                    destination = "gomeria"
                } else if (normalizedText.contains("taller") || normalizedText.contains("mecanico")) {
                    destination = "taller de motos"
                } else if (normalizedText.contains("hospital") || normalizedText.contains("clinica") || normalizedText.contains("guardia")) {
                    destination = "hospital"
                } else if (normalizedText.contains("comisaria") || normalizedText.contains("policia")) {
                    destination = "comisaria"
                }

                Command.Navigate(destination)
            }
            
            Intent.STOP_NAVIGATION -> Command.StopNavigation
            
            Intent.OPEN_APP -> {
                val app = decision.entities["app_name"] ?: normalizedText.replace("abrir ", "").trim()
                Command.OpenApp(app)
            }

            Intent.SWITCH_TAB -> Command.PCAction("cambiar pestaña")
            Intent.REPEAT_MUSIC -> Command.PCAction("bucle")

            Intent.READ_NOTIFICATION -> Command.ReadNotifications

            Intent.SMART_TV_CONTROL -> Command.Feedback(
                "Entendido. Todavía no tengo los cables conectados a la tele, pero ya anoté en mi cerebro " +
                "que querés que maneje el Smart TV. ¡Pronto lo haremos realidad!"
            )

            Intent.IOT_CONTROL -> Command.Feedback(
                "Visión de casa inteligente activada. Estoy lista para aprender a controlar luces y portones " +
                "en la próxima actualización del sistema."
            )

            Intent.SEND_MESSAGE -> {
                // Limpieza agresiva del contacto para evitar verbos en el nombre
                val contact = decision.entities["contact"] ?: normalizedText
                    .replace(Regex("(?i)^mensaje |^escribile |^mandale whatsapp |^mandale mensaje |^mandale |^mandá |^enviá "), "")
                    .trim()
                Command.SendMessage(contact)
            }

            Intent.YIELD_CONTROL -> Command.YieldControl

            Intent.ASK_LEGAL -> Command.Feedback(
                "Sí, estoy segura. Es totalmente legal porque no modificamos ni hackeamos a otras apps. " +
                "Solo les enviamos señales estándar de Android que cualquier sistema de manos libres usaría."
            )

            Intent.ASK_PLAY_STORE -> Command.Feedback(
                "Sí, podés subirla sin miedo. Cumplimos con las reglas de Google al usar intents seguros y " +
                "al declarar la app como un asistente de seguridad para motociclistas. Solo recordá no usar logos de terceros."
            )

            else -> Command.Unknown
        }
    }
}
