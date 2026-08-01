package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.learning.HistoricalExecutionRecord
import com.thamis.lab.intelligence.learning.HistoricalLearningStore
import com.thamis.lab.orchestrator.campaign.AutonomousCampaignEngine
import com.thamis.lab.performance.command.RealCommandRunner
import com.thamis.lab.simulation.validation.RealValidationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutonomousCampaignTest {

    @Test
    fun testRealCommandRunnerAndValidationEngine() {
        val runner = RealCommandRunner()
        val validationEngine = RealValidationEngine()
        val devId = "emulator-5554"

        val openRecord = runner.openApp(devId)
        assertNotNull(openRecord)
        assertEquals("adb -s $devId shell am start -n com.uriel.logpose/.MainActivity", openRecord.commandString)

        val closeRecord = runner.closeApp(devId)
        assertNotNull(closeRecord)

        val validation = validationEngine.validateDeviceState(devId)
        assertNotNull(validation)
    }

    @Test
    fun testAutonomousCampaignPipelineAndLearningStore() {
        val engine = AutonomousCampaignEngine()
        val learningStore = HistoricalLearningStore()
        val devId = "TKDMZPZDZ5MR8XNV"

        val report = engine.runAutonomousPipeline(devId, "poné música")
        assertNotNull(report)
        assertTrue(report.campaignId.startsWith("auto-"))

        val record = HistoricalExecutionRecord(
            recordId = report.evidence.evidenceUuid,
            timestampMs = report.evidence.timestampMs,
            qualityScore = report.qualityScore,
            executionDurationMs = report.evidence.executionDurationMs,
            isSuccess = report.isSuccess
        )

        learningStore.recordCampaignExecution(record)
        val insight = learningStore.generateInsights()

        assertEquals(1, insight.totalCampaignsAnalyzed)
        assertTrue(insight.averageQualityScore > 0.0)
    }
}
