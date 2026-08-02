package com.thamis.lab.orchestrator

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.orchestrator.campaign.RealWorldCampaignTemplates
import com.thamis.lab.orchestrator.campaign.TestCampaign
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.performance.telemetry.HardwareTelemetry
import com.thamis.lab.performance.telemetry.HardwareTelemetryCollector
import com.thamis.lab.simulation.scenario.ScenarioBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RealWorldValidationProgramTest {

    @Test
    fun testAndroidVersionsEmulatorMatrix() {
        val orchestrator = UnifiedSimulationOrchestrator()
        val androidVersions = listOf(8, 9, 10, 11, 12, 13, 14, 15)

        for (apiLevel in androidVersions) {
            val devId = "emulator-api-$apiLevel"
            val device = DeviceInfo(devId, "Android $apiLevel Emulator", isEmulator = true, state = DeviceState.ONLINE, apiLevel = apiLevel)

            val campaign = RealWorldCampaignTemplates.createBluetoothCampaign()
            val result = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", campaign)

            assertTrue("Campaign must pass on Android API $apiLevel", result.isSuccess)
            val report = result.getOrNull()!!
            assertEquals(2, report.campaignSummary.totalScenarios)
        }
    }

    @Test
    fun testMultiVendorDeviceMatrix() {
        val orchestrator = UnifiedSimulationOrchestrator()
        val vendors = listOf(
            DeviceInfo("samsung-s23", "Samsung Galaxy S23", isEmulator = false, state = DeviceState.ONLINE, apiLevel = 33),
            DeviceInfo("moto-g84", "Motorola Moto G84", isEmulator = false, state = DeviceState.ONLINE, apiLevel = 34),
            DeviceInfo("xiaomi-13", "Xiaomi Redmi Note 13", isEmulator = false, state = DeviceState.ONLINE, apiLevel = 34),
            DeviceInfo("pixel-8", "Google Pixel 8 Pro", isEmulator = false, state = DeviceState.ONLINE, apiLevel = 34)
        )

        val campaign = RealWorldCampaignTemplates.createGpsCampaign()

        for (device in vendors) {
            val result = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", campaign)
            assertTrue("Campaign must pass on vendor ${device.modelName}", result.isSuccess)
        }
    }

    @Test
    fun testUltraHighVolumeStressTest100000Scenarios() {
        val orchestrator = UnifiedSimulationOrchestrator()
        val device = DeviceInfo("stress-master", "High-Performance Node", isEmulator = true, state = DeviceState.ONLINE)

        val count = 100000
        val scenarios = mutableListOf<com.thamis.lab.simulation.scenario.Scenario>()

        for (i in 1..count) {
            val scen = ScenarioBuilder("ultra-$i", "Ultra Scenario $i")
                .initialSnapshot(CognitiveSnapshot(timestampMs = i.toLong()))
                .addEvent(LabEvent.TextCommandEvent("e-$i", i.toLong(), userText = "poné música"))
                .expectedIntent("PLAY_MUSIC")
                .build()
            scenarios.add(scen)
        }

        val campaign = TestCampaign("ultra-stress", "100k Scenarios Stress Test", scenarios)

        val startTime = System.currentTimeMillis()
        val result = orchestrator.runEndToEndCampaign(device, "/tmp/logpose.apk", campaign)
        val duration = System.currentTimeMillis() - startTime

        assertTrue(result.isSuccess)
        val report = result.getOrNull()!!

        assertEquals(count, report.campaignSummary.totalScenarios)
        assertEquals(count, report.campaignSummary.passedScenarios)
        assertEquals(100.0, report.overallQualityScore, 0.01)
        assertTrue("Execution of 100,000 scenarios must complete in under 5000ms", duration < 5000)
    }

    @Test
    fun testRealHardwareTelemetryRecording() {
        val collector = HardwareTelemetryCollector()
        val devId = "pixel-8-real"

        collector.recordTelemetry(HardwareTelemetry(timestampMs = 1000L, deviceId = devId, cpuPercent = 12.5, ramUsedMb = 145.0, gpuPercent = 5.0, networkBytesPerSec = 1024L))
        collector.recordTelemetry(HardwareTelemetry(timestampMs = 2000L, deviceId = devId, cpuPercent = 18.0, ramUsedMb = 160.0, gpuPercent = 8.0, networkBytesPerSec = 2048L))

        assertEquals(15.25, collector.getAverageCpuUsage(devId), 0.01)
        assertEquals(160.0, collector.getPeakRamUsageMb(devId), 0.01)
    }
}
