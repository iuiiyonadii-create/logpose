package com.uriel.logpose.thamis.action

import android.content.Context
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.decision.Decision
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.language.LanguageProcessor
import com.uriel.logpose.thamis.cognitive.ConfidenceEngine

import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.regex.Pattern
import com.uriel.logpose.thamis.communication.ContactNormalizer

/**
 * Mapea decisiones de THAMIS a comandos ejecutables del sistema.
 * v11.4: Integración DriverProfile & ContactNormalizer (YMK Fix).
 * v12.0: Guardián de seguridad con ConfidenceEngine + Regex con word boundaries.
 */
object ActionMapper {

    private val ARTICULOS_CONECTORES_REGEX = Pattern.compile(
        "^(a|el|la|los|las|un|una|unos|unas|de|del|tema|cancion|canción|algo|un poco|de|musica de|música de|la cancion de|el tema de)\\b", 
        Pattern.CASE_INSENSITIVE
    )

    private val MUSIC_CLEAN_REGEX = Regex("(?i).*(reproducir|reproducí|reproduce|repro|poned|ponéme|poneme|poné|pone|ponete|ponele|tira|tirá|pasame|pasá|largame|lárgame|mandale|mándale)\\s+")
    private val MUSIC_EXTRA_CLEAN_REGEX = Regex("(?i).*(la cancion|el tema|algo de|musica de|música de)\\s+")
    
    private val NAV_CLEAN_REGEX = Regex("(?i).*(ir a|ir|llevame a|lleváme a|andá a|anda a|guiame a|guiáme a|navegar a|navegá a|navega a|ruta a|poner gps a|gps a|encara para|encará para|vamos a|buscá|buscar|donde queda|dónde queda|llegar a|quiero ir a)\\s+")
    private val OPEN_APP_CLEAN_REGEX = Regex("(?i).*(abrir|abri|abrí|abril|lanzar|arranca|poner|pone|poné|entra|entrar|abre)\\s+")
    private val SEND_MSG_CLEAN_REGEX = Regex("(?i)^mensaje |^escribile |^mandale whatsapp |^mandale mensaje |^mandale |^mandá |^enviá ")
    private val REPLY_MSG_CLEAN_REGEX = Regex("(?i)^respondé |^responde |^contestale |^decile que |^escribile que |^ponele que |^mandale que ")
    private val SAFETY_ALERT_CLEAN_REGEX = Regex("(?i)^ojo |^guarda |^cuidado |^atenti |^hay |^viste ")
    private val TRAFFIC_STATUS_CLEAN_REGEX = Regex("(?i)^quilombo en |^embotellamiento en |^como viene |^estado de |^trafico en |^tráfico en ")

