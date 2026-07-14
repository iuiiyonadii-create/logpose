package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
import com.uriel.logpose.logcore.core.intent.capability.Capability
import com.uriel.logpose.logcore.orchestrator.validator.ActionValidator
import com.uriel.logpose.logcore.orchestrator.validator.ValidationResult
import com.uriel.logpose.logcore.services.MusicService

class ActionEngine(

    private val resolver: ServiceResolver,
    private val validator: ActionValidator

) {

    fun dispatch(action: ActionRequest): ActionResult {

        when (val validation = validator.validate(action)) {

            ValidationResult.Valid -> Unit

            is ValidationResult.Invalid ->
                return ActionResult.Failure(validation.reason)
        }

        val service = resolver.resolve(action)

        return when (service) {

            is MusicService -> {

                when (action.capability) {

                    Capability.PlayMusic -> service.play()

                    Capability.PauseMusic -> service.pause()

                    Capability.NextTrack -> service.next()

                    Capability.PreviousTrack -> service.previous()

                    Capability.RepeatTrack -> {
                        // TODO
                    }

                    Capability.SetVolume -> {
                        // TODO
                    }

                    Capability.Mute -> {
                        // TODO
                    }

                    Capability.Silence -> {
                        // TODO
                    }

                    else -> {
                        return ActionResult.Failure(
                            "Capability no soportada por MusicService: ${action.capability}"
                        )
                    }
                }

                ActionResult.Success
            }

            else -> ActionResult.Failure(
                "Service no soportado"
            )
        }
    }
}