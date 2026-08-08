package com.uriel.logpose.core.music

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Controller for device media playback using key event simulation.
 */
class MusicController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun execute(command: LogPoseCommand): Boolean {
        return when (command) {
            is LogPoseCommand.PlayMusic -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY)
            LogPoseCommand.PauseMusic -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PAUSE)
            LogPoseCommand.NextTrack -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            LogPoseCommand.PreviousTrack -> sendMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            LogPoseCommand.VolumeUp -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                true
            }
            LogPoseCommand.VolumeDown -> {
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
