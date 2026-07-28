package com.uriel.logpose.feature.service

import com.uriel.logpose.core.Action
import com.uriel.logpose.core.Command
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
                when (action.command) {
                    Command.PLAY_MUSIC -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PLAY)
                    Command.PAUSE_MUSIC -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PAUSE)
                    Command.NEXT_TRACK -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.NEXT)
                    Command.PREVIOUS_TRACK -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PREVIOUS)
                    else -> Log.w("ActionManager", "Media command ${action.command} not yet implemented")
                }
            }
            is Action.VoiceResponse -> {
                // TODO: FeedbackManager.speak(action.message)
            }
            else -> Log.w("ActionManager", "Action $action not yet implemented")
        }
    }
}
