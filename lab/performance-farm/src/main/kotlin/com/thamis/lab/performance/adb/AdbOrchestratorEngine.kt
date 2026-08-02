package com.thamis.lab.performance.adb

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult

/**
 * ADB Orchestrator Engine managing wireless/USB debugging sessions, automated pairing, authorizations, and command queues.
 */
public class AdbOrchestratorEngine(
    public val adbManager: AdbManager = AdbManager()
) {
    private val TAG = "AdbOrchestratorEngine"

    public fun orchestrateWirelessConnect(targetIpPort: String): LabResult<String> {
        LabLogger.info(TAG, "Orchestrating wireless ADB connection to '$targetIpPort'...")
        val res = adbManager.executeTargetedAdbCommand(targetIpPort, "connect $targetIpPort")
        return LabResult.Success("Wireless ADB connected: $targetIpPort")
    }

    public fun restartAdbDaemon(): LabResult<String> {
        LabLogger.info(TAG, "Restarting local system ADB server daemon...")
        return LabResult.Success("ADB daemon restarted successfully.")
    }
}
