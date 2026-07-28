package com.uriel.logpose.features.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.flow.StateFlow

/**
 * VoiceRepository: Ahora usa Vosk para reconocimiento OFFLINE de alta velocidad.
 */
class VoiceRepository(
    private val session: VoiceSession = VoiceSession()
) {
    private var appContext: Context? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    val state: StateFlow<VoiceState> get() = session.state

    fun attachRecognizer(manager: SpeechRecognizerManager, context: Context) {
        this.appContext = context.applicationContext
        // SINCRO CLAUDE: El motor ya se inicializa en el AppContainer.
        // Solo nos aseguramos de que esté listo para la batalla.
        LogPoseLogger.i("VoiceRepository: Vinculado al motor Vosk del AppContainer.")
    }

    fun startListening() {
        if (session.state.value == VoiceState.LISTENING) return
        
        session.update(VoiceState.LISTENING)
        LogPoseLogger.d("Micro: Escucha OFFLINE activada.")

        // Usamos la instancia del AppContainer
        com.uriel.logpose.core.app.AppContainer.voskEngine.start()
    }

    fun stopListening() {
        com.uriel.logpose.core.app.AppContainer.voskEngine.stop()
        session.update(VoiceState.STOPPED)
    }

    fun resetStateForRestart() {
        com.uriel.logpose.core.app.AppContainer.voskEngine.stop()
        session.update(VoiceState.IDLE)
    }

    fun current(): VoiceState = session.current()
}
