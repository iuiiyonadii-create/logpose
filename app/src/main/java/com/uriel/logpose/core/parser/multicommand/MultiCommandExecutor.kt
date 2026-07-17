package com.uriel.logpose.core.parser.multicommand

import com.uriel.logpose.core.parser.pipeline.CommandPipeline

object MultiCommandExecutor {

    fun execute(
        result: MultiCommandResult
    ) {

        result.command.commands.forEach {

            CommandPipeline.process(it)
        }
    }

    fun execute(
        text: String
    ) {

        execute(
            MultiCommandParser.parse(text)
        )
    }
}