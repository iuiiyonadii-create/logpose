package com.uriel.logpose.core.parser

/**
 * @deprecated Use CognitivePipeline instead.
 * Part of Thamis Evolution Program v5.0 - Duplication Removal.
 */
@Deprecated("Use CognitivePipeline")
object FastParser {
    fun parse(phoneticClean: String): ParseResult = ParseResult.Unknown

    enum class TransportAction {
        NEXT, PREVIOUS, PAUSE, PLAY, REPEAT, VOLUME_UP, VOLUME_DOWN
    }
}
