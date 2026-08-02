package com.thamis.lab.evidence

import com.thamis.lab.evidence.release.ReleaseReadinessEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseReadinessTest {

    @Test
    fun testReleaseReadinessEngine() {
        val readinessEngine = ReleaseReadinessEngine()
        val report = readinessEngine.calculateReleaseReadiness()

        assertNotNull(report)
        assertEquals(100.0, report.releaseReadinessScore, 0.01)
        assertTrue(report.isLogPoseCertified)
    }
}
