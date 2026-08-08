package com.uriel.logpose.core.memory

import com.thamis.lab.core.contracts.command.LogPoseCommand

object CommandMemory {

    private val entries =
        mutableListOf<MemoryEntry>()

    fun remember(command: LogPoseCommand) {

        entries.add(
            MemoryEntry(
                command = command
            )
        )
    }

    fun last(): MemoryEntry? =
        entries.lastOrNull()

    fun recent(limit: Int = 10): List<MemoryEntry> =
        entries
            .takeLast(limit)
            .reversed()

    fun find(command: LogPoseCommand): List<MemoryEntry> =
        entries.filter {
            it.command == command
        }

    fun clear() {
        entries.clear()
    }
}