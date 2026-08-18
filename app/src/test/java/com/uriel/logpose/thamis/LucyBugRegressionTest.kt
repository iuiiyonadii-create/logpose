package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.intent.IntentDetector
import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * LucyBugRegressionTest: Permanent test to prevent incorrect voice executions.
 * Goal: Ensure 'look poner luego ponen' NEVER triggers a command.
 */
class LucyBugRegressionTest {

    @Test
    fun `test lucy bug - should be rejected`() {
        val text = "look poner luego ponen"
        val result = IntentDetector.detect(text)
        
        // v71.1: Even if 'poner' is a verb, 'look' requires parameters and high confidence.
        // In this case, it should remain PRIVACY_MUTED.
        assertEquals("PRIVACY_MUTED", result.type)
        assertEquals(Intent.UNKNOWN, result.intent)
    }

    @Test
    fun `test phonetically similar wake words - should be rejected if no command follows`() {
        // v72.0: Now we allow 'lo', 'los', etc. to rescue if a command follows.
        // But the bare word should still be muted.
        val cases = listOf("lo", "los", "low", "lock", "lujo", "lucy", "hugo", "look")
        for (case in cases) {
            val result = IntentDetector.detect(case)
            // If case is just the word, it should remain UNKNOWN/PRIVACY_MUTED
            assertEquals("Case '$case' should be muted", "PRIVACY_MUTED", result.type)
        }
    }

    @Test
    fun `test rescue logic - lo pone musica should be accepted`() {
        val text = "lo pone musica"
        val result = IntentDetector.detect(text)
        
        // v72.0: 'lo' is now in WAKE_WORDS, so it should be stripped
        // and the remaining 'pone musica' should be classified.
        assertEquals("STAFF_MATCH", result.type)
        assertEquals(Intent.PLAY_MUSIC, result.intent)
        assertEquals("musica", result.entities["parameter"])
    }

    @Test
    fun `test valid command with rioplatense variation`() {
        val text = "log poneme una cumbia"
        val result = IntentDetector.detect(text)
        
        assertEquals("STAFF_MATCH", result.type)
        assertEquals(Intent.PLAY_MUSIC, result.intent)
        assertEquals("una cumbia", result.entities["parameter"])
    }
}
