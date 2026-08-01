package com.thamis.ui.missioncontrol

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.orchestrator.FullOrchestrationReport
import com.thamis.lab.performance.device.DeviceInfo

public data class ComboBoxItem(
    public val displayName: String,
    public val targetSerial: String
)

public data class DashboardState(
    public val activeDevicesCount: Int,
    public val comboBoxItems: List<ComboBoxItem> = emptyList(),
    public val selectedDeviceSerial: String? = null,
    public val totalCampaignsExecuted: Int = 0,
    public val latestQualityScore: Double = 0.0,
    public val isRunning: Boolean = false
)

/**
 * Main Dashboard Controller for Mission Control UI managing device selector ComboBox items.
 */
public class MainDashboardController {
    private val TAG = "MainDashboardController"
    private var state = DashboardState(activeDevicesCount = 0)

    public val currentState: DashboardState get() = state

    public fun updateDevices(devices: List<DeviceInfo>) {
        LabLogger.info(TAG, "Updating ComboBox selector with ${devices.size} devices...")

        val items = devices.map { dev ->
            ComboBoxItem(displayName = dev.displayName, targetSerial = dev.adbTargetSerial)
        }

        val selected = if (items.isNotEmpty()) {
            state.selectedDeviceSerial?.takeIf { serial -> items.any { it.targetSerial == serial } } ?: items.first().targetSerial
        } else null

        state = state.copy(
            activeDevicesCount = devices.size,
            comboBoxItems = items,
            selectedDeviceSerial = selected
        )

        for (item in items) {
            LabLogger.info(TAG, "ComboBox Option -> '${item.displayName}' [serial=${item.targetSerial}]")
        }
        LabLogger.info(TAG, "Selected Option -> '$selected'")
    }

    public fun selectDeviceBySerial(targetSerial: String) {
        if (state.comboBoxItems.any { it.targetSerial == targetSerial }) {
            state = state.copy(selectedDeviceSerial = targetSerial)
            LabLogger.info(TAG, "User selected device serial: $targetSerial")
        }
    }

    public fun onReportGenerated(report: FullOrchestrationReport) {
        state = state.copy(
            totalCampaignsExecuted = state.totalCampaignsExecuted + 1,
            latestQualityScore = report.overallQualityScore,
            isRunning = false
        )
    }
}
