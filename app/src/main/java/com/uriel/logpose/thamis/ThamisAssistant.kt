package com.uriel.logpose.thamis

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.parser.UdpSender
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.language.LanguageProcessor
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.core.services.AlertManager
import com.uriel.logpose.core.services.FlightRecorder
import com.uriel.logpose.features.voice.FeedbackManager
import com.uriel.logpose.thamis.context.THAMISContext
import com.uriel.logpose.thamis.voice.filter.VoiceActivationGate
import com.uriel.logpose.thamis.orchestrator.SystemOrchestrator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * THAMIS Assistant: La fusión definitiva entre Vosk (Oído) y THAMIS (Cerebro).
 */
object ThamisAssistant {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isListening = false
    private var processingJob: Job? = null
    
    private val voiceGate = VoiceActivationGate()

    private var parser: CommandParser? = null
    private var udpSender: UdpSender? = null

    private val _lastResult = MutableSharedFlow<VoskVoiceEngine.RecognizedCommand>(replay = 1)
    val lastResult = _lastResult.asSharedFlow()

    fun start(context: Context) {
        if (isListening) return
        isListening = true

        val glosario = PhoneticDictionary(context)
        parser = CommandParser(glosario)
        val pcIp = AppContainer.settingsManager.getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
        udpSender = UdpSender(pcIp)

        LogPoseLogger.i("🧠 THAMIS: Sistema sincronizado con PC ($pcIp)")
        
        val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context)

        MusicManager.duck()
        core.ready()

        AppContainer.voskEngine.start()

        processingJob?.cancel()
        processingJob = scope.launch {
            AppContainer.voskEngine.recognizedCommands.collect { recognized ->
                processText(recognized.text, recognized.confidence, recognized.durationMs)
            }
        }
    }

    private fun processText(rawText: String, confidence: Float, durationMs: Long = 0) {
        val noise = VoskVoiceEngine.getAmbientNoiseLevel()
        
        if (!voiceGate.shouldProcess(rawText, confidence, noise, durationMs)) {
            LogPoseLogger.d("👂 THAMIS: Ignorando por Gate (Ruido: $noise, Conf: $confidence, Dur: ${durationMs}ms)")
            return
        }

        LogPoseLogger.d("👂 THAMIS escuchó: '$rawText' (Conf: $confidence, Noise: $noise)")
        
        val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(AppContainer.appContext)
        val currentState = core.getState()
        
        if (currentState == com.uriel.logpose.core.compat.core.AppState.WAITING_SEND_CONFIRMATION) {
            handleConfirmation(rawText)
            return
        }

        scope.launch {
            _lastResult.emit(VoskVoiceEngine.RecognizedCommand(rawText, confidence))
        }

        val filterResult = com.uriel.logpose.thamis.decision.SurvivalFilter.evaluate(
            VoskVoiceEngine.RecognizedCommand(rawText, confidence)
        )

        when (filterResult) {
            is com.uriel.logpose.thamis.decision.FilterResult.Confirmed -> {
                LogPoseLogger.i("🧠 THAMIS: Comando core confirmado.")
                FlightRecorder.logCommand(rawText, success = true)
                AlertManager.beep()
                SystemOrchestrator.dispatchCompatCommand(filterResult.command)
                return
            }
            is com.uriel.logpose.thamis.decision.FilterResult.TooUncertain -> {
                LogPoseLogger.w("🧠 THAMIS: Dudoso. Ruido alto.")
                FlightRecorder.logCommand(rawText, success = false)
                if (noise > 0.7f) {
                    AlertManager.enqueue("Uriel, bajá la visera que hay mucho viento.", com.uriel.logpose.core.services.AlertPriority.NORMAL)
                } else {
                    AlertManager.beep() 
                }
                return
            }
            else -> {}
        }

        val fastResult = com.uriel.logpose.core.parser.FastParser.parse(rawText)
        if (fastResult is com.uriel.logpose.core.parser.ParseResult.Success) {
            val cmd = fastResult.command
            SystemOrchestrator.dispatchCompatCommand(cmd)
            return
        }

        val cleanText = LanguageProcessor.process(rawText)
        if (cleanText.isBlank()) return

        scope.launch {
            val request = THAMISRequest(text = cleanText, speechConfidence = confidence)
            
            // FASE: CEREBRO HÍBRIDO (Gratis)
            // Usamos ThamisBrain que decide si procesar local o pedir ayuda a la PC/Nube
            val decision = com.uriel.logpose.thamis.intelligence.ThamisBrain.process(request)

            LogPoseLogger.i("🧠 THAMIS Decisión: ${decision.intent} (Confianza: ${decision.confidence})")

            val command = ActionMapper.map(decision, cleanText)
            SystemOrchestrator.dispatchCompatCommand(command)
        }
    }

    private fun handleConfirmation(text: String) {
        val normalized = com.uriel.logpose.thamis.language.PhoneticEngine.normalize(text)
        LogPoseLogger.d("🧠 THAMIS Diálogo: Procesando confirmación -> '$normalized'")

        if (normalized.contains("sí") || normalized.contains("dale") || normalized.contains("manda")) {
            FeedbackManager.speak("Listo, mensaje enviado.")
            val lastCmd = com.uriel.logpose.core.memory.CommandMemory.last()?.command
            if (lastCmd is com.uriel.logpose.core.compat.core.Command.SendMessage) {
                sendDirectWhatsApp(lastCmd.contact, lastCmd.message)
            }
        } else if (normalized.contains("no") || normalized.contains("borra") || normalized.contains("cancela")) {
            FeedbackManager.speak("Dale, lo borro.")
        } else {
            FeedbackManager.speak("No te entendí bien, ¿lo mando o lo borro?")
            return 
        }

        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(AppContainer.appContext).ready()
    }

    private fun sendDirectWhatsApp(contact: String, message: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("whatsapp://send?text=${android.net.Uri.encode(message)}")
            setPackage("com.whatsapp")
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            val context = AppContainer.appContext
            val trampolineIntent = android.content.Intent(context, com.uriel.logpose.core.app.TrampolineActivity::class.java).apply {
                putExtra("TARGET_INTENT", intent)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(trampolineIntent)
        } catch (e: Exception) {}
    }

    fun stop() {
        LogPoseLogger.i("🧠 THAMIS: Entrando en modo reposo.")
        isListening = false
        processingJob?.cancel()
        AppContainer.voskEngine.stop()
        MusicManager.unduck()
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(AppContainer.appContext).shutdown()
    }
}
