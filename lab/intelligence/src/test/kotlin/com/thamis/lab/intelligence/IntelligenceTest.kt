package com.thamis.lab.intelligence

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.explanation.CognitiveExplanation
import com.thamis.lab.intelligence.analysis.AiAnalysisEngine
import com.thamis.lab.intelligence.graph.GraphEdge
import com.thamis.lab.intelligence.graph.GraphNode
import com.thamis.lab.intelligence.graph.KnowledgeGraph
import com.thamis.lab.intelligence.pattern.PatternRecognitionEngine
import com.thamis.lab.intelligence.quality.QualityEngine
import com.thamis.lab.intelligence.recommendation.RecommendationEngine
import com.thamis.lab.intelligence.regression.RegressionDetector
import com.thamis.lab.intelligence.reports.IntelligenceReportGenerator
import com.thamis.lab.intelligence.rootcause.RootCauseEngine
import com.thamis.lab.simulation.validation.ScenarioExecutionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IntelligenceTest {

    @Test
    fun testAiAnalysisEngineAndQualityEngine() {
        val aiEngine = AiAnalysisEngine()
        val qualityEngine = QualityEngine()

        val execResult = ScenarioExecutionResult("scen-1", isPassed = true, actualIntentMatched = "PLAY_MUSIC", expectedIntent = "PLAY_MUSIC", executionDurationMs = 25L)
        val decision = CognitiveDecision("PLAY_MUSIC", 0.95, "MEDIA_PLAY", true, CognitiveExplanation("Trace", emptyList(), emptyList(), 0.95))

        val analysis = aiEngine.analyzeExecution(execResult, listOf(decision))
        assertTrue(analysis.isStable)
        assertTrue(analysis.performanceScore > 0.9)

        val score = qualityEngine.calculateScores(passedScenarios = 9, totalScenarios = 10, faultScenariosPassed = 4, totalFaultScenarios = 5)
        assertEquals(90.0, score.reliabilityScore, 0.01)
        assertEquals(80.0, score.robustnessScore, 0.01)
    }

    @Test
    fun testRootCauseAndPatternRecognition() {
        val rootEngine = RootCauseEngine()
        val patternEngine = PatternRecognitionEngine()
        val recEngine = RecommendationEngine()

        val events = listOf(
            LabEvent.TextCommandEvent("e-1", 100L, userText = "cmd"),
            LabEvent.FaultInjectedEvent("f-1", 200L, faultType = "BLUETOOTH_DROP", targetComponent = "BluetoothManager")
        )

        val rootReport = rootEngine.analyzeRootCause(events, "Bluetooth disconnected")
        assertEquals("f-1", rootReport.rootEventId)
        assertEquals("HIGH", rootReport.impactLevel)

        val clusters = patternEngine.clusterFailures(listOf("BLUETOOTH_DROP error", "Expected intent mismatch"))
        assertEquals(2, clusters.size)

        val recommendations = recEngine.generateRecommendations(clusters)
        assertTrue(recommendations.isNotEmpty())
    }

    @Test
    fun testRegressionDetectorAndKnowledgeGraph() {
        val detector = RegressionDetector()
        val graph = KnowledgeGraph()

        val regression = detector.compareVersions(95.0, 90.0, 10L, 15L)
        assertTrue(regression.isRegressionDetected)

        graph.addNode(GraphNode("err-1", "Error", "BT_DISCONNECT"))
        graph.addNode(GraphNode("scen-1", "Scenario", "Riding Scenario"))
        graph.addEdge(GraphEdge("err-1", "scen-1", "OCCURRED_IN"))

        assertEquals(2, graph.totalNodes())
        assertEquals(1, graph.totalEdges())

        val related = graph.getRelatedNodes("err-1")
        assertEquals(1, related.size)
        assertEquals("Riding Scenario", related[0].label)
    }

    @Test
    fun testIntelligenceReportGenerator() {
        val generator = IntelligenceReportGenerator()
        val qualityEngine = QualityEngine()

        val score = qualityEngine.calculateScores(10, 10, 5, 5)
        val markdown = generator.generateMarkdownReport(score, emptyList())

        assertNotNull(markdown)
        assertTrue(markdown.contains("THAMIS Lab Intelligence Report"))
        assertTrue(markdown.contains("100.00"))
    }
}
