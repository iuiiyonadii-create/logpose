package com.uriel.logpose.core.parser.pipeline

import com.uriel.logpose.core.context.CommandContext
import com.uriel.logpose.core.context.CommandContextHolder
import com.uriel.logpose.core.learning.LearningEngine
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.core.parser.FastParser
import com.uriel.logpose.core.parser.multicommand.MultiCommandExecutor
import com.uriel.logpose.core.parser.multicommand.MultiCommandParser
import com.uriel.logpose.core.parser.multicommand.MultiCommandStatistics
import com.uriel.logpose.core.parser.normalization.CommandNormalizer
import com.uriel.logpose.thamis.THAMIS
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.request.THAMISRequest

object CommandPipeline {

    fun process(
        text: String
    ): ParseResult {

        if (MultiCommandParser.isMultiCommand(text)) {
            val multiCommand = MultiCommandParser.parse(text)
            MultiCommandStatistics.register(multiCommand)
            MultiCommandExecutor.execute(multiCommand)
            return ParseResult.MultiSuccess
        }

        // --- Fast Path: Intelligent Dispatcher (Rule-based) ---
        val fastResult = FastParser.parse(text)
        if (fastResult !is ParseResult.Unknown) {
            return fastResult
        }

        val normalized = CommandNormalizer.normalize(text)

        CommandContextHolder.update(
            CommandContext(
                originalText = text,
                normalizedText = normalized
            )
        )

        // Integración con THAMIS
        val request = THAMISRequest(text = normalized)
        val decision = THAMIS.process(request)

        if (decision.intent == com.uriel.logpose.thamis.intent.Intent.UNKNOWN) {
            LearningEngine.registerFailure(text)
            return ParseResult.Unknown
        }

        val command = ActionMapper.map(decision, text)
        val result = ParseResult.Success(command)

        LearningEngine.registerSuccess(text)

        return result
    }
}