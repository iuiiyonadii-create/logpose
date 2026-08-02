package com.thamis.lab.intelligence

import com.thamis.lab.intelligence.connector.AntigravityProviderConnector
import com.thamis.lab.intelligence.reasoning.AiReasoningEngine
import com.thamis.lab.intelligence.repair.SelfRepairEngine
import com.thamis.lab.intelligence.training.LogPoseTrainingEngine
import com.thamis.lab.performance.analyzer.PerformanceLab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterExpansionTest {

    @Test
    fun testLogPoseTrainingEngineAndKnowledgeGraph() {
        val engine = LogPoseTrainingEngine()
        engine.recordExecution("exec-101", "Log trace clean", hasCrash = false, hasAnr = false)
        engine.recordExecution("exec-102", "Fatal ANR detected", hasCrash = false, hasAnr = true)

        val graph = engine.generateKnowledgeGraph()
        assertNotNull(graph)
        assertEquals(2, graph.totalExecutionsIndexed)
        assertEquals(1, graph.totalAnrsIndexed)
        assertTrue(graph.projectHealthScore > 0.0)
    }

    @Test
    fun testPerformanceLabAndReasoning() {
        val perfLab = PerformanceLab()
        val reasoning = AiReasoningEngine()
        val repair = SelfRepairEngine()

        val perf = perfLab.analyzePerformance("TKDMZPZDZ5MR8XNV")
        assertNotNull(perf)
        assertEquals(2.4, perf.cpuUsagePercent, 0.01)

        val rec = reasoning.reasonAboutArchitecture(":lab:orchestrator", "Clean Architecture layer boundary")
        assertNotNull(rec)
        assertEquals("HIGH", rec.estimatedImpact)

        val repResult = repair.attemptRepair("Build failure", "Trace")
        assertNotNull(repResult)
        assertTrue(repResult.isSuccess)
    }

    @Test
    fun testAiProviderConnector() {
        val connector = AntigravityProviderConnector()
        assertEquals("Google Antigravity", connector.providerName)

        val res = connector.analyzeTask("Verify 10 modules")
        assertTrue(res.isSuccess)
    }
}
