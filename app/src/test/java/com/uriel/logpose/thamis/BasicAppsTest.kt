package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class BasicAppsTest {

    @Test
    fun testWhatsAppBasics() {
        val request = THAMISRequest("mandale un wasap a mama que diga llego en cinco")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.SEND_MESSAGE, decision.intent)
        assertEquals("mama", decision.entities["contact"])
        assertEquals("llego en cinco", decision.entities["message"])
    }

    @Test
    fun testMusicBasics() {
        val request = THAMISRequest("poné duki")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        assertEquals("duki", decision.entities["media"])
    }

    @Test
    fun testMusicComplex() {
        val request = THAMISRequest("pone uzbekistan de ysy a")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        assertEquals("uzbekistan", decision.entities["track"])
        assertEquals("ysy a", decision.entities["artist"])
    }

    @Test
    fun testMapsBasics() {
        val request = THAMISRequest("abrí el gps")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
        assertEquals("com.google.android.apps.maps", decision.entities["app_name"])
    }

    @Test
    fun testSpotifyBasics() {
        val request = THAMISRequest("abrí la playlist")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
        assertEquals("com.spotify.music", decision.entities["app_name"])
    }

    @Test
    fun testInstagramBasics() {
        val request = THAMISRequest("abrí el insta")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.OPEN_APP, decision.intent)
        assertEquals("instagram", decision.entities["app_name"])
    }

    @Test
    fun testInstagramDeepSearch() {
        val request = THAMISRequest("buscá en instagram a messi")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.SOCIAL_SEARCH, decision.intent)
        assertEquals("messi", decision.entities["parameter"])
    }

    @Test
    fun testReplyMessage() {
        val request = THAMISRequest("respondé que voy yendo")
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.REPLY_MESSAGE, decision.intent)
        assertEquals("voy yendo", decision.entities["parameter"])
    }
}
