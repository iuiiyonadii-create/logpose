package com.thamis.ui.missioncontrol

import com.thamis.lab.orchestrator.FullOrchestrationReport
import com.thamis.lab.orchestrator.campaign.CampaignExecutionSummary
import com.thamis.lab.performance.device.ConnectionType
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import org.junit.Assert.assertEquals
import org.junit.Test

class MissionControlTest {

    @Test
    fun testMainDashboardControllerStateUpdates() {
        val dashboard = MainDashboardController()
        val usbDev = DeviceInfo("TKDMZPZDZ5MR8XNV", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.USB, rawModel = "2409BRN2CA")
        val wifiDev = DeviceInfo("adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.WIFI, rawModel = "2409BRN2CA")

        dashboard.updateDevices(listOf(usbDev, wifiDev))
        assertEquals(2, dashboard.currentState.activeDevicesCount)
        assertEquals(2, dashboard.currentState.comboBoxItems.size)
        assertEquals("2409BRN2CA (USB)", dashboard.currentState.comboBoxItems[0].displayName)
        assertEquals("2409BRN2CA (WiFi)", dashboard.currentState.comboBoxItems[1].displayName)

        // User selects WiFi device in UI ComboBox
        dashboard.selectDeviceBySerial(wifiDev.deviceId)
        assertEquals(wifiDev.deviceId, dashboard.currentState.selectedDeviceSerial)

        val summary = CampaignExecutionSummary("camp-1", 1, 1, 0, emptyList())
        val report = FullOrchestrationReport(summary, "# Report", 100.0)

        dashboard.onReportGenerated(report)
        assertEquals(1, dashboard.currentState.totalCampaignsExecuted)
        assertEquals(100.0, dashboard.currentState.latestQualityScore, 0.01)
    }
}
