package com.thamis.lab.orchestrator

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.intelligence.analytics.EngineeringAnalytics
import com.thamis.lab.intelligence.bug.BugIntelligenceEngine
import com.thamis.lab.intelligence.bug.BugRecord
import com.thamis.lab.intelligence.graph.GraphNode
import com.thamis.lab.intelligence.graph.KnowledgeGraphEngine
import com.thamis.lab.orchestrator.loop.AutonomousEngineeringLoop
import com.thamis.lab.orchestrator.sdk.PluginSdk
import com.thamis.lab.orchestrator.sdk.ThamisPlugin
import com.thamis.lab.simulation.scenario.ScenarioBuilder
import com.thamis.lab.simulation.twin.LogPoseDigitalTwin
import com.thamis.lab.simulation.voice.AdvancedVoiceLab
import com.thamis.lab.simulation.voice.VoiceProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FullSystemMasterTest {

    @Test
    fun testVoiceLabAndDigitalTwin() {
        val voiceLab = AdvancedVoiceLab()
        val digitalTwin = LogPoseDigitalTwin()

        val profile = VoiceProfile("es-AR", "MALE", "NORMAL", "MOTORCYCLE_HELMET")
        val voiceRes = voiceLab.testVoiceProfile(profile, "poné música")
        assertNotNull(voiceRes)
        assertTrue(voiceRes.recognitionAccuracyPercent > 90.0)

        val scen = ScenarioBuilder("scen-1", "Test Scenario").build()
        val pred = digitalTwin.predictScenarioExecution(scen)
        assertNotNull(pred)
        assertEquals("scen-1", pred.scenarioId)
    }

    @Test
    fun testBugIntelligenceKnowledgeGraphAndAnalytics() {
        val bugEngine = BugIntelligenceEngine()
        val graphEngine = KnowledgeGraphEngine()
        val analytics = EngineeringAnalytics()

        bugEngine.registerBug(BugRecord("bug-1", "fp1", "AUDIO", "HIGH", 3, "LOW", 4.5))
        val backlog = bugEngine.getPrioritizedBacklog()
        assertEquals(1, backlog.size)

        graphEngine.addNode(GraphNode("node-1", "MODULE", ":lab:orchestrator"))
        assertEquals(1, graphEngine.getTotalNodeCount())

        val kpis = analytics.generateKpiDashboard()
        assertEquals(100.0, kpis.compilationSuccessRatePercent, 0.01)
    }

    @Test
    fun testPluginSdkAndAutonomousLoop() {
        val sdk = PluginSdk()
        val loop = AutonomousEngineeringLoop()

        val dummyPlugin = object : ThamisPlugin {
            override val pluginId: String = "test-plugin"
            override val pluginVersion: String = "1.0.0"
            override val capabilities: List<String> = listOf("SIMULATION")
            override fun initialize(): LabResult<String> = LabResult.Success("OK")
        }

        val regRes = sdk.registerPlugin(dummyPlugin)
        assertTrue(regRes.isSuccess)
        assertEquals(1, sdk.getRegisteredPlugins().size)

        val cycle = loop.executeImprovementCycle()
        assertNotNull(cycle)
        assertTrue(cycle.isArchitectureValid)
    }
}
