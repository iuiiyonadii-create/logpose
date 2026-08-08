package com.uriel.logpose.core.memory

import com.thamis.lab.core.contracts.command.LogPoseCommand

data class MemoryEntry(
    val command: LogPoseCommand,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)