package com.thamis.lab.orchestrator

import com.thamis.lab.orchestrator.campaign.RealWorldCampaignTemplates
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.performance.telemetry.AppCrashLog
import com.thamis.lab.performance.telemetry.AppCrashLogMonitor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionReadinessTest {

    @Test
    fun testRealWorldCampaignsExecution() {
        val orchestrator = UnifiedSimulationOrchestrator()
        val device = DeviceInfo("dev-real-1", "Moto G84 5G", isEmulator = false, state = DeviceState.ONLINE)

        val btCampaign = RealWorldCampaignTemplates.createBluetoothCampaign()
        val btResult = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", btCampaign)

        assertTrue(btResult.isSuccess)
        val btReport = btResult.getOrNull()!!
        assertEquals(2, btReport.campaignSummary.totalScenarios)

        val gpsCampaign = RealWorldCampaignTemplates.createGpsCampaign()
        val gpsResult = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", gpsCampaign)
        assertTrue(gpsResult.isSuccess)
    }

    @Test
    fun testStressTestExecution1000Scenarios() {
        val orchestrator = UnifiedSimulationOrchestrator()
        val device = DeviceInfo("dev-stress-1", "Pixel 7 Pro", isEmulator = true, state = DeviceState.ONLINE)

        val count = 1000
        val stressCampaign = RealWorldCampaignTemplates.createStressTestCampaign(count)

        val startTime = System.currentTimeMillis()
        val result = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", stressCampaign)
        val duration = System.currentTimeMillis() - startTime

        assertTrue(result.isSuccess)
        val report = result.getOrNull()!!

        assertEquals(count, report.campaignSummary.totalScenarios)
        assertEquals(count, report.campaignSummary.passedScenarios)
        assertEquals(100.0, report.overallQualityScore, 0.01)
        assertTrue("Execution of 1,000 scenarios must complete in under 2000ms", duration < 2000)
    }

    @Test
    fun testAppCrashAndAnrMonitoring() {
        val monitor = AppCrashLogMonitor()
        val devId = "dev-crash-1"

        assertFalse(monitor.hasAnrOrCrash(devId))

        monitor.recordCrash(AppCrashLog(timestampMs = 1000L, deviceId = devId, packageName = "com.uriel.logpose", exceptionType = "NullPointerException", stackTrace = "at com.uriel.logpose.MainActivity"))

        assertTrue(monitor.hasAnrOrCrash(devId))
        assertEquals(1, monitor.getCrashesForDevice(devId).size)
    }
}
