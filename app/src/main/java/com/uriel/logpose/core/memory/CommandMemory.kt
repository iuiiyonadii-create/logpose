package com.uriel.logpose.core.memory

import com.uriel.logpose.core.compat.core.Command

object CommandMemory {

    private val entries =
        mutableListOf<MemoryEntry>()

    fun remember(command: Command) {

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

    fun find(command: Command): List<MemoryEntry> =
        entries.filter {
            it.command == command
        }

    fun clear() {
        entries.clear()
    }
}