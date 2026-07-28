package com.uriel.logpose.features.music

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent
import com.uriel.logpose.core.parser.FastParser

/**
 * TransportCommandInterpreter: Ejecución directa de comandos de transporte y volumen.
 */
class TransportCommandInterpreter(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun execute(action: FastParser.TransportAction): Boolean {
        return when (action) {
            FastParser.TransportAction.NEXT -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            FastParser.TransportAction.PREVIOUS -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            FastParser.TransportAction.PAUSE -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PAUSE)
            FastParser.TransportAction.PLAY -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY)
            FastParser.TransportAction.REPEAT -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD)
            FastParser.TransportAction.VOLUME_UP -> adjustVolume(AudioManager.ADJUST_RAISE)
            FastParser.TransportAction.VOLUME_DOWN -> adjustVolume(AudioManager.ADJUST_LOWER)
        }
    }

    private fun dispatchMediaKey(keyCode: Int): Boolean {
        return try {
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun adjustVolume(direction: Int): Boolean {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        return true
    }
}
