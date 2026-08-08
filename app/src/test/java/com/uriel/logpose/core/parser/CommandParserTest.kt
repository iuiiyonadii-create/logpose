package com.uriel.logpose.core.parser

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.features.voice.VoiceCommandParser
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandParserTest {

    @Test
    fun parse_start_listening() {

        val result = VoiceCommandParser.parse("logpose escuchar")

        assertTrue(
            result == LogPoseCommand.StartListening
        )
    }

    @Test
    fun parse_stop_listening() {

        val result = VoiceCommandParser.parse("logpose detener")

        assertTrue(
            result == LogPoseCommand.StopListening
        )
    }

    @Test
    fun parse_unknown_command() {

        val result = VoiceCommandParser.parse("logpose hola mundo")

        assertTrue(
            result == LogPoseCommand.Unknown
        )
    }
}