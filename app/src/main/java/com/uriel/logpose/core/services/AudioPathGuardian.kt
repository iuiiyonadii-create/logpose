package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * AudioPathGuardian: Asegura que el audio nunca se filtre por el altavoz del teléfono
 * mientras el usuario está en un viaje activo con casco.
 */
object AudioPathGuardian {

    private var audioManager: AudioManager? = null
    private var isGuardianActive = false

    fun initialize(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * Activa el blindaje de audio.
     */
    fun protect() {
        if (isGuardianActive) return
        isGuardianActive = true
        LogPoseLogger.i("AudioGuardian: Blindaje de privacidad ACTIVADO.")
        checkAndEnforceRouting()
    }

    /**
     * Desactiva el blindaje.
     */
    fun release() {
        isGuardianActive = false
        LogPoseLogger.i("AudioGuardian: Blindaje de privacidad DESACTIVADO.")
        // Restauramos volúmenes si es necesario (Implementación futura)
    }

    /**
     * Verifica si el audio está saliendo por el lugar correcto.
     * Si detecta altavoz del teléfono durante el viaje, lo silencia.
     */
    fun checkAndEnforceRouting() {
        if (!isGuardianActive) return
        val am = audioManager ?: return

        val isScoOn = am.isBluetoothScoOn
        val isA2dpOn = am.isBluetoothA2dpOn

        if (!isScoOn && !isA2dpOn) {
            LogPoseLogger.w("AudioGuardian: ¡ALERTA! El audio no está ruteado a Bluetooth. Silenciando altavoz local por seguridad.")
            silenceLocalSpeaker()
        } else {
            restoreAudioStreams()
        }
    }

    private fun silenceLocalSpeaker() {
        val am = audioManager ?: return
        // Silenciamos Streams clave para evitar filtraciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0)
        } else {
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_MUSIC, true)
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true)
        }
    }

    private fun restoreAudioStreams() {
        val am = audioManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
            am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0)
        } else {
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_MUSIC, false)
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false)
        }
    }
}
