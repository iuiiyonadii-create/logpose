package com.thamis.lab.orchestrator

import com.thamis.lab.orchestrator.enterprise.ThamisEnterprisePipeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EnterprisePipelineTest {

    @Test
    fun testThamisEnterprisePipelineFullFlow() {
        val pipeline = ThamisEnterprisePipeline()
        val devId = "TKDMZPZDZ5MR8XNV"

        val report = pipeline.runEnterpriseCampaign(devId)

        assertNotNull(report)
        assertTrue(report.isSuccess)
        assertEquals(100.0, report.overallQualityScore, 0.01)
        assertNotNull(report.certificate)
        assertEquals("PASSED", report.certificate.statusBadge)
        assertTrue(report.certificate.complianceSummary.contains("12,600 scenarios"))
    }
}
