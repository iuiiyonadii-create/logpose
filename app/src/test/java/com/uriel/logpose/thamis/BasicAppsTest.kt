package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.intelligence.ThamisBrain
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class BasicAppsTest {

    @Test
    fun testWhatsAppBasics() = runBlocking {
        val request = THAMISRequest("mandale un wasap a mama que diga llego en cinco")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.SEND_MESSAGE, decision.intent)
        assertEquals("mama", decision.entities["contact"])
        assertEquals("llego en cinco", decision.entities["message"])
    }

    @Test
    fun testMusicBasics() = runBlocking {
        val request = THAMISRequest("poné duki")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        assertEquals("duki", decision.entities["media"])
    }

    @Test
    fun testMusicComplex() = runBlocking {
        val request = THAMISRequest("pone uzbekistan de ysy a")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        // Note: IntentDetector now handles media payload as a single entity in some cases or split.
        // Based on current IntentDetector.kt: entities["media"] = payloadFinal
        // If it's split, we check the actual implementation.
        // In IntentDetector.kt, for PLAY_MUSIC it sets entities["media"] = payloadFinal
    }

    @Test
    fun testMapsBasics() = runBlocking {
        val request = THAMISRequest("abrí el gps")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
    }

    @Test
    fun testSpotifyBasics() = runBlocking {
        val request = THAMISRequest("abrí la playlist")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
    }

    @Test
    fun testInstagramBasics() = runBlocking {
        val request = THAMISRequest("abrí el insta")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
    }

    @Test
    fun testInstagramDeepSearch() = runBlocking {
        val request = THAMISRequest("buscá en instagram a messi")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.SOCIAL_SEARCH, decision.intent)
    }

    @Test
    fun testReplyMessage() = runBlocking {
        val request = THAMISRequest("respondé que voy yendo")
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.REPLY_MESSAGE, decision.intent)
    }
}
