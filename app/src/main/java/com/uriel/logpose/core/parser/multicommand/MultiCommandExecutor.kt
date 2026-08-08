package com.uriel.logpose.core.parser.multicommand

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.pipeline.CommandPipeline

object MultiCommandExecutor {

    fun execute(
        result: MultiCommandResult
    ) {

        result.command.commands.forEach {

            val parseResult =
                CommandPipeline.process(it)



            if (parseResult is ParseResult.Success) {


                CommandDispatcher.execute(
                    parseResult.command
                )


            }
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