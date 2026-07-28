package com.uriel.logpose.core.music

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.uriel.logpose.domain.models.LogPoseCommand

/**
 * Controller for device media playback using key event simulation.
 */
class MusicController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun execute(command: LogPoseCommand): Boolean {
        return when (command) {
            LogPoseCommand.PLAY -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY)
            LogPoseCommand.PAUSE -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PAUSE)
            LogPoseCommand.NEXT -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            LogPoseCommand.PREVIOUS -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            LogPoseCommand.VOLUME_UP -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                true
            }
            LogPoseCommand.VOLUME_DOWN -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                true
            }
            else -> false
        }
    }

    private fun sendMediaKey(keyCode: Int): Boolean {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        audioManager.dispatchMediaKeyEvent(downEvent)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(upEvent)
        return true
    }
}
