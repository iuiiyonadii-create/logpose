package com.uriel.logpose.features.voice

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.SpeechRecognizer
import android.media.AudioRecordingConfiguration
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.thamis.THAMIS
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.language.PhoneticEngine
import com.uriel.logpose.core.services.IntercomCaptureManager
import com.uriel.logpose.core.services.LogPoseCallService
import com.uriel.logpose.domain.repositories.VoiceRepository

/**
 * VoiceManager: Orquestador del flujo de voz con tolerancia fonética de alto nivel.
 */
object VoiceManager {

    private var voiceRepository: VoiceRepository? = null
    private var appContext: Context? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isStopping = false
    private var isPrivacyMode = false
    private var isPausedForExternalApp = false
    
    private var isQuickWindowActive = false
    private val quickWindowRunnable = Runnable { stop() }
    
    private val autoSleepRunnable = Runnable { 
        if (!isQuickWindowActive && !isTransitioningToMusic) {
            LogPoseLogger.i("VoiceManager: Auto-Sleep activado. Liberando ruteo SCO para A2DP.")
            stop() 
        }
    }

    fun initialize(context: Context, repository: VoiceRepository) {
        this.appContext = context.applicationContext
        this.voiceRepository = repository
        registerRecordingCallback()
    }

    private fun registerRecordingCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val audioManager = appContext?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.registerAudioRecordingCallback(object : AudioManager.AudioRecordingCallback() {
                override fun onRecordingConfigChanged(configs: List<AudioRecordingConfiguration>) {
                    val mySessionId = IntercomCaptureManager.getActiveSessionId()
                    val otherAppRecording = configs.any { config ->
                        config.clientAudioSessionId != mySessionId
                    }
                    if (otherAppRecording && !isPausedForExternalApp && !isStopping) {
                        LogPoseLogger.i("VoiceManager: Micrófono en uso por otra app. Cediendo control total.")
                        pauseForExternalApp(45000) 
                    }
                }
            }, handler)
        }
    }

    fun pauseForExternalApp(durationMs: Long) {
        isPausedForExternalApp = true
        stop()
        LogPoseCallService.instance?.yieldAudio()
        handler.removeCallbacksAndMessages("EXTERNAL_PAUSE")
        
        handler.postAtTime({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val audioManager = appContext?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                val mySessionId = IntercomCaptureManager.getActiveSessionId()
                val otherAppStillRecording = audioManager?.activeRecordingConfigurations?.any { 
                    it.clientAudioSessionId != mySessionId
                } ?: false
                if (otherAppStillRecording) {
                    LogPoseLogger.d("VoiceManager: Otra app sigue grabando. Extendiendo pausa 15s...")
                    pauseForExternalApp(15000)
                    return@postAtTime
                }
            }

            LogPoseLogger.i("VoiceManager: Fin de pausa externa. Reanudando...")
            isPausedForExternalApp = false
            LogPoseCallService.instance?.resumeAudio()
            start()
        }, "EXTERNAL_PAUSE", android.os.SystemClock.uptimeMillis() + durationMs)
    }

    private var isStartingHandshake = false
    private var isTransitioningToMusic = false

    fun start() {
        if (isStopping || isStartingHandshake || isTransitioningToMusic || isPausedForExternalApp) return
        
        val context = appContext ?: return
        handler.removeCallbacks(autoSleepRunnable)
        isStartingHandshake = true
        com.uriel.logpose.features.music.MusicManager.duck() 
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context).ready()
        isQuickWindowActive = false
        
        com.uriel.logpose.core.app.AppContainer.communicationManager.startCommunication()
        com.uriel.logpose.core.app.AppContainer.communicationManager.enterFullCommunicationMode()
        
        handler.postDelayed({
            if (!isTransitioningToMusic && !isStopping) {
                voiceRepository?.startListening()
            }
            isStartingHandshake = false
            handler.postDelayed(autoSleepRunnable, 60000)
        }, 1000)
    }

    fun lockForMusicTransition() {
        LogPoseLogger.i("VoiceManager: SISTEMA EN ESPERA (Esperando música...)")
        com.uriel.logpose.core.app.AppContainer.communicationManager.prepareForMusic()
        isTransitioningToMusic = true
        stop()
        
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isTransitioningToMusic) {
                LogPoseLogger.w("VoiceManager: Watchdog forzó unlock — el job de música colgó")
                unlockMusicTransition(success = false)
            }
        }, 15000)
    }

    fun isTransitioning(): Boolean = isTransitioningToMusic

    fun unlockMusicTransition(success: Boolean = true) {
        if (!isTransitioningToMusic) return
        com.uriel.logpose.core.services.ComfortNoiseManager.restoreVolume()
        com.uriel.logpose.core.app.AppContainer.communicationManager.restoreCommunication()

        isTransitioningToMusic = false
        isStartingHandshake = false
        isStopping = false
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ start() }, 2000)
    }

    fun stop() {
        if (isStopping) return
        isStopping = true
        resetQuickWindow()
        voiceRepository?.stopListening()
        
        com.uriel.logpose.core.app.AppContainer.communicationManager.exitFullCommunicationMode()
        com.uriel.logpose.core.app.AppContainer.communicationManager.stopCommunication()

        handler.postDelayed({ isStopping = false }, 800)
    }

    private var repeatCount = 0
    private val WAKE_WORD_VARIANTS = setOf("log", "logpose", "log pose", "lock", "lockpose", "low", "lon", "los")

    fun onTextReceived(text: String) {
        LogPoseLogger.d("VoiceManager: Recibido -> '$text'")
        com.uriel.logpose.features.music.MusicManager.unduck() 
        handler.removeCallbacks(autoSleepRunnable)
        
        if (isStopping || isTransitioningToMusic || isPausedForExternalApp) return

        val cleanText = PhoneticEngine.normalize(text).lowercase()
        if (cleanText.isBlank()) return

        val words = cleanText.split(" ")
        val firstWord = words.firstOrNull() ?: ""
        val isCalled = firstWord in WAKE_WORD_VARIANTS
        
        if (isCalled) {
            val commandAfterWake = words.drop(1).joinToString(" ").trim()
            if (commandAfterWake.isNotEmpty()) {
                executeMainCommand(commandAfterWake)
            } else {
                FeedbackManager.speak("Te escucho.")
                startQuickWindow(5000)
            }
        } else if (isQuickWindowActive) {
            executeMainCommand(cleanText)
        } else {
            handler.postDelayed(autoSleepRunnable, 5000)
        }
    }

    private fun restartListeningSlightly(delay: Long) {
        if (isStopping || isPrivacyMode || isTransitioningToMusic) return
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (!isStopping && !isTransitioningToMusic) voiceRepository?.startListening()
        }, delay)
    }

    private fun executeMainCommand(commandText: String) {
        val request = THAMISRequest(text = commandText)
        if (repeatCount >= 1) request.overrideConfidence = 1.0f

        val decision = THAMIS.process(request)

        if (decision.intent != Intent.UNKNOWN) {
            com.uriel.logpose.thamis.learning.LearningEngine.registerCorrection(commandText, decision.intent)
            val command = ActionMapper.map(decision, commandText)
            
            if (decision.intent == Intent.PLAY_MUSIC) {
                lockForMusicTransition()
                stop() 
            }
            handleCommandFeedback(decision)
            CommandDispatcher.execute(command)
            
            val handoverIntents = setOf(Intent.OPEN_APP, Intent.CALL_CONTACT, Intent.SEND_MESSAGE)
            if (decision.intent in handoverIntents) {
                pauseForExternalApp(12000) 
            } else if (decision.intent != Intent.PLAY_MUSIC) {
                stop()
            }
        } else {
            com.uriel.logpose.core.services.AlertManager.enqueue("No entendí ese comando.")
            restartListeningSlightly(1000)
        }
    }

    private fun handleCommandFeedback(decision: Decision) {
        val text = when(decision.intent) {
            Intent.CALL_CONTACT -> "Llamando."
            Intent.NAVIGATE -> "Iniciando navegación."
            Intent.STOP_NAVIGATION -> "Navegación finalizada."
            Intent.OPEN_APP -> "Abriendo aplicación."
            else -> ""
        }
        if (text.isNotEmpty()) FeedbackManager.speak(text)
    }

    fun startQuickWindow(ms: Long) {
        if (isTransitioningToMusic) return
        handler.removeCallbacks(quickWindowRunnable)
        isQuickWindowActive = true
        voiceRepository?.resetStateForRestart()
        voiceRepository?.startListening()
        handler.postDelayed(quickWindowRunnable, ms)
    }

    private fun resetQuickWindow() {
        isQuickWindowActive = false
        handler.removeCallbacks(quickWindowRunnable)
    }

    fun setPrivacyMode(active: Boolean) {
        isPrivacyMode = active
        if (active) stop() else start()
    }

    fun isPrivacyMode(): Boolean = isPrivacyMode

    fun notifyError(error: Int) {
        com.uriel.logpose.features.music.MusicManager.unduck() 
        if (isStopping || isTransitioningToMusic) return
        
        when (error) {
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY, 11 -> {
                voiceRepository?.resetStateForRestart()
                isStartingHandshake = false
                handler.postDelayed({ if (!isStopping && !isTransitioningToMusic) start() }, 5000)
            }
            SpeechRecognizer.ERROR_NO_MATCH, 7 -> {
                restartListeningSlightly(2500)
            }
            else -> {
                restartListeningSlightly(2000)
            }
        }
    }
}
