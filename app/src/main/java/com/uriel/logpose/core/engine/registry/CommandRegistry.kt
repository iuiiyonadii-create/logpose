package com.uriel.logpose.core.engine.registry

import com.uriel.logpose.core.compat.core.Command
import kotlin.reflect.KClass

interface CommandRegistry {

    fun <T : Command> register(
        type: KClass<T>,
        handler: CommandHandler
    )

    fun execute(command: Command): Boolean

}