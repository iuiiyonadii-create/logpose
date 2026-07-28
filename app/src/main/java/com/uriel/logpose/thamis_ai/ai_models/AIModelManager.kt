package com.uriel.logpose.thamis_ai.ai_models

import android.util.Log

/**
 * Manages the lifecycle of local AI models.
 */
class AIModelManager {

    private val activeModels = mutableMapOf<String, AIModel>()

    fun loadModel(model: AIModel) {
        Log.d("AIModels", "Loading model: ${model.name}")
        activeModels[model.id] = model.copy(state = ModelState.READY)
    }

    fun unloadModel(id: String) {
        activeModels.remove(id)
        Log.d("AIModels", "Unloaded model: $id")
    }
}
