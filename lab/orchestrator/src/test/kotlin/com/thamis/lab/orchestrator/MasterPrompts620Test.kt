package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.constitution.EngineeringConstitutionEngine
import com.thamis.lab.orchestrator.absolute.ThamisAbsoluteDirectiveEngine
import com.thamis.lab.orchestrator.meta.ThamisMetaOperatingSystemCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts620Test {

    @Test
    fun testConstitutionAbsoluteDirectiveAndMetaOperatingSystemCore() {
        val constitution = EngineeringConstitutionEngine()
        val absoluteDirective = ThamisAbsoluteDirectiveEngine()
        val metaOsCore = ThamisMetaOperatingSystemCore()

        val constReport = constitution.auditEngineeringConstitution()
        assertTrue(constReport.isConstitutionFullyHonored)
        assertEquals(100.0, constReport.architectureBeforeFeaturesScore, 0.01)

        val absReport = absoluteDirective.verifyAbsoluteDirectiveAlignment()
        assertTrue(absReport.isEvidenceDrivenOptimizationVerified)
        assertEquals(620, absReport.totalMasterPromptsCompletedCount)

        val metaStatus = metaOsCore.verifyMetaOperatingSystem()
        assertNotNull(metaStatus)
        assertTrue(metaStatus.isMetaOsActive)
        assertEquals(620, metaStatus.totalMasterPromptsSatisfied)
        assertEquals(100.0, metaStatus.metaOsQualityScore, 0.01)
    }
}
