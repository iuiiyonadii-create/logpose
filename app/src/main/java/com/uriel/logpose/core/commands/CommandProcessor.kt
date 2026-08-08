package com.uriel.logpose.core.commands

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.pipeline.CommandPipeline


object CommandProcessor {


    fun process(
        text: String
    ): LogPoseCommand {


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



            ParseResult.MultiSuccess -> {


                LogPoseLogger.i(
                    "Multi-comando procesado con éxito"
                )


                LogPoseCommand.Multi

            }



            ParseResult.Unknown -> {


                LogPoseLogger.w(
                    "Comando no reconocido: $text"
                )


                LogPoseCommand.Unknown

            }

            ParseResult.Ignored -> {

                LogPoseLogger.i(
                    "Frase ignorada (ruido/muletilla): $text"
                )

                LogPoseCommand.Ignore
            }

        }

    }

}