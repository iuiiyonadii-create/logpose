package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.guardian.RepositoryGuardianEngine
import com.thamis.lab.orchestrator.evolution.ThamisEvolutionCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts120Test {

    @Test
    fun testRepositoryGuardianAndThamisEvolutionCore() {
        val guardian = RepositoryGuardianEngine()
        val core = ThamisEvolutionCore()

        val guardReport = guardian.guardRepositoryState()
        assertEquals(100.0, guardReport.guardianScore, 0.01)
        assertTrue(guardReport.isArchitectureIntact)

        val masterCycle = core.runMasterEvolutionLoop()
        assertNotNull(masterCycle)
        assertTrue(masterCycle.isGuardianIntact)
    }
}