    fun map(
        decision: Decision, 
        originalText: String,
        confidenceReport: ConfidenceEngine.ConfidenceReport? = null
    ): LogPoseCommand {
        // [PREDICTIVE_CACHE_ZONE_START]
        // Las reglas predictivas de alta prioridad se inyectarán aquí.
        // [PREDICTIVE_CACHE_ZONE_END]

        // Guardián de Seguridad: Si existe reporte de confianza, validar antes de despachar
        if (confidenceReport != null && !ConfidenceEngine.isSafeToExecute(decision, confidenceReport)) {
            LogPoseLogger.w("ActionMapper", "⛔ Dispatch abortado: Falló validación de isSafeToExecute.")
            return LogPoseCommand.Ignore
        }

        // v58.0: Bypass de normalización. Si viene del pipeline, ya está limpio.
        val normalizedText = originalText.lowercase()
        val textWithoutWake = normalizedText.trim()

        if (textWithoutWake.contains("cerrar") || textWithoutWake.contains("cierra")) {
            if (textWithoutWake.contains("spotify") || textWithoutWake.contains("musica") || textWithoutWake.contains("música")) {
                return LogPoseCommand.Media.PauseMusic
            }
            if (textWithoutWake.contains("mapas") || textWithoutWake.contains("gps") || 
                textWithoutWake.contains("navegacion") || textWithoutWake.contains("navegación")) {
                return LogPoseCommand.Navigation.StopNavigation
            }
        }

        // v68.5: Blindaje de Finalización de Viaje (Staff Fix)
        // Priorizamos terminar el viaje si aparecen palabras clave de navegación
        if (textWithoutWake.contains("viaje") || textWithoutWake.contains("navegacion") || textWithoutWake.contains("navegación")) {
            if (textWithoutWake.contains("cancela") || textWithoutWake.contains("termina") || textWithoutWake.contains("parar")) {
                LogPoseLogger.w("ActionMapper", "Comando de finalización de viaje detectado -> EndTrip")
                return LogPoseCommand.EndTrip
            }
        }

        // v11.2: Detección quirúrgica de comandos rápidos de la calle Rioplatense con word boundaries
        val nextTrackRegex = Regex("(?i)\\b(pone otra cosa|poné otra cosa|pone algo diferente|poné algo diferente|pone algo distinto|poné algo distinto|pone otra|poné otra|cambia de tema|sacá esto|saca esto|sacá ese tema|saca ese tema|pasalo|siguiente|pasa de tema)\\b")
        if (nextTrackRegex.containsMatchIn(normalizedText)) {
            LogPoseLogger.i("ActionMapper", "Comando de cambio rápido de canción detectado -> NextTrack")
            return LogPoseCommand.Media.NextTrack
        }

        // [DYNAMIC_LEARNING_ZONE_START]
        // Las reglas aprendidas por la IA se inyectarán aquí.
        // [DYNAMIC_LEARNING_ZONE_END]

        return when (decision.intent) {
            Intent.PLAY_MUSIC -> {
                // v6.9: Si ya tenemos una URI pre-calculada por el IntentDetector (Direct Gateway), la usamos
                decision.intentUri?.let { uri ->
                    if (uri.startsWith("spotify:")) return LogPoseCommand.Media.PlayMusic(uri)
                }

                val mediaEntity = decision.entities["media"] ?: decision.entities["track"] ?: ""
                
                // v109.5: RAZÓN REAL - Si la decisión viene de la IA (Gemma), BYPASS total de limpieza.
                // Confiamos 100% en lo que el cerebro razonó.
                if (decision.fromAi && mediaEntity.isNotBlank()) {
                    LogPoseLogger.i("ActionMapper", "🤖 IA-Direct: Reproduciendo entidad limpia -> '$mediaEntity'")
                    return LogPoseCommand.Media.PlayMusic(mediaEntity)
                }
                
                var query = if (mediaEntity.isNotBlank()) {
                    mediaEntity
                } else {
                    normalizedText.replace(MUSIC_CLEAN_REGEX, "").trim()
                }

                // v85.0: Si el usuario solo dice "play", interpretamos como Resume
                if (query.lowercase().trim() == "play") {
                    LogPoseLogger.i("ActionMapper", "Comando 'play' puro detectado -> ResumeMusic")
                    return LogPoseCommand.Media.PlayMusic("") // Query vacío dispara resume en MusicManager
                }

                // v10.0: Limpieza "Fuzzy Extremo" en cascada
                query = limpiarArticulosYConectores(query)

                // v5.3: Deduplicación STAFF Eliminada & Rescate de palabras cortas (y, a, o)
                val musicTriggerSet = setOf("pone", "poné", "poneme", "ponéme", "poned", "ponete", "ponele", "reproduce", "reproduci", "reproducí", "reproducir", "repro", "largame", "lárgame", "mandale", "mándale")
                val words = query.split(" ")
                
                var cleanQuery = words.filter { 
                    (it.length >= 2 || it.lowercase() in setOf("y", "a", "o", "3")) && !musicTriggerSet.contains(it.lowercase()) 
                }.joinToString(" ")
                cleanQuery = cleanQuery.replace(MUSIC_EXTRA_CLEAN_REGEX, "").trim()
                
                // Limpieza de preposiciones en bordes
                cleanQuery = cleanQuery.replace(Regex("\\bde$"), "").trim()
                cleanQuery = cleanQuery.replace(Regex("^de\\b"), "").trim()

                // Sanitización v4.6: Si el query es basura acústica de Vosk conocida
                val acousticTrash = setOf("bola", "el rey", "mental", "reloj", "dos", "ojo", "vamos")
                if (cleanQuery in acousticTrash && decision.confidence < 0.8f) {
                    cleanQuery = ""
                }

                if (cleanQuery.isEmpty() || musicTriggerSet.contains(cleanQuery)) {
                    return LogPoseCommand.Feedback("¿Qué querés escuchar?")
                }

                val finalQuery = if (cleanQuery.isNotBlank()) {
                    // v79.0: ADN Musical con prevención de duplicidad Staff
                    val associatedArtist = com.uriel.logpose.thamis.learning.LearningEngine.getArtistForTrack(cleanQuery)
                    val queryLower = cleanQuery.lowercase()
                    if (associatedArtist != null) {
                        val artistLower = associatedArtist.lowercase()
                        if (!queryLower.contains(artistLower)) {
                            LogPoseLogger.i("🧠 ADN Musical: Enriqueciendo búsqueda con autor -> $associatedArtist")
                            "$cleanQuery $associatedArtist"
                        } else {
                            cleanQuery
                        }
                    } else {
                        cleanQuery
                    }
                } else {
                    cleanQuery
                }
                
                LogPoseCommand.Media.PlayMusic(finalQuery.trim())
            }
            Intent.PAUSE_MUSIC -> LogPoseCommand.Media.PauseMusic
            Intent.NEXT_TRACK -> LogPoseCommand.Media.NextTrack
            Intent.PREVIOUS_TRACK -> LogPoseCommand.Media.PreviousTrack
            
            Intent.SET_VOLUME -> {
                if (normalizedText.contains("subir") || normalizedText.contains("sube") || normalizedText.contains("mas")) {
                    LogPoseCommand.System.VolumeUp
                } else {
                    LogPoseCommand.System.VolumeDown
                }
            }
            
            Intent.CALL_CONTACT -> {
                val rawContact = decision.entities["contact"] ?: normalizedText.replace("llamar a", "").trim()
                val finalContact = ContactNormalizer.normalize(rawContact)
                LogPoseCommand.Communication.Call(finalContact)
            }
            
            Intent.NAVIGATE -> {
                // v6.9: Si ya tenemos una URI pre-calculada por el IntentDetector (Direct Gateway), la usamos
                decision.intentUri?.let { uri ->
                    if (uri.startsWith("geo:")) return LogPoseCommand.Navigation.Navigate(uri)
                }

                if (normalizedText.contains("como vamos") || normalizedText.contains("cómo vamos") || normalizedText == "ir") {
                    return LogPoseCommand.Navigation.Navigate("")
                }

                var destination = decision.entities["destination"] ?: normalizedText
                    .replace(NAV_CLEAN_REGEX, "")
                    .replace(Regex("(?i)^a\\s+"), "")
                    .replace(Regex("(?i)\\s+(por favor|che)$"), "")
                    .trim()
                
                destination = limpiarArticulosYConectores(destination)
                
                // v11.4: Inyección de Preferencia de Estación (DriverProfile)
                val searchTerms = listOf("nafta", "gasolinera", "estacion de servicio", "estación de servicio", "ypf", "shell", "axion", "suma", "puma")
                if (searchTerms.any { normalizedText.contains(it) }) {
                    val preferred = com.uriel.logpose.core.services.DriverProfileDelegator.getPreferredGasStation()
                    destination = when {
                        normalizedText.contains("shell") -> "shell"
                        normalizedText.contains("ypf") -> "ypf"
                        normalizedText.contains("axion") -> "axion"
                        normalizedText.contains("puma") -> "puma"
                        // Si el usuario solo dijo "estacion", inyectamos su marca favorita
                        else -> preferred
                    }
                    LogPoseLogger.i("ActionMapper", "📍 Autocompletado por perfil: $destination")
                }

                LogPoseCommand.Navigation.Navigate(destination)
            }
            
            Intent.STOP_NAVIGATION -> LogPoseCommand.Navigation.StopNavigation
            
            Intent.OPEN_APP -> {
                val app = decision.entities["app_name"]
                if (app != null && app.isNotBlank()) {
                    LogPoseCommand.OpenApp(app)
                } else {
                    val cleanText = textWithoutWake.replace(OPEN_APP_CLEAN_REGEX, "").trim()
                    val finalAppQuery = limpiarArticulosYConectores(cleanText)
                    
                    val verbs = setOf("abrir", "abri", "abrí", "abril", "lanzar", "arranca", "poner", "pone", "poné", "reproduce", "entra", "entrar", "abre")
                    if (finalAppQuery.isEmpty() || verbs.contains(finalAppQuery)) {
                        LogPoseCommand.Feedback("¿Qué aplicación querés que abra?")
                    } else {
                        LogPoseCommand.OpenApp(finalAppQuery)
                    }
                }
            }

            Intent.READ_NOTIFICATION -> LogPoseCommand.Communication.ReadNotifications

            Intent.ANSWER_CALL -> LogPoseCommand.Communication.AcceptCall
            Intent.REJECT_CALL -> LogPoseCommand.Communication.RejectCall

            Intent.SEND_MESSAGE -> {
                val rawContact = decision.entities["contact"] ?: normalizedText
                    .replace(SEND_MSG_CLEAN_REGEX, "")
                    .trim()
                val finalContact = ContactNormalizer.normalize(rawContact)
                val message = decision.entities["message"] ?: ""
                LogPoseCommand.Communication.SendMessage(finalContact, message)
            }

            Intent.REPLY_MESSAGE -> {
                val message = decision.entities["parameter"] ?: originalText
                    .replace(REPLY_MSG_CLEAN_REGEX, "")
                    .trim()
                LogPoseCommand.Communication.SendMessage("", message) // Dispatcher resolverá el contacto
            }

            // --- CONVERSACIÓN ---
            Intent.MESSAGE_CONTENT -> {
                val content = decision.entities["content"] ?: originalText
                LogPoseCommand.MessageContent(content)
            }

            // --- MISIÓN #023: ENTRENAMIENTO STAFF ---
            Intent.CONFIRM_ACTION -> {
                if (normalizedText.contains("entrena") || normalizedText.contains("simula")) {
                    com.uriel.logpose.thamis.intelligence.NeuroEvolutionSimulator.startInfiniteTraining()
                    LogPoseCommand.Feedback("Iniciando auto-entrenamiento neuronal infinito.")
                } else {
                    LogPoseCommand.ConfirmAction
                }
            }
            
            Intent.CANCEL_ACTION -> {
                if (normalizedText.contains("detener") || normalizedText.contains("para")) {
                    com.uriel.logpose.thamis.intelligence.NeuroEvolutionSimulator.stopTraining()
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

            Intent.SWITCH_TAB -> LogPoseCommand.System.PCAction("cambiar pestaña")
            Intent.REPEAT_MUSIC -> LogPoseCommand.System.PCAction("bucle")
            Intent.YIELD_CONTROL -> LogPoseCommand.System.YieldControl

            // --- REDES SOCIALES ---
            Intent.SOCIAL_SEARCH -> {
                val user = decision.entities["contact"] ?: decision.entities["parameter"] ?: ""
                LogPoseCommand.OpenApp("instagram://user?username=$user")
            }
            Intent.SOCIAL_CAMERA -> LogPoseCommand.OpenApp("instagram://camera")

            // --- SEGURIDAD Y TRÁFICO ---
            Intent.SAFETY_ALERT -> {
                val type = decision.entities["parameter"] ?: normalizedText
                    .replace(SAFETY_ALERT_CLEAN_REGEX, "")
                    .trim()
                LogPoseCommand.SafetyAlert(type)
            }
            Intent.TRAFFIC_STATUS -> {
                val location = decision.entities["parameter"] ?: normalizedText
                    .replace(TRAFFIC_STATUS_CLEAN_REGEX, "")
                    .trim()
                LogPoseCommand.Navigation.TrafficStatus(location)
            }

            Intent.RESTAURANT_SEARCH -> {
                val query = decision.entities["parameter"] ?: normalizedText
                LogPoseCommand.RestaurantSearch(query)
            }
            Intent.TRANSPORT_INFO -> {
                val type = decision.entities["parameter"] ?: normalizedText
                LogPoseCommand.TransportInfo(type)
            }

            Intent.WEATHER -> LogPoseCommand.System.GetWeather

            // --- DIAGNÓSTICO DE MOTO ---
            Intent.VEHICLE_STATUS -> {
                if (normalizedText.contains("gustos") || normalizedText.contains("preferencias")) {
                    val favs = com.uriel.logpose.thamis.learning.LearningEngine.getLearnedMusicEntities().take(5).joinToString(", ")
                    LogPoseCommand.Feedback("Tus artistas Staff con prioridad son: $favs")
                } else {
                    LogPoseCommand.System.GetVehicleStatus
                }
            }
            Intent.FUEL_LEVEL -> LogPoseCommand.System.GetFuelLevel
            Intent.MAINTENANCE_INFO -> LogPoseCommand.System.GetMaintenanceInfo
            Intent.ENGINE_TEMP -> LogPoseCommand.System.GetEngineTemp

            // --- SEGURIDAD PROACTIVA Y UI ---
            Intent.RECORD_INCIDENT -> LogPoseCommand.System.RecordIncident
            Intent.TOGGLE_HUD -> {
                val isHide = normalizedText.contains("ocultar") || normalizedText.contains("saca") || normalizedText.contains("sacá")
                LogPoseCommand.System.ToggleHud(!isHide)
            }

            else -> {
                // v80.0 STAFF: Bucle de Aprendizaje Soberano (Offline-First)
                // Ya no llama a la PC. El aprendizaje se delega al OrganicLearningManager 
                // que ahora decide si registrar local o esperar al Lab.
                if (normalizedText.length > 3) {
                    com.uriel.logpose.thamis.learning.OrganicLearningManager.requestRuleSynthesis(originalText)
                }
                LogPoseCommand.Unknown
            }
        }
    }

    private fun limpiarArticulosYConectores(target: String): String {
        var texto = com.uriel.logpose.core.services.MusicNormalizerDelegator.normalize(target).trim()
        
        // v112.0: Filtro de etiquetas de motores (Anti-Contaminación)
        val acousticGarbage = setOf(
            "is", "que", "y estan", "y están", "estan", "están", "viste", "che", 
            "fue", "jueguitos", "jueguito", "blog", "ploc", "vosk", "whisper", "log"
        )
        texto = texto.split(" ").filter { it !in acousticGarbage }.joinToString(" ")

        var huboCambio: Boolean
        
        do {
            huboCambio = false
            val matcher = ARTICULOS_CONECTORES_REGEX.matcher(texto)
            if (matcher.find()) {
                val match = matcher.group()
                if (texto.length > match.length && texto[match.length] == ' ') {
                    texto = texto.substring(matcher.end()).trim()
                    huboCambio = true
                } else if (texto.length == match.length) {
                    texto = ""
                    huboCambio = false
                }
            }
        } while (huboCambio)
        
        return texto
    }

    fun procesarJsonDeGemini(context: Context, jsonString: String): LogPoseCommand {
        return try {
            val json = org.json.JSONObject(jsonString)
            val action = json.optString("action", "")

            when (action) {
                "MEDIA_PLAY" -> {
                    val target = json.optString("target", "")
                    if (target.isNotBlank()) {
                        LogPoseCommand.Media.PlayMusic(target)
                    } else {
                        LogPoseCommand.Feedback("¿Qué querés escuchar?")
                    }
                }
                "NAVIGATE" -> {
                    val location = json.optString("location", "")
                    LogPoseCommand.Navigation.Navigate(location)
                }
                "MEDIA_CONTROL" -> {
                    when (json.optString("command", "").uppercase()) {
                        "PAUSE" -> LogPoseCommand.Media.PauseMusic
                        "NEXT" -> LogPoseCommand.Media.NextTrack
                        "PREVIOUS" -> LogPoseCommand.Media.PreviousTrack
                        else -> LogPoseCommand.Unknown
                    }
                }
                "OPEN_APP" -> {
                    val packageName = json.optString("package", "")
                    LogPoseCommand.OpenApp(packageName)
                }
                "IGNORE" -> {
                    LogPoseLogger.d("ActionMapper", "Audio clasificado como ruido ambiental por Gemini.")
                    LogPoseCommand.Unknown
                }
                else -> LogPoseCommand.Unknown
            }
        } catch (e: Exception) {
            LogPoseLogger.e("ActionMapper", "Error al parsear JSON de Gemini: ${e.message}")
            LogPoseCommand.Unknown
        }
    }

    fun ejecutarAccionDirecta(context: Context, jsonString: String) {
        val command = procesarJsonDeGemini(context, jsonString)
        if (command !is LogPoseCommand.Unknown) {
            com.uriel.logpose.core.services.CommandExecutorDelegator.execute(command)
        }
    }
}
