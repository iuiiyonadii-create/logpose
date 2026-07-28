package com.uriel.logpose.thamis.personalization.validation

import com.uriel.logpose.thamis.personalization.monitor.PersonalizationEngine
import com.uriel.logpose.thamis.personalization.preference.PreferenceManager
import com.uriel.logpose.thamis.personalization.model.PreferenceType

/**
 * Suite de simulación para validar la estabilidad de las preferencias.
 */
class PersonalizationStressTest {

    fun runScenario() {
        PersonalizationEngine.initialize()
        
        // Simular 100 cambios rápidos de preferencia
        repeat(100) { i ->
            PreferenceManager.setPreference(PreferenceType.VOICE_STYLE, "STYLE_$i", 0.5f)
        }

        PersonalizationEngine.applyPersonalization("Hola, ¿cómo estás?")
    }
}
