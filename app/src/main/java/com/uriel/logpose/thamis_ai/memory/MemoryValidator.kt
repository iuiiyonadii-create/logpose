package com.uriel.logpose.thamis_ai.memory

/**
 * Validates entries before storage to ensure privacy and utility.
 */
class MemoryValidator {

    fun isValid(item: MemoryItem): Boolean {
        // Rule 1: No sensitive keywords in value
        val sensitiveKeywords = listOf("password", "clave", "secreto")
        if (sensitiveKeywords.any { item.value.lowercase().contains(it) }) return false

        // Rule 2: Keys must not be empty
        if (item.key.isBlank()) return false

        return true
    }
}
