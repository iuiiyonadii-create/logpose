package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.debt.TechnicalDebtEngine
import com.thamis.lab.orchestrator.bus.EngineeringCommand
import com.thamis.lab.orchestrator.bus.EngineeringCommandBus
import com.thamis.lab.orchestrator.kernel.ThamisPlatformKernel
import com.thamis.lab.orchestrator.ops.EngineeringOperationsCenter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts340Test {

    @Test
    fun testCommandBusTechnicalDebtOpsCenterAndPlatformKernel() {
        val bus = EngineeringCommandBus()
        val debtEngine = TechnicalDebtEngine()
        val opsCenter = EngineeringOperationsCenter()
        val kernel = ThamisPlatformKernel()

        val dummyCmd = object : EngineeringCommand {
            override val commandId: String = "cmd-1"
            override val commandName: String = "TestCommand"
        }
        val dispatchRes = bus.dispatchCommand(dummyCmd)
        assertTrue(dispatchRes.isSuccess)

        val debtReport = debtEngine.calculateTechnicalDebt()
        assertEquals(0.0, debtReport.totalTechnicalDebtScore, 0.01)

        val opsStatus = opsCenter.inspectOperationsStatus()
        assertTrue(opsStatus.isOpsCenterActive)

        val kernelReport = kernel.verifyKernelHealth()
        assertNotNull(kernelReport)
        assertTrue(kernelReport.isKernelOperational)
        assertEquals(100.0, kernelReport.kernelHealthScore, 0.01)
    }
}
