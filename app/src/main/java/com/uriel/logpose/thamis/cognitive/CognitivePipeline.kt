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

import android.content.Context
import com.uriel.logpose.thamis.voice.cloud.CloudSpeechService
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import com.thamis.lab.core.common.speech.SpeechResult
import com.thamis.lab.core.common.speech.AudioQualityReport

import com.uriel.logpose.features.voice.FeedbackManager

import com.uriel.logpose.core.intelligence.MotherbaseBridge

/**
 * COGNITIVE PIPELINE CP-3.1 (Singularity Staff): PC Motherbase Handover.
 * v73.0: PC Agent acts as Tier 0 (Supreme) Reasoning.
 */
object CognitivePipeline {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var lastUsedIp: String? = null
    private var _udpSender: com.uriel.logpose.core.parser.UdpSender? = null
    private val telemetrySequence = AtomicLong(0)
    private val telemetryQueue = java.util.concurrent.ConcurrentLinkedQueue<JSONObject>()
    private val queueMutex = Mutex()
    private var isLowPowerMode = false

    init {
        // v72.0: Flush de Telemetría (Ciclo de 30s para radio-silencio)
        scope.launch {
            while (isActive) {
                delay(30000L)
                flushTelemetry()
            }
        }
    }

    private suspend fun flushTelemetry() {
        val discoveredIp = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value ?: return
        
        while (telemetryQueue.isNotEmpty()) {
            val trace = telemetryQueue.poll() ?: break
            sendTelemetryImmediate(trace)
        }
    }

    private fun sendTelemetryImmediate(trace: JSONObject) {
        val rawText = trace.optString("final_clean_text", "empty_payload")
        val encryptedText = com.uriel.logpose.core.network.NetworkConfig.encryptPayload(rawText)
        
        trace.put("payload_enc", encryptedText)
        trace.remove("final_clean_text") 
        
        val currentSeq = telemetrySequence.getAndIncrement()
        val signature = com.uriel.logpose.core.network.NetworkConfig.generateStaffSignature("$encryptedText:$currentSeq")
        
        trace.put("staff_sig", signature)
        trace.put("seq", currentSeq)
        
        getUdpSender()?.enviarJson(trace.toString())
    }

    private fun getUdpSender(): com.uriel.logpose.core.parser.UdpSender? {
        // v89.0: Descubrimiento Dinámico Staff Puro (Sin IP hardcodeada)
        val discoveredIp = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value
        
        if (discoveredIp == null) {
            if (System.currentTimeMillis() % 10000 < 100) {
                LogPoseLogger.d("Pipeline", "Esperando descubrimiento del Lab...")
            }
            return null
        }
        
        if (discoveredIp != lastUsedIp) {
            LogPoseLogger.i("Pipeline: ⚡ Conexión Lab (AUTO) -> $discoveredIp:5055")
            
            // v75.0: Liberación de socket anterior para evitar FD leaks
            _udpSender?.close()
            
            lastUsedIp = discoveredIp
            _udpSender = com.uriel.logpose.core.parser.UdpSender(discoveredIp, pcPort = 5055)
        }
        
        return _udpSender
    }

    private val musicTriggers = setOf("pone", "poneme", "poné", "poner", "reproduce", "reproduci", "reproducir", "escuchar", "play", "pasame", "sonar", "tira", "tirame")

    suspend fun process(
        rawText: String, 
        speechConfidence: Float, 
        noiseLevel: Float, 
        preDetected: com.uriel.logpose.thamis.intent.IntentDetector.DetectionResult? = null
    ) {
        val pipelineStart = System.currentTimeMillis()

        // 0. MULTI-COMMAND DETECTION
        if (com.uriel.logpose.core.parser.multicommand.MultiCommandParser.isMultiCommand(rawText)) {
            val multi = com.uriel.logpose.core.parser.multicommand.MultiCommandParser.parse(rawText)
            multi.command.commands.forEach { cmdText ->
                process(cmdText, speechConfidence, noiseLevel)
            }
            return
        }

        // Misión #089: Refinado de Short-Circuit (Xiaomi HyperOS Stability)
        // Solo permitimos ejecución directa si la confianza es absoluta (>98%) y no es música.
        val isExtremelyHighConfidence = speechConfidence > 0.98f
        val isMusicVerb = extractTriggerVerb(rawText) in musicTriggers

        if ((isExtremelyHighConfidence && !isMusicVerb) || preDetected != null) {
            LogPoseLogger.i("Pipeline", "⚡ Confianza Staff Alta. Ejecución directa (Short-Circuit).")
            executePipeline(rawText, speechConfidence, pipelineStart, noiseLevel = noiseLevel, preDetected = preDetected, confidenceReport = null)
            return
        }

        executePipeline(rawText, speechConfidence, pipelineStart, noiseLevel = noiseLevel, confidenceReport = null)
    }

