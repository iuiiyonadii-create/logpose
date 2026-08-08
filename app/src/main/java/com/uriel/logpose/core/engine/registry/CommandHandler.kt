package com.uriel.logpose.core.engine.registry

import com.thamis.lab.core.contracts.command.LogPoseCommand

fun interface CommandHandler {

    fun execute(command: LogPoseCommand)

}