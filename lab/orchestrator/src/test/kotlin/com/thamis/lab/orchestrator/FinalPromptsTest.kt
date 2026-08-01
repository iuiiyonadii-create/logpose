package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.optimization.OptimizationEngine
import com.thamis.lab.intelligence.roadmap.RoadmapEngine
import com.thamis.lab.intelligence.roadmap.RoadmapMilestone
import com.thamis.lab.intelligence.security.SecurityAuditEngine
import com.thamis.lab.orchestrator.release.ReleasePipelineEngine
import com.thamis.lab.orchestrator.release.ReleaseStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FinalPromptsTest {

    @Test
    fun testSecurityReleaseRoadmapAndOptimization() {
        val secEngine = SecurityAuditEngine()
        val relEngine = ReleasePipelineEngine()
        val roadmap = RoadmapEngine()
        val optimizer = OptimizationEngine()

        val secReport = secEngine.executeSecurityAudit()
        assertTrue(secReport.isSecretsSafe)
        assertEquals(100.0, secReport.securityScore, 0.01)

        val relReport = relEngine.prepareRelease("v1.2.0", ReleaseStage.STABLE)
        assertNotNull(relReport)
        assertTrue(relReport.isBuildVerified)

        roadmap.addMilestone(RoadmapMilestone("m1", "Master System Release", 1, "LOW", "HIGH", true))
        assertEquals(1, roadmap.getActiveRoadmap().size)

        val optReport = optimizer.benchmarkAndOptimize(":lab:orchestrator")
        assertNotNull(optReport)
        assertTrue(optReport.throughputGainPercent > 0.0)
    }
}
