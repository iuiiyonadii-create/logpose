package com.uriel.logpose.core.parser.pipeline

import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.context.CommandContext
import com.uriel.logpose.core.parser.context.CommandContextHolder
import com.uriel.logpose.core.parser.intent.IntentResolver
import com.uriel.logpose.core.parser.normalization.CommandNormalizer

object CommandPipeline {

    private const val MIN_CONFIDENCE = 0.50f

    fun process(
        text: String
    ): ParseResult {

        val normalized = CommandNormalizer.normalize(text)

        CommandContextHolder.update(
            CommandContext(
                originalText = text,
                normalizedText = normalized
            )
        )

        val intent = IntentResolver.resolve(normalized)

        if (intent.confidence < MIN_CONFIDENCE) {
            return ParseResult.Unknown
        }

        return CommandParser.parse(
            intent.text
        )
    }
}