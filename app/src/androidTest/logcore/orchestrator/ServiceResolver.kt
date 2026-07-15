package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.capability.Capability
import com.uriel.logpose.logcore.services.MusicService

class ServiceResolver(

    private val musicService: MusicService

) {

    fun resolve(action: ActionRequest): Any {

        return when (action.capability) {

            Capability.PlayMusic,
            Capability.PauseMusic,
            Capability.NextTrack,
            Capability.PreviousTrack,
            Capability.RepeatTrack,
            Capability.SetVolume,
            Capability.Mute,
            Capability.Silence -> musicService

            else -> error(
                "No existe un Service para ${action.capability}"
            )
        }
    }
}