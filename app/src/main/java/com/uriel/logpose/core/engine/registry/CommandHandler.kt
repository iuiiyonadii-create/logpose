package com.uriel.logpose.core.engine.registry

import com.uriel.logpose.core.compat.core.Command

fun interface CommandHandler {

    fun execute(command: Command)

}