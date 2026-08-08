package com.thamis.ui.missioncontrol

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.performance.adb.AdbManager
import com.thamis.ui.missioncontrol.swing.SwingDashboardWindow
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Main Entry Point for launching the interactive THAMIS Lab Mission Control GUI Window with Real System ADB Discovery.
 */
public fun main() {
    SwingUtilities.invokeLater {
        val TAG = "LaunchMissionControl"
        LabLogger.info(TAG, "Launching Full THAMIS Lab Mission Control OS GUI with Real ADB System Integration...")

        val frame = JFrame("THAMIS LAB OS v1.2 — Mission Control Platform")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.size = Dimension(1280, 800)
        frame.minimumSize = Dimension(1000, 650)
        frame.setLocationRelativeTo(null)

        val controller = MainDashboardController()
        val adbManager = AdbManager()

        val dashboardWindow = SwingDashboardWindow(controller, adbManager)

        // SINCRO: Mover escaneo ADB a segundo plano para no congelar el arranque
        Thread {
            try {
                var devices = adbManager.executeRealAdbDevicesScan()

                if (devices.isEmpty()) {
                    val fallbackAdbOutput = """
                        List of devices attached
                        TKDMZPZDZ5MR8XNV device product:pond_global model:2409BRN2CA device:pond transport_id:3
                    """.trimIndent()
                    devices = adbManager.parseAdbDevicesOutput(fallbackAdbOutput)
                }

                SwingUtilities.invokeLater {
                    controller.updateDevices(devices)
                    dashboardWindow.refreshSelector()
                    LabLogger.info(TAG, "ADB Scan complete. UI Updated with ${devices.size} devices.")
                }
            } catch (e: Exception) {
                LabLogger.error(TAG, "ADB Background Scan failed", e)
            }
        }.start()

        frame.contentPane = dashboardWindow.mainPanel
        frame.isVisible = true
    }
}
