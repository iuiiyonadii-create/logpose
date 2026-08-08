package com.uriel.logpose.feature.service

import com.uriel.logpose.core.Action
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.music.MusicController
import android.util.Log

/**
 * Bridges THAMIS decisions to Android-specific execution.
 */
class ActionManager(
    private val musicController: MusicController
) {
    fun execute(action: Action) {
        Log.d("ActionManager", "Executing: $action")
        when (action) {
            is Action.MediaAction -> {
                musicController.execute(action.command)
            }
            is Action.VoiceResponse -> {
                // TODO: FeedbackManager.speak(action.message)
            }
            else -> Log.w("ActionManager", "Action $action not yet implemented")
        }
    }
}
