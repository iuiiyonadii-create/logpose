package com.thamis.lab.evidence

import com.thamis.lab.evidence.quality.MasterQualityIndexCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MasterQualityIndexTest {

    @Test
    fun testMasterQualityIndexCalculator() {
        val calculator = MasterQualityIndexCalculator()
        val report = calculator.calculateMasterQualityIndex()

        assertNotNull(report)
        assertEquals(100.0, report.overallMasterScore, 0.01)
        assertEquals(100.0, report.engineeringQualityIndex, 0.01)
    }
}
