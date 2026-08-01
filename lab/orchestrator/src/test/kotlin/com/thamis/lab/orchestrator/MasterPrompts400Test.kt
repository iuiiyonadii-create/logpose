package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.dna.RepositoryDnaEngine
import com.thamis.lab.orchestrator.singularity.ThamisEngineeringSingularityCore
import com.thamis.lab.orchestrator.singularity.ThamisPrimeDirectiveEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts400Test {

    @Test
    fun testRepositoryDnaPrimeDirectiveAndSingularityCore() {
        val dnaEngine = RepositoryDnaEngine()
        val primeEngine = ThamisPrimeDirectiveEngine()
        val singularityCore = ThamisEngineeringSingularityCore()

        val dna = dnaEngine.generateRepositoryDna()
        assertEquals(100.0, dna.cleanArchitectureComplianceScore, 0.01)
        assertTrue(dna.zeroTechnicalDebtVerified)

        val primeReport = primeEngine.auditPrimeDirectiveAlignment()
        assertTrue(primeReport.isLogPoseImprovementAligned)
        assertEquals("com.uriel.logpose", primeReport.targetPackage)

        val status = singularityCore.verifySingularityStatus()
        assertNotNull(status)
        assertTrue(status.isSingularityAchieved)
        assertEquals(400, status.totalMasterPromptsSatisfiedCount)
        assertEquals(100.0, status.singularityQualityScore, 0.01)
    }
}
