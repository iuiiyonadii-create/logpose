package com.uriel.logpose.logcore.core.action

sealed interface ActionResult {

    data object Success : ActionResult

    data class Failure(
        val message: String
    ) : ActionResult

    data class BooleanResult(
        val value: Boolean
    ) : ActionResult

}