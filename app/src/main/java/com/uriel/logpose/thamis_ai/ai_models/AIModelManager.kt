package com.uriel.logpose.thamis_ai.ai_models

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Manages the lifecycle of local AI models.
 */
class AIModelManager {

    private val activeModels = mutableMapOf<String, AIModel>()

    fun loadModel(model: AIModel) {
        LogPoseLogger.d("AIModels", "Loading model: ${model.name}")
        activeModels[model.id] = model.copy(state = ModelState.READY)
    }

    fun unloadModel(id: String) {
        activeModels.remove(id)
        LogPoseLogger.d("AIModels", "Unloaded model: $id")
    }
}
