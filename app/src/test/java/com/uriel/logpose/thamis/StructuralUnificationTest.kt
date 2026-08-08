package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.action.ActionMapper
import com.uriel.logpose.thamis.decision.Decision
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StructuralUnificationTest {

    @Test
    fun testMusicIntentDetection() {
        val text = "Pone Uzbekistan"
        val result = IntentDetector.detect(text)
        
        assertEquals(Intent.PLAY_MUSIC, result.intent)
        assertTrue(result.score > 0.5f)
    }

    @Test
    fun testMusicActionMapping() {
        val decision = Decision(
            intent = Intent.PLAY_MUSIC,
            confidence = 0.9f,
            entities = mapOf("media" to "uzbekistan")
        )
        
        val command = ActionMapper.map(decision, "Pone Uzbekistan")
        
        assertTrue(command is LogPoseCommand.PlayMusic)
        assertEquals("uzbekistan", (command as LogPoseCommand.PlayMusic).query)
    }
}
