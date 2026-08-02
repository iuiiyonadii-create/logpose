package com.thamis.lab.orchestrator

import com.thamis.lab.orchestrator.platform.ThamisEvolutionPlatformCore
import com.thamis.lab.performance.chaos.ChaosResilienceEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Prompts221To260MasterTest {

    @Test
    fun testChaosResilienceAndThamisEvolutionPlatformCore() {
        val chaos = ChaosResilienceEngine()
        val platformCore = ThamisEvolutionPlatformCore()

        val chaosReport = chaos.executeChaosFaultInjection()
        assertEquals(100.0, chaosReport.resilienceScore, 0.01)
        assertEquals(5, chaosReport.recoveredFailuresCount)

        val platformStatus = platformCore.verifyEvolutionPlatform()
        assertNotNull(platformStatus)
        assertTrue(platformStatus.isPlatformOperational)
        assertEquals(100.0, platformStatus.masterQualityIndex, 0.01)
    }
}
