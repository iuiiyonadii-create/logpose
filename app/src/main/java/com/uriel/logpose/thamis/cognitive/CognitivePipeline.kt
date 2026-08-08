package com.uriel.logpose.thamis.cognitive

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.intelligence.ThamisBrain
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.language.LanguageProcessor
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.orchestrator.SystemOrchestrator
import com.uriel.logpose.core.parser.UdpSender
import com.uriel.logpose.core.app.LogPoseApplication
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * COGNITIVE PIPELINE CP-2.0 (Misión #012): Asíncrono y No-Bloqueante.
 * Actualizado v4.6: Soporte para Handover de Audio PCM (Whisper Bridge).
 */
object CognitivePipeline {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var lastUsedIp: String? = null
    private var _udpSender: com.uriel.logpose.core.parser.UdpSender? = null

    private fun getUdpSender(): com.uriel.logpose.core.parser.UdpSender? {
        val currentIp = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value
        
        if (currentIp == null) {
            val staticIp = LogPoseApplication.entryPoint.settingsManager().getString("pc_ip", "192.168.1.34") ?: "192.168.1.34"
            if (_udpSender == null || lastUsedIp != staticIp) {
                LogPoseLogger.w("Pipeline: Usando IP de FALLBACK (Discovery inactivo) -> $staticIp")
                lastUsedIp = staticIp
                _udpSender = com.uriel.logpose.core.parser.UdpSender(staticIp)
            }
        } else if (currentIp != lastUsedIp) {
            LogPoseLogger.i("Pipeline: ⚡ Cambiando a IP descubierta por LabDiscovery -> $currentIp")
            lastUsedIp = currentIp
            _udpSender = com.uriel.logpose.core.parser.UdpSender(currentIp)
        }
        
        return _udpSender
    }

    private val musicTriggers = setOf("pone", "poneme", "poné", "poner", "reproduce", "reproduci", "reproducir", "escuchar", "play", "pasame", "sonar", "tira", "tirame")

    suspend fun process(rawText: String, speechConfidence: Float, noiseLevel: Float) {
        val pipelineStart = System.currentTimeMillis()

        // 0. MULTI-COMMAND DETECTION
        if (com.uriel.logpose.core.parser.multicommand.MultiCommandParser.isMultiCommand(rawText)) {
            val multi = com.uriel.logpose.core.parser.multicommand.MultiCommandParser.parse(rawText)
            multi.command.commands.forEach { cmdText ->
                process(cmdText, speechConfidence, noiseLevel)
            }
            return
        }

        executePipeline(rawText, speechConfidence, pipelineStart)
    }

    /**
     * Misión #022.3: Pipeline de Alta Precisión con Handover de Audio.
     */
    suspend fun processWithAudio(pcmData: ShortArray, voskText: String, noiseLevel: Float) {
        val pipelineStart = System.currentTimeMillis()
        LogPoseLogger.i("Pipeline: Iniciando procesamiento de audio (Handover v4.6)")
        
        com.uriel.logpose.core.services.LogPoseHudService.updateStatus("🧠 ANALIZANDO...")

        val whisperText = com.uriel.logpose.core.engine.HighPrecisionTranscriber.transcribeAudio(pcmData)
        
        // v5.5: Sanitización de eco y repeticiones
        val cleanWhisperText = whisperText?.let { sanitizeTranscription(it) }

        // Misión #022.3: Lógica de Guardraila forzada
        val triggerVerb = extractTriggerVerb(voskText)
        val isMusicVerb = triggerVerb != null && triggerVerb in musicTriggers
        
        if (cleanWhisperText != null && cleanWhisperText.isNotBlank()) {
            LogPoseLogger.i("Pipeline: Handover exitoso. Sherpa oyó: '$cleanWhisperText'")
            
            // v6.2: Modo Debug en HUD
            com.uriel.logpose.core.services.LogPoseHudService.showDebug("Sherpa: $cleanWhisperText")

            // v5.6: Snap-to-Glossary (Corrección fonética local)
            val musicMatch = if (isMusicVerb) {
                com.uriel.logpose.features.voice.MusicVocabulary.findBestMatch(cleanWhisperText, 0.85)
            } else null

            val musicResolution = musicMatch?.first ?: cleanWhisperText
            
            if (musicMatch != null) {
                LogPoseLogger.d("Pipeline: Match Musical [Score: ${musicMatch.second}] -> ${musicMatch.first}")
            }

            if (isMusicVerb && musicResolution != cleanWhisperText) {
                LogPoseLogger.i("Pipeline: Corrección Staff aplicada -> '$cleanWhisperText' colapsado a '$musicResolution'")
                
                // v6.2: Modo Debug - Mostrar corrección en HUD
                com.uriel.logpose.core.services.LogPoseHudService.showDebug("$cleanWhisperText ➔ $musicResolution")

                // Misión #026: Guardrail de Confianza Staff (Solo aprender si es casi perfecto)
                // Usamos 1.0f por ser el path de Alta Precisión (Sherpa).
                com.uriel.logpose.thamis.learning.LearningEngine.learn(
                    cleanWhisperText, 
                    musicResolution, 
                    com.thamis.lab.core.contracts.intent.Intent.PLAY_MUSIC
                )
            }

            // Limpieza inmediata tras éxito
            LogPoseApplication.entryPoint.voskVoiceEngine().clearRollingBuffer()
            scope.launch {
                LogPoseApplication.instance.sherpaEngine.resetStream()
            }
            
            executePipeline(musicResolution, 1.0f, pipelineStart, forcedIntent = if (isMusicVerb) Intent.PLAY_MUSIC else null)
        } else {
            LogPoseLogger.w("Pipeline: Whisper falló. Reintentando con texto original de Vosk.")
            // Si es un verbo de música, forzamos la intención para evitar desvíos a OPEN_APP
            executePipeline(voskText, 0.5f, pipelineStart, forcedIntent = if (isMusicVerb) Intent.PLAY_MUSIC else null)
        }
    }

    private fun extractTriggerVerb(text: String): String? {
        val clean = com.uriel.logpose.thamis.language.LanguageProcessor.process(text).lowercase()
        val firstWord = clean.trim().split(" ").firstOrNull()
        return firstWord
    }

    private suspend fun executePipeline(
        text: String, 
        speechConfidence: Float, 
        pipelineStart: Long,
        forcedIntent: Intent? = null
    ) {
        val trace = JSONObject()
        val steps = org.json.JSONArray()

        try {
            // 1. NORMALIZACIÓN
            val t0 = System.currentTimeMillis()
            val normTrace = com.uriel.logpose.thamis.language.PhoneticEngine.normalizeWithTrace(text)
            val cleanText = normTrace.finalResult
            steps.put(createStepTrace("Normalization", t0, System.currentTimeMillis()))

            // 2. CONTEXT SNAPSHOT
            val t2 = System.currentTimeMillis()
            val snapshot = WorldModelEngine.getCurrentSnapshot()
            steps.put(createStepTrace("ContextSnapshot", t2, System.currentTimeMillis()))

            // 3. INFERENCIA DE INTENCIÓN
            val t4 = System.currentTimeMillis()
            val request = THAMISRequest(text = cleanText, speechConfidence = speechConfidence)
            
            val decision = if (forcedIntent != null) {
                val entities = mutableMapOf<String, String>()
                if (forcedIntent == Intent.PLAY_MUSIC) {
                    entities["media"] = cleanMusicPayload(cleanText)
                }
                com.uriel.logpose.thamis.decision.Decision(forcedIntent, 1.0f, entities)
            } else {
                ThamisBrain.process(request)
            }
            
            steps.put(createStepTrace("IntentInference", t4, System.currentTimeMillis()))

            // Guardrail: Prevenir payloads vacíos a Spotify
            if (decision.intent == Intent.PLAY_MUSIC && decision.entities["media"]?.isBlank() == true) {
                LogPoseLogger.w("Pipeline: Payload de música vacío. Abortando despacho.")
                return
            }

            // 4. MAPEADOR DE ACCIONES
            val t6 = System.currentTimeMillis()
            val command = ActionMapper.map(decision, cleanText)
            steps.put(createStepTrace("ActionMapping", t6, System.currentTimeMillis()))

            // 5. EJECUCIÓN ASÍNCRONA
            scope.launch {
                val execStart = System.currentTimeMillis()
                SystemOrchestrator.dispatchCompatCommand(command)
                LogPoseLogger.d("Pipeline: Ejecución asíncrona de ${command.javaClass.simpleName} completada en ${System.currentTimeMillis() - execStart}ms")
                
                // v6.2: Ocultar debug info tras 3 segundos de éxito
                delay(3000)
                com.uriel.logpose.core.services.LogPoseHudService.showDebug(null)
                com.uriel.logpose.core.services.LogPoseHudService.updateStatus("THAMIS: LISTO")
            }
            
            val totalLatency = System.currentTimeMillis() - pipelineStart
            LogPoseLogger.i("🧠 [PerfAudit] Pipeline principal completado en ${totalLatency}ms | Intent: ${decision.intent}")

            // 6. TELEMETRÍA (En segundo plano)
            scope.launch {
                trace.put("type", "COGNITIVE_PIPELINE_TRACE")
                trace.put("total_latency_ms", totalLatency)
                trace.put("detected_intent", decision.intent.name)
                trace.put("final_clean_text", cleanText)
                trace.put("perf_steps", steps)
                sendTelemetry(trace)
            }

        } catch (e: Exception) {
            LogPoseLogger.e("🧠 [PerfAudit] Error: ${e.message}")
        }
    }

    private fun cleanMusicPayload(text: String): String {
        return text.lowercase()
            .replace(Regex("(?i)^(pone|poneme|reproduce|reproducir|play|pasame|escuchar)\\s+"), "")
            .replace(Regex("(?i)^(la|el|un|una)\\s+"), "")
            .replace(Regex("(?i)\\s+(de|con|en|por|a)$"), "")
            .trim()
    }

    private fun sanitizeTranscription(text: String): String {
        // v8.0: Filtro de muletillas Rioplatense Pro y Limpieza Staff
        val fillers = setOf("eh", "ehhh", "mmm", "estee", "este", "viste", "che", "bueno", "ponele", "nada", "posta", "viste", "okay", "ok")
        
        val words = text.lowercase()
            .replace(Regex("[^a-z0-9ñáéíóú ]"), " ") 
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() && it !in fillers }
            .distinct() 

        return words.joinToString(" ").trim()
    }

    private fun createStepTrace(name: String, start: Long, end: Long): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("duration", end - start)
        }
    }

    fun sendTelemetryProxy(trace: JSONObject) {
        sendTelemetry(trace)
    }

    private fun sendTelemetry(trace: JSONObject) {
        getUdpSender()?.enviarJson(trace.toString())
    }
}
