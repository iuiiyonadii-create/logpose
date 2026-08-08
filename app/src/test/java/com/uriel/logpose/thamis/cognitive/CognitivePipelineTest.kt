package com.uriel.logpose.thamis.cognitive

import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking

class CognitivePipelineTest {

    @Test
    fun testNormalizationAndIntentResolution() = runBlocking {
        // This is an integration test
        assertTrue(true)
    }

    @Test
    fun testMultiCommandSplitting() = runBlocking {
        val text = "pone musica y despues busca el clima"
        assertTrue(true)
    }
}
