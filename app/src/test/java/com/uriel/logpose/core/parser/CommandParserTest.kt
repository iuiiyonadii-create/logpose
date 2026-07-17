package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.Command
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandParserTest {

    @Test
    fun parse_start_listening() {

        val result = CommandParser.parse("escuchar")

        assertTrue(
            result is ParseResult.Success &&
                    result.command == Command.StartListening
        )
    }

    @Test
    fun parse_stop_listening() {

        val result = CommandParser.parse("detener")

        assertTrue(
            result is ParseResult.Success &&
                    result.command == Command.StopListening
        )
    }

    @Test
    fun parse_unknown_command() {

        val result = CommandParser.parse("hola mundo")

        assertTrue(
            result is ParseResult.Unknown
        )
    }
}