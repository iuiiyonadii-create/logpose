package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
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

            is ValidationResult.Invalid -> {
                return ActionResult.Failure(validation.reason)
            }
        }

        val service = resolver.resolve(action)

        return when (service) {

            is MusicService -> {
                service.play()
                ActionResult.Success
            }

            else -> {
                ActionResult.Failure(
                    "Service no soportado: ${service::class.simpleName}"
                )
            }
        }
    }
}