package com.uriel.logpose.thamis_ai.ai_models

/**
 * Representation of a local AI model.
 */
data class AIModel(
    val id: String,
    val name: String,
    val version: String,
    val state: ModelState = ModelState.NOT_INSTALLED
)

enum class ModelState {
    NOT_INSTALLED,
    LOADING,
    READY,
    RUNNING,
    ERROR
}
