package com.uriel.logpose.thamis_ai.memory

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * High-level coordinator for THAMIS memory lifecycle.
 */
class MemoryManager {

    private val validator = MemoryValidator()

    fun remember(item: MemoryItem) {
        if (validator.isValid(item)) {
            LogPoseLogger.d("Memory", "Remembering: ${item.key}")
            // Persistence logic here
        }
    }

    fun forget(id: String) {
        LogPoseLogger.d("Memory", "Forgetting: $id")
    }

    fun clear() {
        LogPoseLogger.d("Memory", "All memory cleared")
    }
}
