package com.uriel.logpose.core.commands

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.pipeline.CommandPipeline


object CommandProcessor {


    fun process(
        text: String
    ): Command {


        LogPoseLogger.i(
            "Procesando: $text"
        )



        return when (
            val result =
                CommandPipeline.process(text)
        ) {



            is ParseResult.Success -> {


                LogPoseLogger.i(
                    "Comando detectado: ${result.command}"
                )


                result.command

            }



            ParseResult.Unknown -> {


                LogPoseLogger.w(
                    "Comando no reconocido: $text"
                )


                Command.Unknown

            }

        }

    }

}