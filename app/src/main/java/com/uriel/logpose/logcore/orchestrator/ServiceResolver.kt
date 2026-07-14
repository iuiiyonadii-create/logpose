package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.capability.PlayMusic
import com.uriel.logpose.logcore.services.MusicService

class ServiceResolver(

    private val musicService: MusicService

) {

    fun resolve(action: ActionRequest): Any {

        return when (action.capability) {

            PlayMusic -> musicService

            else -> throw IllegalArgumentException(
                "No existe un Service para ${action.capability}"
            )

        }

    }

}