    // --- AUTO-REPARACIÓN: CIRCUIT BREAKER v10.1 ---
    @Volatile private var isCloudCircuitBreakerOpen = false
    private var consecutiveCloudFailures = 0
    private const val MAX_ALLOWED_FAILURES = 3
    private const val CIRCUIT_BREAKER_COOLDOWN_MS = 60000L

    private fun triggerSelfHealingCooldown() {
        scope.launch {
            LogPoseLogger.e("Pipeline", "🚨 CIRCUITO CLOUD EN COOLDOWN (60s). Auto-reparación activa: 100% Sherpa local.")
            delay(CIRCUIT_BREAKER_COOLDOWN_MS)
            isCloudCircuitBreakerOpen = false
            consecutiveCloudFailures = 0
            LogPoseLogger.i("Pipeline", "🔧 Cooldown finalizado. Reconectando canal Cloud.")
        }
    }

    /**
     * v77.0 STAFF: Implementación de CASCADA ESTRICTA - Nivel 2.
     * Procesa una ráfaga de audio con motores de alta precisión.
     */
    suspend fun processWithAudio(pcmData: ShortArray, voskText: String, noiseLevel: Float) {
        val pipelineStart = System.currentTimeMillis()
        LogPoseLogger.i("Pipeline", "Iniciando Cascada Nivel 2 (Ráfaga HP) | Ruido: $noiseLevel")
        
        com.uriel.logpose.core.services.LogPoseHudService.updateStatus("🧠 PROCESANDO...")

        // 2. STT de Alta Precisión (Solo ejecutamos lo necesario)
        val sttResults = mutableListOf<SpeechResult>()
        
        coroutineScope {
            // Motor A: Whisper Pro (Local si existe)
            val whisperTask = async {
                val t0 = System.currentTimeMillis()
                val whisperEngine = com.uriel.logpose.core.app.LogPoseApplication.instance.whisperEngine
                val res = whisperEngine.transcribe(pcmData)
                val text = res.getOrNull()?.text ?: ""
                val confidence = if (text.isBlank()) 0.0f else 0.98f
                SpeechResult(text, confidence, System.currentTimeMillis() - t0, "Whisper-Pro")
            }

            // Motor B: Sherpa-ONNX (Local - Siempre disponible)
            val sherpaTask = async {
                val t0 = System.currentTimeMillis()
                val text = com.uriel.logpose.core.app.LogPoseApplication.instance.sherpaEngine.transcribeShortArray(pcmData)
                SpeechResult(text, 0.92f, System.currentTimeMillis() - t0, "Sherpa-ONNX")
            }

            sttResults.add(whisperTask.await())
            sttResults.add(sherpaTask.await())
        }

        // 3. Audio Quality Analysis
        val qualityReport = AudioQualityReport(
            snr = if (noiseLevel < 0.2f) 10.0 else 5.0, // Simplificado para demo
            noiseLevel = noiseLevel,
            isSpeechDetected = true, // VAD previo garantizado
            durationMs = (pcmData.size / 16.0).toLong()
        )

        // 4. Consensus Evaluation
        val confidenceReport = ConfidenceEngine.evaluateConsensus(sttResults, qualityReport)
        
        LogPoseLogger.i("Pipeline", "Consenso alcanzado: ${confidenceReport.isCommandValid}. Rationale: ${confidenceReport.rationale}")

        if (!confidenceReport.isCommandValid) {
            LogPoseLogger.w("Pipeline", "🚨 COMANDO BLOQUEADO: Baja confianza o conflicto entre motores.")
            com.uriel.logpose.core.services.LogPoseHudService.updateStatus("⚠️ NO ENTENDÍ")
            FeedbackManager.speak("No entendí la orden, ¿puedes repetirla?")
            
            // Forensic Logging
            recordForensicError(sttResults, qualityReport, confidenceReport)
            return
        }

        // 5. Intent Validation
        val winningText = sttResults.maxByOrNull { it.confidence }?.text ?: voskText
        val cleanWinningText = sanitizeTranscription(winningText)
        
        executePipeline(cleanWinningText, confidenceReport.finalConfidence, pipelineStart, 
            noiseLevel = noiseLevel, 
            resolutionSource = "CONSENSUS_V70",
            bypassWakeWordCheck = true,
            confidenceReport = confidenceReport
        )
    }

