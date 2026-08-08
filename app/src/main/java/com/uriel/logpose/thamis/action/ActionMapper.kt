package com.uriel.logpose.thamis.action

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.decision.Decision
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.language.LanguageProcessor

/**
 * Mapea decisiones de THAMIS a comandos ejecutables del sistema.
 */
object ActionMapper {

    fun map(decision: Decision, originalText: String): LogPoseCommand {
        var normalizedText = LanguageProcessor.process(originalText).lowercase()
        
        if (normalizedText.contains("cerrar") || normalizedText.contains("cierra")) {
            if (normalizedText.contains("spotify") || normalizedText.contains("musica") || normalizedText.contains("música")) {
                return LogPoseCommand.PauseMusic
            }
            if (normalizedText.contains("mapas") || normalizedText.contains("gps") || 
                normalizedText.contains("navegacion") || normalizedText.contains("navegación")) {
                return LogPoseCommand.StopNavigation
            }
        }

        return when (decision.intent) {
            Intent.PLAY_MUSIC -> {
                val mediaEntity = decision.entities["media"] ?: decision.entities["track"] ?: ""
                
                var query = if (mediaEntity.isNotBlank()) {
                    mediaEntity
                } else {
                    // Fallback a limpieza manual del texto original
                    normalizedText.replace(Regex("(?i)^reproducir |^reproduci |^pone |^poné |^poneme |^tira |^pasame "), "").trim()
                }

                // v5.3: Deduplicación Staff (Evita 'pakistan pone uzbekistan' por eco de Sherpa)
                val words = query.split(" ").distinct()
                val musicTriggerSet = setOf("pone", "poné", "poneme", "reproduce", "reproduci")
                
                var cleanQuery = words.filter { it.length >= 2 && !musicTriggerSet.contains(it.lowercase()) }.joinToString(" ")
                
                cleanQuery = cleanQuery.replace(Regex("(?i)^la cancion |^el tema |^algo de |^musica de "), "").trim()
                
                // Si Vosk/Sherpa alucinó con "de" al final del artista, lo limpiamos
                cleanQuery = cleanQuery.replace(Regex("\\bde$"), "").trim()
                
                // Sanitización v4.6: Si el query es basura acústica de Vosk conocida
                val acousticTrash = setOf("bola", "el rey", "mental", "reloj", "dos", "ojo", "vamos")
                if (cleanQuery in acousticTrash && decision.confidence < 0.8f) {
                    cleanQuery = ""
                }

                // v4.6.4: Si el comando está vacío, pedimos detalles
                val musicVerbs = setOf("pone", "poné", "poneme", "reproduce", "reproduci")
                if (cleanQuery.isEmpty() || musicVerbs.contains(cleanQuery)) {
                    return LogPoseCommand.Feedback("¿Qué querés escuchar?")
                }

                val finalQuery = if (cleanQuery.isNotBlank()) {
                    // v7.8: Enriquecimiento de búsqueda mediante ADN Musical
                    val associatedArtist = com.uriel.logpose.thamis.learning.LearningEngine.getArtistForTrack(cleanQuery)
                    if (associatedArtist != null && !cleanQuery.lowercase().contains(associatedArtist.lowercase())) {
                        com.uriel.logpose.core.compat.core.LogPoseLogger.i("🧠 ADN Musical: Enriqueciendo búsqueda con autor -> $associatedArtist")
                        "$cleanQuery $associatedArtist"
                    } else {
                        cleanQuery
                    }
                } else {
                    cleanQuery
                }
                
                LogPoseCommand.PlayMusic(finalQuery.trim())
            }
            Intent.PAUSE_MUSIC -> LogPoseCommand.PauseMusic
            Intent.NEXT_TRACK -> LogPoseCommand.NextTrack
            Intent.PREVIOUS_TRACK -> LogPoseCommand.PreviousTrack
            
            Intent.SET_VOLUME -> {
                if (normalizedText.contains("subir") || normalizedText.contains("sube") || normalizedText.contains("mas")) {
                    LogPoseCommand.VolumeUp
                } else {
                    LogPoseCommand.VolumeDown
                }
            }
            
            Intent.CALL_CONTACT -> {
                val contact = decision.entities["contact"] ?: normalizedText.replace("llamar a", "").trim()
                LogPoseCommand.Call(contact)
            }
            
            Intent.NAVIGATE -> {
                if (normalizedText.contains("como vamos") || normalizedText.contains("cómo vamos") || normalizedText == "ir") {
                    return LogPoseCommand.Navigate("")
                }

                var destination = decision.entities["destination"] ?: normalizedText
                    .replace(Regex("(?i)^ir a |^ir |^llevame |^navegar |^guiame |^ruta |^poner gps |^anda |^encara |^vamos |^buscá |^buscar |^donde hay "), "")
                    .trim()
                
                val searchTerms = listOf("nafta", "gasolinera", "estacion de servicio", "estación de servicio", "ypf", "shell", "axion", "puma")
                if (searchTerms.any { normalizedText.contains(it) }) {
                    destination = when {
                        normalizedText.contains("shell") -> "shell"
                        normalizedText.contains("ypf") -> "ypf"
                        normalizedText.contains("axion") -> "axion"
                        normalizedText.contains("puma") -> "puma"
                        else -> "gasolinera"
                    }
                }

                LogPoseCommand.Navigate(destination)
            }
            
            Intent.STOP_NAVIGATION -> LogPoseCommand.StopNavigation
            
            Intent.OPEN_APP -> {
                val cleanText = normalizedText.replace(Regex("(?i)^(abrir|abri|abrí|lanzar|arranca|poner|pone|poné|entra|entrar|abre)\\s+"), "").trim()
                
                // v4.6.4: Feedback interactivo en lugar de silencio
                val verbs = setOf("abrir", "abri", "abrí", "lanzar", "arranca", "poner", "pone", "poné", "reproduce", "entra", "entrar", "abre")
                if (cleanText.isEmpty() || verbs.contains(cleanText)) {
                    LogPoseCommand.Feedback("¿Qué aplicación querés que abra?")
                } else {
                    val app = decision.entities["app_name"] ?: cleanText
                    LogPoseCommand.OpenApp(app)
                }
            }

            Intent.READ_NOTIFICATION -> LogPoseCommand.ReadNotifications

            Intent.ANSWER_CALL -> {
                if (normalizedText.contains("entrena") || normalizedText.contains("simula")) {
                    com.uriel.logpose.core.intelligence.NeuroEvolutionSimulator.startInfiniteTraining()
                    LogPoseCommand.Feedback("Iniciando auto-entrenamiento neuronal infinito.")
                } else {
                    LogPoseCommand.AcceptCall
                }
            }
            Intent.REJECT_CALL -> LogPoseCommand.RejectCall

            Intent.SEND_MESSAGE -> {
                val contact = decision.entities["contact"] ?: normalizedText
                    .replace(Regex("(?i)^mensaje |^escribile |^mandale whatsapp |^mandale mensaje |^mandale |^mandá |^enviá "), "")
                    .trim()
                val message = decision.entities["message"] ?: ""
                LogPoseCommand.SendMessage(contact, message)
            }

            Intent.REPLY_MESSAGE -> {
                val message = decision.entities["parameter"] ?: originalText
                    .replace(Regex("(?i)^respondé |^responde |^contestale |^decile que |^escribile que |^ponele que |^mandale que "), "")
                    .trim()
                LogPoseCommand.SendMessage("", message) // Dispatcher resolverá el contacto
            }

            // --- CONVERSACIÓN ---
            Intent.MESSAGE_CONTENT -> {
                val content = decision.entities["content"] ?: originalText
                LogPoseCommand.MessageContent(content)
            }

            // --- MISIÓN #023: ENTRENAMIENTO STAFF ---
            Intent.CONFIRM_ACTION -> {
                if (normalizedText.contains("entrena") || normalizedText.contains("simula")) {
                    com.uriel.logpose.core.intelligence.NeuroEvolutionSimulator.startInfiniteTraining()
                    LogPoseCommand.Feedback("Iniciando auto-entrenamiento neuronal infinito.")
                } else {
                    LogPoseCommand.ConfirmAction
                }
            }
            
            Intent.CANCEL_ACTION -> {
                if (normalizedText.contains("detener") || normalizedText.contains("para")) {
                    com.uriel.logpose.core.intelligence.NeuroEvolutionSimulator.stopTraining()
                    LogPoseCommand.Feedback("Entrenamiento detenido.")
                }
                // v6.1: Si el usuario dice "olvida eso" o "está mal", purgamos la memoria dinámica
                else if (normalizedText.contains("olvida") || normalizedText.contains("borra") || normalizedText.contains("mal")) {
                    com.uriel.logpose.thamis.learning.LearningEngine.forgetLast()
                    LogPoseCommand.Feedback("Entendido, borré el último aprendizaje.")
                } else {
                    LogPoseCommand.CancelAction
                }
            }

            Intent.SWITCH_TAB -> LogPoseCommand.PCAction("cambiar pestaña")
            Intent.REPEAT_MUSIC -> LogPoseCommand.PCAction("bucle")
            Intent.YIELD_CONTROL -> LogPoseCommand.YieldControl

            // --- REDES SOCIALES ---
            Intent.SOCIAL_SEARCH -> {
                val user = decision.entities["contact"] ?: decision.entities["parameter"] ?: ""
                LogPoseCommand.OpenApp("instagram://user?username=$user")
            }
            Intent.SOCIAL_CAMERA -> LogPoseCommand.OpenApp("instagram://camera")

            // --- SEGURIDAD Y TRÁFICO ---
            Intent.SAFETY_ALERT -> {
                val type = decision.entities["parameter"] ?: normalizedText
                    .replace(Regex("(?i)^ojo |^guarda |^cuidado |^atenti |^hay |^viste "), "")
                    .trim()
                LogPoseCommand.SafetyAlert(type)
            }
            Intent.TRAFFIC_STATUS -> {
                val location = decision.entities["parameter"] ?: normalizedText
                    .replace(Regex("(?i)^quilombo en |^embotellamiento en |^como viene |^estado de |^trafico en |^tráfico en "), "")
                    .trim()
                LogPoseCommand.TrafficStatus(location)
            }

            Intent.RESTAURANT_SEARCH -> {
                val query = decision.entities["parameter"] ?: normalizedText
                LogPoseCommand.RestaurantSearch(query)
            }
            Intent.TRANSPORT_INFO -> {
                val type = decision.entities["parameter"] ?: normalizedText
                LogPoseCommand.TransportInfo(type)
            }

            // --- DIAGNÓSTICO DE MOTO ---
            Intent.VEHICLE_STATUS -> {
                if (normalizedText.contains("gustos") || normalizedText.contains("preferencias")) {
                    val favs = com.uriel.logpose.thamis.learning.LearningEngine.getLearnedMusicEntities().take(5).joinToString(", ")
                    LogPoseCommand.Feedback("Tus artistas Staff con prioridad son: $favs")
                } else {
                    LogPoseCommand.GetVehicleStatus
                }
            }
            Intent.FUEL_LEVEL -> LogPoseCommand.GetFuelLevel
            Intent.MAINTENANCE_INFO -> LogPoseCommand.GetMaintenanceInfo
            Intent.ENGINE_TEMP -> LogPoseCommand.GetEngineTemp

            // --- SEGURIDAD PROACTIVA Y UI ---
            Intent.RECORD_INCIDENT -> LogPoseCommand.RecordIncident
            Intent.TOGGLE_HUD -> {
                val isHide = normalizedText.contains("ocultar") || normalizedText.contains("saca") || normalizedText.contains("sacá")
                LogPoseCommand.ToggleHud(!isHide)
            }

            else -> LogPoseCommand.Unknown
        }
    }
}
