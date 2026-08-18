package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.intent.IntentDetector
import org.junit.Assert.assertEquals
import org.junit.Test

class AcousticHardeningTest {

    @Test
    fun `test lucy bug - look poner luego ponen should not trigger wake word`() {
        val text = "look poner luego ponen"
        val result = IntentDetector.detect(text)
        
        // v70.0: 'look' is in LENIENT_WAKE_WORDS, but 'poner' is not a strong action verb trigger
        // to justify a wake-word match without high confidence.
        // Actually, IntentDetector.detect returns Intent.UNKNOWN and PRIVACY_MUTED if wake word fails.
        assertEquals("PRIVACY_MUTED", result.type)
        assertEquals(Intent.UNKNOWN, result.intent)
    }

    @Test
    fun `test correct wake word - log pone musica should trigger`() {
        val text = "log pone musica"
        val result = IntentDetector.detect(text)
        
        assertEquals("STAFF_MATCH", result.type)
        assertEquals(Intent.PLAY_MUSIC, result.intent)
    }
}
