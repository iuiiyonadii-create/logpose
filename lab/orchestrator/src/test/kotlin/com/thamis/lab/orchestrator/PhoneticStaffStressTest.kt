package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.engineering.PhoneticVulnerabilityHunter
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneticStaffStressTest {

    @Test
    fun testDeepPhoneticCollisionDetection() {
        val hunter = PhoneticVulnerabilityHunter()
        
        // Simulación de un diccionario con palabras peligrosamente similares en ruido
        val dictionary = mapOf(
            "bajar volumen" to "VOLUME_DOWN",
            "parar musica" to "MEDIA_STOP",
            "pasar musica" to "MEDIA_NEXT", // Riesgo de colisión con 'parar' bajo ruido extremo
            "llamar casa" to "CALL_CONTACT"
        )
        
        val collisions = hunter.findCollisions(dictionary)
        
        println("COLISIONES FONÉTICAS DETECTADAS (ALF-R v4.0):")
        collisions.forEach { 
            println("🚨 RIESGO: '${it.intentA}' y '${it.intentB}' colisionan en la llave: ${it.commonKey}")
        }
        
        // Esperamos encontrar al menos una colisión en este dataset de prueba
        // 'parar' -> pVrVr -> bVrVr
        // 'pasar' -> pVsVr -> bVsVr
        // Con colapso de vocales (a/o -> V) y consonantes (p/b -> b):
        // ambos pueden terminar en una estructura similar.
        assertTrue("Debería detectar debilidades fonéticas", collisions.isNotEmpty())
    }
}
