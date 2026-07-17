package com.uriel.logpose.core.parser.pipeline

import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.intent.IntentResolver
import com.uriel.logpose.core.parser.normalization.CommandNormalizer

object CommandPipeline {

    fun process(
        text: String
    ): ParseResult {

        val normalized = CommandNormalizer.normalize(text)

        val intent = IntentResolver.resolve(normalized)

        return CommandParser.parse(
            intent.text
        )
    }
}