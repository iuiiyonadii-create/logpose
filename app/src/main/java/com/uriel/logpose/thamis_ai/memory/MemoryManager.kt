package com.uriel.logpose.thamis_ai.memory

import android.util.Log

/**
 * High-level coordinator for THAMIS memory lifecycle.
 */
class MemoryManager {

    private val validator = MemoryValidator()

    fun remember(item: MemoryItem) {
        if (validator.isValid(item)) {
            Log.d("Memory", "Remembering: ${item.key}")
            // Persistence logic here
        }
    }

    fun forget(id: String) {
        Log.d("Memory", "Forgetting: $id")
    }

    fun clear() {
        Log.d("Memory", "All memory cleared")
    }
}
