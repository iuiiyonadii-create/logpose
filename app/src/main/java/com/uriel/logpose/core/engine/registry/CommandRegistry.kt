package com.uriel.logpose.core.engine.registry

import com.thamis.lab.core.contracts.command.LogPoseCommand
import kotlin.reflect.KClass

interface CommandRegistry {

    fun <T : LogPoseCommand> register(
        type: KClass<T>,
        handler: CommandHandler
    )

    fun execute(command: LogPoseCommand): Boolean

}