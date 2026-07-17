package com.uriel.logpose.core.engine.registry

import com.uriel.logpose.core.compat.core.Command
import kotlin.reflect.KClass

class DefaultCommandRegistry : CommandRegistry {

    private val handlers =
        mutableMapOf<KClass<out Command>, CommandHandler>()

    override fun <T : Command> register(
        type: KClass<T>,
        handler: CommandHandler
    ) {
        handlers[type] = handler
    }

    override fun execute(command: Command): Boolean {

        val handler =
            handlers[command::class]
                ?: return false

        handler.execute(command)

        return true
    }

}