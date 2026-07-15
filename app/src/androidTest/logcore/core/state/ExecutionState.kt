package com.uriel.logpose.logcore.core.state

enum class ExecutionState {
    CREATED,
    RUNNING,
    SUCCESS,
    FAILED,
    CANCELLED,
    TIMEOUT
}