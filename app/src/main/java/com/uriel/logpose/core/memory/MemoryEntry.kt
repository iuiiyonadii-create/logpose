package com.uriel.logpose.core.memory

import com.uriel.logpose.core.compat.core.Command

data class MemoryEntry(
    val command: Command,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)