package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.orchestrator.validator.ActionValidator
import com.uriel.logpose.logcore.orchestrator.validator.ValidationResult
import com.uriel.logpose.logcore.services.MusicService

class ActionEngine(

    private val resolver: ServiceResolver,
    private val validator: ActionValidator

) {

    fun dispatch(action: ActionRequest) {

        when (validator.validate(action)) {

            ValidationResult.Valid -> Unit

            is ValidationResult.Invalid -> {
                return
            }
        }

        val service = resolver.resolve(action)

        when (service) {

            is MusicService -> service.play()

            else -> error(
                "Service no soportado: ${service::class.simpleName}"
            )
        }
    }
}