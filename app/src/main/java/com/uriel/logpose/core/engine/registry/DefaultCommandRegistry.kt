package com.uriel.logpose.core.engine.registry

import com.thamis.lab.core.contracts.command.LogPoseCommand
import kotlin.reflect.KClass

class DefaultCommandRegistry : CommandRegistry {

    private val handlers =
        mutableMapOf<KClass<out LogPoseCommand>, CommandHandler>()

    override fun <T : LogPoseCommand> register(
        type: KClass<T>,
        handler: CommandHandler
    ) {
        handlers[type] = handler
    }

    override fun execute(command: LogPoseCommand): Boolean {

        val handler =
            handlers[command::class]
                ?: return false

        handler.execute(command)

        return true
    }

}