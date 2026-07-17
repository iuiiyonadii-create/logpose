package com.uriel.logpose.core.learning

object CommandMemory {

    private const val MAX_HISTORY = 100

    private val history = ArrayDeque<CommandSession>()

    fun add(session: CommandSession) {

        if (history.size >= MAX_HISTORY) {
            history.removeFirst()
        }

        history.addLast(session)
    }

    fun history(): List<CommandSession> =
        history.toList()

    fun clear() {
        history.clear()
    }

    fun size(): Int =
        history.size
}