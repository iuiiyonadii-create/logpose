package com.uriel.logpose.feature.service

import com.uriel.logpose.core.Action
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Bridges THAMIS decisions to Android-specific execution.
 */
class ActionManager(
    private val musicController: MusicController
) {
    fun execute(action: Action) {
        LogPoseLogger.d("ActionManager", "Executing: $action")
        when (action) {
            is Action.MediaAction -> {
                musicController.execute(action.command)
            }
            is Action.VoiceResponse -> {
                // TODO: FeedbackManager.speak(action.message)
            }
            else -> LogPoseLogger.w("ActionManager", "Action $action not yet implemented")
        }
    }
}