    private fun recordForensicError(
        results: List<SpeechResult>,
        quality: AudioQualityReport,
        report: ConfidenceEngine.ConfidenceReport
    ) {
        val forensic = JSONObject().apply {
            put("type", "FORENSIC_ERROR")
            put("timestamp", System.currentTimeMillis())
            put("engines", results.map { it.engineName })
            put("transcripts", results.map { it.text })
            put("confidences", results.map { it.confidence })
            put("snr", quality.snr)
            put("rationale", report.rationale)
        }
        com.uriel.logpose.core.forensic.ForensicVault.recordFailure(forensic)
        sendTelemetryImmediate(forensic)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = LogPoseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actv = connectivityManager.getNetworkCapabilities(network) ?: return false
        return actv.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
               actv.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    private fun pcmToByteArray(pcm: ShortArray): ByteArray {
        val buffer = java.nio.ByteBuffer.allocate(pcm.size * 2).order(java.nio.ByteOrder.LITTLE_ENDIAN)
        buffer.asShortBuffer().put(pcm)
        return buffer.array()
    }

    private fun extractTriggerVerb(text: String): String? {
        val clean = com.uriel.logpose.thamis.language.LanguageProcessor.process(text).lowercase()
        val words = clean.trim().split(" ")
        // v98.0: Escaneo STAFF de toda la frase para detectar el verbo de acción en cualquier posición
        return words.find { it in musicTriggers || it.startsWith("llam") || it.startsWith("vam") || it.startsWith("abri") }
    }

    private suspend fun executePipeline(
        text: String, 
        speechConfidence: Float, 
        pipelineStart: Long,
        noiseLevel: Float = 0.0f,
        forcedIntent: Intent? = null,
        resolutionSource: String = "VOSK_DIRECT",
        preDetected: com.uriel.logpose.thamis.intent.IntentDetector.DetectionResult? = null,
        bypassWakeWordCheck: Boolean = false,
        confidenceReport: ConfidenceEngine.ConfidenceReport? = null
    ) {
        val isLabsOnline = com.uriel.logpose.core.parser.LabDiscoveryService.isBridgeOnline()

        try {
            // 1. NORMALIZACIÓN (Bypass si es un texto de fusión VOSK|WHISPER)
            val isFusionText = text.contains("VOSK:") && text.contains("WHISPER:")
            val cleanText = if (preDetected == null && !isFusionText) {
                com.uriel.logpose.thamis.language.PhoneticEngine.normalize(text)
            } else text 

            // 2. CONTEXT SNAPSHOT

            // 3. INFERENCIA DE INTENCIÓN (Bypass si tenemos preDetected)
            val decision = when {
                preDetected != null -> {
                    com.uriel.logpose.thamis.decision.Decision(
                        preDetected.intent, 
                        preDetected.score, 
                        preDetected.entities,
                        intentUri = preDetected.intentUri
                    )
                }
                forcedIntent != null -> {
                    val entities = mutableMapOf<String, String>()
                    if (forcedIntent == Intent.PLAY_MUSIC) {
                        entities["media"] = cleanMusicPayload(cleanText)
                    }
                    com.uriel.logpose.thamis.decision.Decision(forcedIntent, 1.0f, entities)
                }
                bypassWakeWordCheck -> {
                    // v77.0 STAFF: Prioridad LOCAL (Gemma/Llama) para modo Producto.
                    LogPoseLogger.i("Pipeline", "🧠 Consultando Juez Final (Gemma) para audio procesado...")
                    val llmDecision = com.uriel.logpose.core.intelligence.llm.LLMDecisionEngine.think(cleanText)
                    
                    if (llmDecision != null && llmDecision.intent != Intent.UNKNOWN) {
                        LogPoseLogger.i("Pipeline", "✅ Gemma resolvió: ${llmDecision.intent}")
                        llmDecision
                    } else if (isLabsOnline) {
                        // Solo consultamos a la PC si el local falló y estamos en el Lab.
                        val pcDecision = MotherbaseBridge.queryReasoning(cleanText)
                        if (pcDecision != null) {
                            LogPoseLogger.i("Pipeline", "✅ PC Bridge resolvió fallback: ${pcDecision.optString("claude")}")
                            val intent = Intent.valueOf(pcDecision.optString("claude", "UNKNOWN"))
                            val entities = mutableMapOf<String, String>()
                            entities["parameter"] = pcDecision.optString("entity", "")
                            if (intent == Intent.PLAY_MUSIC) entities["media"] = entities["parameter"] ?: ""
                            com.uriel.logpose.thamis.decision.Decision(intent, 1.0f, entities, fromAi = true)
                        } else {
                            val detection = com.uriel.logpose.thamis.intent.IntentDetector.detect(cleanText, ignoreWakeWord = true)
                            com.uriel.logpose.thamis.decision.Decision(detection.intent, detection.score, detection.entities)
                        }
                    } else {
                        // Fallback a reglas (Safety Net)
                        val detection = com.uriel.logpose.thamis.intent.IntentDetector.detect(cleanText, ignoreWakeWord = true)
                        com.uriel.logpose.thamis.decision.Decision(detection.intent, detection.score, detection.entities)
                    }
                }
                else -> {
                    val request = THAMISRequest(text = cleanText, speechConfidence = speechConfidence)
                    val initialDecision = ThamisBrain.process(request)
                    
                    // v100.0: APOYO NEURONAL STAFF (Llama 3.2 / Gemma Backup)
                    if (initialDecision.intent == Intent.UNKNOWN || initialDecision.confidence < 0.70f) {
                        LogPoseLogger.i("Pipeline", "🧠 Motor de reglas duda. Consultando a Gemma...")
                        val llmDecision = com.uriel.logpose.core.intelligence.llm.LLMDecisionEngine.think(cleanText)
                        if (llmDecision != null && llmDecision.intent != Intent.UNKNOWN) {
                            LogPoseLogger.i("Pipeline", "✅ Gemma resolvió la duda: ${llmDecision.intent}")
                            llmDecision
                        } else initialDecision
                    } else initialDecision
                }
            }

            // v70.0 Safety Gate: Final validation before mapping
            confidenceReport?.let { report ->
                if (!ConfidenceEngine.isSafeToExecute(decision, report)) {
                    LogPoseLogger.w("Pipeline", "🚨 GOVERNANCE_REJECT: Acción bloqueada por el Confidence Engine. Rationale: ${report.rationale}")
                    
                    com.uriel.logpose.core.services.LogPoseHudService.updateStatus("⚠️ REINTENTAR")
                    FeedbackManager.speak("No entendí bien, ¿podés repetir?")
                    return
                }
            }

            // v67.1: Misión LAB - Reportamos UNKNOWN para que el GHOST MONITOR marque errores
            if (decision.intent == Intent.UNKNOWN || decision.confidence == 0.0f) {
                if (isLabsOnline) {
                    LogPoseLogger.d("Pipeline: UNKNOWN - Reportando a LAB para diagnóstico.")
                    val totalLatency = System.currentTimeMillis() - pipelineStart
                    val trace = JSONObject().apply {
                        put("type", "COGNITIVE_PIPELINE_TRACE")
                        put("total_latency_ms", totalLatency)
                        put("detected_intent", decision.intent.name)
                        put("final_clean_text", cleanText)
                        put("failure_level", 3)
                        put("noise_level", noiseLevel)
                        put("confidence", speechConfidence)
                        put("battery", WorldModelEngine.getCurrentSnapshot().systems.device.batteryPct)
                        put("speed", WorldModelEngine.getCurrentSnapshot().vehicle.speedKmh)
                    }
                    sendTelemetry(trace)
                }
                return
            }

            // Guardrail: Prevenir payloads vacíos a Spotify
            if (decision.intent == Intent.PLAY_MUSIC && decision.entities["media"]?.isBlank() == true) {
                LogPoseLogger.w("Pipeline: Payload de música vacío. Abortando despacho.")
                return
            }

            // 4. MAPEADOR DE ACCIONES (Optimizado v56.0 con Guardián de Seguridad)
            val command = ActionMapper.map(decision, cleanText, confidenceReport)

            // 5. DESPACHO ULTRA-RÁPIDO (Prioridad RIDER)
            // v69.0: Desacople total del Main Thread para evitar que MIUI Scout mate la app
            scope.launch(Dispatchers.Default) {
                SystemOrchestrator.dispatchCompatCommand(command)
            }
            
            // 6. TAREAS DE FONDO (Async)
            scope.launch(Dispatchers.IO) {
                // Ingesta de simulador (Mantenimiento de madurez)
                com.uriel.logpose.core.intelligence.NeuroEvolutionSimulator.addRealWorldFailure(cleanText, decision.intent)

                // Limpieza de HUD tras éxito
                delay(2000)
                com.uriel.logpose.core.services.LogPoseHudService.showDebug(null)
                com.uriel.logpose.core.services.LogPoseHudService.updateStatus("THAMIS: LISTO")
            }
            
            val totalLatency = System.currentTimeMillis() - pipelineStart
            LogPoseLogger.i("🧠 [PerfAudit] Pipeline completado en ${totalLatency}ms | Intent: ${decision.intent}")

            // 7. TELEMETRÍA (Low Priority)
            if (isLabsOnline) {
                scope.launch {
                    val snapshot = WorldModelEngine.getCurrentSnapshot()
                    val trace = JSONObject().apply {
                        put("type", "COGNITIVE_PIPELINE_TRACE")
                        put("total_latency_ms", totalLatency)
                        put("detected_intent", decision.intent.name)
                        put("final_clean_text", cleanText)
                        put("resolution_source", resolutionSource)
                        put("failure_level", 0)
                        put("noise_level", noiseLevel)
                        put("confidence", speechConfidence)
                        put("battery", snapshot.systems.device.batteryPct)
                        put("speed", snapshot.vehicle.speedKmh)
                        try {
                            put("lat", snapshot.systems.navigation.accuracyMeters)
                        } catch (_: Exception) {}
                    }
                    sendTelemetry(trace)
                }
            }

        } catch (e: Exception) {
            LogPoseLogger.e("🧠 [PerfAudit] Error: ${e.message}")
        }
    }

    private fun cleanMusicPayload(text: String): String {
        return text.lowercase()
            .replace(Regex("(?i)^(log|logpose|hugo|jugo|che|hey|oye|pone|poneme|reproduce|reproducir|play|pasame|escuchar)\\b\\s*"), "")
            .replace(Regex("(?i)^(la|el|un|una)\\b\\s*"), "")
            .replace(Regex("(?i)\\s*\\b(de|con|en|por|a)$"), "")
            .trim()
    }

    private fun sanitizeTranscription(text: String): String {
        // v8.0: Filtro de muletillas Rioplatense Pro y Limpieza Staff
        val fillers = setOf("eh", "ehhh", "mmm", "estee", "este", "viste", "che", "bueno", "ponele", "nada", "posta", "viste", "okay", "ok")
        
        val words = text.lowercase()
            .replace(Regex("[^a-z0-9ñáéíóú ]"), " ") 
            .split(Regex("\\s+"))
            .asSequence()
            .filter { it.isNotBlank() && it !in fillers }
            // v99.5: STAFF FIX - Eliminamos .distinct() para permitir temas con repeticiones como "3 y 33"

        return words.joinToString(" ").trim()
    }

    fun sendTelemetryProxy(trace: JSONObject) {
        sendTelemetry(trace)
    }

    fun setLowPowerMode(enabled: Boolean) {
        this.isLowPowerMode = enabled
        LogPoseLogger.i("Pipeline: Modo ahorro energético: $enabled")
    }

    private fun sendTelemetry(trace: JSONObject) {
        // v78.0: Protección de Memoria y CPU (Reducción de GC Churn Staff)
        val type = trace.optString("type")
        
        // Si estamos en ahorro o el Labs no está presente, abortamos ráfaga no crítica
        if (isLowPowerMode || !com.uriel.logpose.core.parser.LabDiscoveryService.isBridgeOnline()) {
            if (type != "COGNITIVE_PIPELINE_TRACE") return
        }

        if (type == "COGNITIVE_PIPELINE_TRACE" || type == "HEARTBEAT") {
            sendTelemetryImmediate(trace)
        } else {
            if (telemetryQueue.size < 50) {
                telemetryQueue.add(trace)
            }
        }
    }
}
