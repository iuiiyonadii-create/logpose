package com.uriel.logpose.core.parser.pipeline

import com.uriel.logpose.core.context.CommandContext
import com.uriel.logpose.core.context.CommandContextHolder
import com.uriel.logpose.core.learning.LearningEngine
import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.confidence.ConfidenceEngine
import com.uriel.logpose.core.parser.intent.IntentResolver
import com.uriel.logpose.core.parser.multicommand.MultiCommandExecutor
import com.uriel.logpose.core.parser.multicommand.MultiCommandParser
import com.uriel.logpose.core.parser.multicommand.MultiCommandStatistics
import com.uriel.logpose.core.parser.normalization.CommandNormalizer

object CommandPipeline {

    private const val MIN_CONFIDENCE = 0.5f

    fun process(
        text: String
    ): ParseResult {

        if (MultiCommandParser.isMultiCommand(text)) {

            val multiCommand =
                MultiCommandParser.parse(text)

            MultiCommandStatistics.register(
                multiCommand
            )

            MultiCommandExecutor.execute(
                multiCommand
            )

            return ParseResult.Unknown
        }

        val normalized =
            CommandNormalizer.normalize(text)

        CommandContextHolder.update(

            CommandContext(

                originalText = text,

                normalizedText = normalized
            )
        )

        val confidence =
            ConfidenceEngine.evaluate(normalized)

        if (confidence.confidence < MIN_CONFIDENCE) {

            LearningEngine.registerFailure(text)

            return ParseResult.Unknown
        }

        val intent =
            IntentResolver.resolve(confidence.text)

        val result =
            CommandParser.parse(intent.text)

        when (result) {

            is ParseResult.Success ->
                LearningEngine.registerSuccess(
                    intent.text
                )

            ParseResult.Unknown ->
                LearningEngine.registerFailure(
                    intent.text
                )
        }

        return result
    }
}