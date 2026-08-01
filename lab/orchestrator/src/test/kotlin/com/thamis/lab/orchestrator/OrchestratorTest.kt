package com.thamis.lab.orchestrator

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.orchestrator.campaign.TestCampaign
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.simulation.scenario.ScenarioBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrchestratorTest {

    @Test
    fun testEndToEndUnifiedOrchestratorFlow() {
        val orchestrator = UnifiedSimulationOrchestrator()

        // 1. Detect Device
        val device = DeviceInfo("emulator-5554", "Pixel 7 Pro", isEmulator = true, state = DeviceState.ONLINE)

        // 2. Build Campaign with Scenarios
        val scen1 = ScenarioBuilder("scen-1", "Play Music Test")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.TextCommandEvent("e-1", 100L, userText = "poné música"))
            .expectedIntent("PLAY_MUSIC")
            .build()

        val campaign = TestCampaign("camp-1", "Full LogPose Verification", listOf(scen1))

        // 3. Execute End-to-End Flow (Detect -> Install APK -> Sim Context -> Run Scenario -> AI Analysis -> Report)
        val result = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", campaign)

        assertTrue(result.isSuccess)
        val report = result.getOrNull()!!

        assertNotNull(report)
        assertEquals(1, report.campaignSummary.totalScenarios)
        assertEquals(1, report.campaignSummary.passedScenarios)
        assertEquals(100.0, report.overallQualityScore, 0.01)
        assertTrue(report.markdownReport.contains("THAMIS Lab Intelligence Report"))
    }
}
