package com.uriel.logpose.thamis.actuator.music

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.services.CommandExecutorDelegator
import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.actuator.CognitiveActionExecutor

/**
 * Puente entre el cerebro cognitivo y el motor de música de Spotify.
 * Implementa la autoridad real delegada por THAMIS.
 */
class MusicActuator : CognitiveActionExecutor {
    override fun execute(decision: ThamisDecision) {
        val query = decision.winningEvaluation?.hypothesis?.entities?.get("media") ?: ""
        if (query.isNotEmpty()) {
            CommandExecutorDelegator.execute(LogPoseCommand.Media.PlayMusic(query))
        } else {
            when (decision.intent) {
                Intent.PAUSE_MUSIC -> CommandExecutorDelegator.execute(LogPoseCommand.Media.PauseMusic)
                Intent.NEXT_TRACK -> CommandExecutorDelegator.execute(LogPoseCommand.Media.NextTrack)
                Intent.PREVIOUS_TRACK -> CommandExecutorDelegator.execute(LogPoseCommand.Media.PreviousTrack)
                else -> {}
            }
        }
    }
}
