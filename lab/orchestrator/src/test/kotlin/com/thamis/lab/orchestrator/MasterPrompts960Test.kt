package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.civilization.EngineeringCivilizationEngine
import com.thamis.lab.orchestrator.charter.ThamisEternalEngineeringCharterEngine
import com.thamis.lab.orchestrator.civilization.ThamisEngineeringCivilizationCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPrompts960Test {

    @Test
    fun testEngineeringCivilizationEternalCharterAndCivilizationCore() {
        val civilization = EngineeringCivilizationEngine()
        val eternalCharter = ThamisEternalEngineeringCharterEngine()
        val civilizationCore = ThamisEngineeringCivilizationCore()

        val civReport = civilization.auditEngineeringCivilization()
        assertTrue(civReport.isCivilizationEnduring)
        assertEquals(960, civReport.knowledgeAssetsCount)

        val charterReport = eternalCharter.auditEternalCharter()
        assertTrue(charterReport.isEnduringEngineeringCivilizationVerified)
        assertEquals(960, charterReport.totalMasterPromptsCompletedCount)

        val status = civilizationCore.verifyEngineeringCivilization()
        assertNotNull(status)
        assertTrue(status.isCivilizationCoreActive)
        assertEquals(960, status.totalMasterPromptsCompletedCount)
        assertEquals(100.0, status.civilizationQualityScore, 0.01)
    }
}
