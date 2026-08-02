package com.thamis.lab.simulation.validation

import com.thamis.lab.performance.adb.RealLogcatEngine
import com.thamis.lab.performance.logpose.RealLogposeController

public data class RealValidationReport(
    public val targetSerial: String,
    public val isProcessAlive: Boolean,
    public val pid: Int,
    public val hasAnr: Boolean,
    public val hasCrash: Boolean,
    public val isForegroundServiceActive: Boolean,
    public val qualityScore: Double
)

/**
 * Real Validation Engine inspecting live process status, ANRs, crashes, and process health post-execution.
 */
public class RealValidationEngine(
    public val logposeController: RealLogposeController = RealLogposeController(),
    public val logcatEngine: RealLogcatEngine = RealLogcatEngine()
) {
    public fun validateDeviceState(targetSerial: String): RealValidationReport {
        val pid = logposeController.fetchProcessPid(targetSerial)
        val isAlive = pid > 0
        val crashes = logcatEngine.parseCrashesAndAnrs(targetSerial)

        val hasAnr = crashes.any { it.isAnr }
        val hasCrash = crashes.any { !it.isAnr }
        val isFg = if (isAlive) logposeController.checkIsForeground(targetSerial) else false

        val score = when {
            hasCrash || hasAnr -> 0.0
            !isAlive -> 50.0
            else -> 100.0
        }

        return RealValidationReport(
            targetSerial = targetSerial,
            isProcessAlive = isAlive,
            pid = pid,
            hasAnr = hasAnr,
            hasCrash = hasCrash,
            isForegroundServiceActive = isFg,
            qualityScore = score
        )
    }
}
