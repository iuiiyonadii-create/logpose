package com.uriel.logpose.core.execution

data class ExecutionResult(

    val status: ExecutionStatus,

    val message: String? = null
)