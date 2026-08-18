package com.uriel.logpose.features.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.concurrent.atomic.AtomicBoolean

/**
 * PlaybackAwareMicGate: Controla el estado del "oído" del sistema.
 * Bloquea la escucha si hay música sonando, si el sistema está hablando
 * o si se detectan indicaciones de navegación (Maps/Waze).
 */
class PlaybackAwareMicGate(context: Context) {

    private val mutedByTts = AtomicBoolean(false)
    private val mutedByPlayback = AtomicBoolean(false)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val playbackCallback = object : AudioManager.AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: List<android.media.AudioPlaybackConfiguration>) {
            val isExternalPlaybackActive = configs.any { config ->
                val usage = config.audioAttributes.usage
                
                // Misión #032: Filtrado por Uso de Audio (Indispensable para ignorar a Maps)
                val isGuidance = usage == AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE
                val isAlarm = usage == AudioAttributes.USAGE_ALARM
                
                // SINCRO CLAUDE: USAGE_MEDIA ya no bloquea el micro por defecto.
                // Esto permite que Thamis escuche mientras la música suena (ducked).
                isGuidance || isAlarm
            }
            
            if (isExternalPlaybackActive != mutedByPlayback.get()) {
                mutedByPlayback.set(isExternalPlaybackActive)
                if (isExternalPlaybackActive) {
                    LogPoseLogger.d("MicGate: Oído silenciado (Audio externo detectado).")
                } else {
                    LogPoseLogger.d("MicGate: Oído reactivado.")
                }
            }
        }
    }

    init {
        audioManager.registerAudioPlaybackCallback(playbackCallback, android.os.Handler(android.os.Looper.getMainLooper()))
    }

    fun onTtsStarted() { mutedByTts.set(true) }
    fun onTtsEnded() { mutedByTts.set(false) }

    fun onPlaybackStarted() { mutedByPlayback.set(true) }
    fun onPlaybackStopped() { mutedByPlayback.set(false) }

    fun isGateOpen(): Boolean {
        return !mutedByTts.get() && !mutedByPlayback.get()
    }
    
    fun cleanup() {
        audioManager.unregisterAudioPlaybackCallback(playbackCallback)
    }
}
