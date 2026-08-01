package com.thamis.lab.evidence

import com.thamis.lab.evidence.gate.QualityGateEngine
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QualityGateTest {

    @Test
    fun testQualityGateEngine() {
        val gateEngine = QualityGateEngine()
        val report = gateEngine.evaluateQualityGates("task-999")

        assertNotNull(report)
        assertTrue(report.isTaskComplete)
        assertTrue(report.compilationPassed)
        assertTrue(report.architecturePassed)
    }
}
