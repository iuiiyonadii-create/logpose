package com.uriel.logpose.core.parser.context

object CommandContextHolder {

    private var current: CommandContext? = null

    fun update(context: CommandContext) {
        current = context
    }

    fun current(): CommandContext? = current

    fun clear() {
        current = null
    }
}