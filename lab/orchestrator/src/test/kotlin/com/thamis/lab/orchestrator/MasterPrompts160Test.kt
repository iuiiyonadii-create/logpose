package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.regression.LogPoseRegressionEngine
import com.thamis.lab.orchestrator.command.EngineeringCommandCenter
import com.thamis.lab.orchestrator.nextgen.ThamisNextGenCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts160Test {

    @Test
    fun testNextGenCoreCommandCenterAndLogPoseRegression() {
        val nextGenCore = ThamisNextGenCore()
        val commandCenter = EngineeringCommandCenter()
        val regressionEngine = LogPoseRegressionEngine()

        val nextGenStatus = nextGenCore.verifyNextGenReadiness()
        assertTrue(nextGenStatus.isNextGenReady)
        assertEquals(100.0, nextGenStatus.nextGenArchitectureScore, 0.01)

        val ccStatus = commandCenter.getCommandCenterStatus()
        assertNotNull(ccStatus)
        assertEquals(10, ccStatus.activeModulesCount)

        val regReport = regressionEngine.auditLogPoseVersion("v2.0.4")
        assertTrue(regReport.isReleaseSafe)
        assertEquals(0, regReport.detectedRegressionsCount)
    }
}
