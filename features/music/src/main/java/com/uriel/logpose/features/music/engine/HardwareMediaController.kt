package com.uriel.logpose.features.music.engine

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * HardwareMediaController: Control de transporte multimedia mediante simulación de KeyEvents de hardware.
 * Procesa comandos universales ("PAUSE", "NEXT", "PREVIOUS", "PLAY") directo sobre AudioManager.
 */
object HardwareMediaController {

    fun executeCommand(context: Context, command: String) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager == null) {
            LogPoseLogger.e("HardwareMediaController: AudioManager no disponible.")
            return
        }

        val keyCode = when (command.uppercase().trim()) {
            "PAUSE", "STOP" -> KeyEvent.KEYCODE_MEDIA_PAUSE
            "PLAY" -> KeyEvent.KEYCODE_MEDIA_PLAY
            "TOGGLE", "PLAY_PAUSE" -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            "NEXT" -> KeyEvent.KEYCODE_MEDIA_NEXT
            "PREVIOUS", "PREV" -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            else -> {
                LogPoseLogger.w("HardwareMediaController: Comando no reconocido -> '$command'")
                return
            }
        }

        try {
            LogPoseLogger.i("HardwareMediaController: Inyectando KeyEvent $keyCode ($command)")
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        } catch (e: Exception) {
            LogPoseLogger.e("HardwareMediaController: Error inyectando KeyEvent: ${e.message}")
        }
    }
}
