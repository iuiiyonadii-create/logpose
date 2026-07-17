package com.uriel.logpose.core.context

object CommandHistory {

    private val history =
        mutableListOf<CommandContext>()

    fun add(context: CommandContext) {
        history.add(context)
    }

    fun last(): CommandContext? =
        history.lastOrNull()

    fun all(): List<CommandContext> =
        history.toList()

    fun clear() {
        history.clear()
    }
}