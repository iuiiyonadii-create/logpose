package com.uriel.logpose.thamis

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.parser.UdpSender
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.decision.Decision
import com.thamis.lab.core.contracts.intent.Intent
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
        val pcIp = LogPoseApplication.entryPoint.settingsManager().getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
        udpSender = UdpSender(pcIp)

        LogPoseLogger.i("🧠 THAMIS: Sistema sincronizado con PC ($pcIp)")
        
        com.uriel.logpose.thamis.language.PhoneticEngine.syncWithLab()

        val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context)

        MusicManager.duck()
        core.ready()

        LogPoseApplication.entryPoint.voskVoiceEngine().start()

        processingJob?.cancel()
        processingJob = scope.launch {
            LogPoseApplication.entryPoint.voskVoiceEngine().recognizedCommands.collect { recognized ->
                processText(recognized.text, recognized.confidence, recognized.durationMs)
            }
        }
    }

    private fun processText(rawText: String, confidence: Float, durationMs: Long = 0) {
        val noise = VoskVoiceEngine.getAmbientNoiseLevel()
        
        if (!voiceGate.shouldProcess(rawText, confidence, noise, durationMs)) {
            LogPoseLogger.d("👂 THAMIS: Ignorando por Gate (Ruido: $noise, Conf: $confidence)")
            return
        }

        // ESTRATEGIA HÍBRIDA v4.6: DETERMINAR NECESIDAD DE HANDOVER
        val needsHighPrecision = isComplexRequest(rawText)

        scope.launch {
            _lastResult.emit(VoskVoiceEngine.RecognizedCommand(rawText, confidence))
            
            if (needsHighPrecision) {
                LogPoseLogger.i("🧠 THAMIS: Detectada frase compleja. Solicitando Handover de Audio...")
                val audioBuffer = LogPoseApplication.entryPoint.voskVoiceEngine().getRecentAudioBuffer()
                // Aquí el CognitivePipeline procesará el AUDIO en lugar del texto basura de Vosk
                com.uriel.logpose.thamis.cognitive.CognitivePipeline.processWithAudio(audioBuffer, rawText, noise)
            } else {
                com.uriel.logpose.thamis.cognitive.CognitivePipeline.process(rawText, confidence, noise)
            }
        }
    }

    private fun isComplexRequest(text: String): Boolean {
        val lower = text.lowercase()
        // v4.6.4: Set de disparadores Staff completo para Handover obligatorio
        val triggers = setOf(
            "pone", "poneme", "reproduce", "reproducir", "play", "pasame", "escuchar", "sonar", "tira", "tirame",
            "abri", "abrir", "abrí", "abre", "entra", "entrar", "lanzar",
            "llama", "llamar", "llamá", "manda", "mandale", "mandá", "mensaje", "enviar", "escribile",
            "buscame", "buscá", "busca", "ir a", "navegar"
        )
        return triggers.any { lower.contains(it) } || text.split(" ").size > 3
    }

    private fun handleConfirmation(text: String) {
        val normalized = com.uriel.logpose.thamis.language.PhoneticEngine.normalize(text)
        LogPoseLogger.d("🧠 THAMIS Diálogo: Procesando confirmación -> '$normalized'")

        if (normalized.contains("sí") || normalized.contains("dale") || normalized.contains("manda")) {
            FeedbackManager.speak("Listo, mensaje enviado.")
            val lastCmd = com.uriel.logpose.core.memory.CommandMemory.last()?.command
            if (lastCmd is com.thamis.lab.core.contracts.command.LogPoseCommand.SendMessage) {
                sendDirectWhatsApp(lastCmd.contact, lastCmd.message)
            }
        } else if (normalized.contains("no") || normalized.contains("borra") || normalized.contains("cancela")) {
            FeedbackManager.speak("Dale, lo borro.")
        } else {
            FeedbackManager.speak("No te entendí bien, ¿lo mando o lo borro?")
            return 
        }

        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(LogPoseApplication.instance).ready()
    }

    private fun sendDirectWhatsApp(contact: String, message: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("whatsapp://send?text=${android.net.Uri.encode(message)}")
            setPackage("com.whatsapp")
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            val context = LogPoseApplication.instance
            val trampolineIntent = android.content.Intent(context, com.uriel.logpose.core.app.TrampolineActivity::class.java).apply {
                putExtra("TARGET_INTENT", intent)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(trampolineIntent)
        } catch (e: Exception) {}
    }

    fun stop() {
        LogPoseLogger.i("🧠 THAMIS: Entrando en modo reposo. Liberando recursos pesados.")
        isListening = false
        processingJob?.cancel()
        
        // Misión #009: Optimización de RAM
        LogPoseApplication.entryPoint.voskVoiceEngine().releaseResources()

        MusicManager.unduck()
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(LogPoseApplication.instance).shutdown()
    }
}
