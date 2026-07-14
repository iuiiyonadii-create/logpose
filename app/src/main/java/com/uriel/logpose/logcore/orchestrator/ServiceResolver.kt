package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.capability.IsPlaying
import com.uriel.logpose.logcore.core.capability.NextSong
import com.uriel.logpose.logcore.core.capability.PauseMusic
import com.uriel.logpose.logcore.core.capability.PlayMusic
import com.uriel.logpose.logcore.core.capability.PreviousSong
import com.uriel.logpose.logcore.core.capability.StopMusic
import com.uriel.logpose.logcore.services.MusicService

class ServiceResolver(

    private val musicService: MusicService

) {

    fun resolve(action: ActionRequest): Any {

        return when (action.capability) {

            PlayMusic,
            PauseMusic,
            StopMusic,
            NextSong,
            PreviousSong,
            IsPlaying -> musicService

            else -> error(
                "No existe un Service para ${action.capability}"
            )

        }

    }

}