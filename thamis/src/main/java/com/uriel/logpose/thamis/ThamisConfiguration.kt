package com.uriel.logpose.thamis

/**
 * Feature Flags para el motor cognitivo THAMIS v3.0.
 * Permite una activación progresiva y segura de las capacidades del cerebro.
 */
object ThamisConfiguration {
    var shadowMode: Boolean = false // Si es true, Thamis v3 no toma control, solo observa.
    var authorityEnabled: Boolean = true // Flag global de autoridad activado para validación 13.1
    
    // Músculos activados para THAMIS v3.0 (Fase 15: MULTIMEDIA y NAVIGATION)
    var musicControlEnabled: Boolean = true
    var callsEnabled: Boolean = false
    var navigationEnabled: Boolean = true

    // --- FASE 16: Optimización Fonética ---
    var useAdvancedLanguageEngine: Boolean = true // Activa Metaphone Rioplatense y N-Gramas
    
    // Versión del motor de razonamiento
    const val ENGINE_VERSION = "3.0.5-pro"
}
