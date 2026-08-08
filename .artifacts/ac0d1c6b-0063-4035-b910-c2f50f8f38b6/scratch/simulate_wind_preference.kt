package com.thamis.lab.orchestrator

import com.uriel.logpose.thamis.language.PhoneticEngine
import com.thamis.lab.core.common.telemetry.LabTelemetry

fun main() {
    println("--- SIMULANDO RÁFAGA DE VIENTO (140 KM/H) ---")
    val extremeNoise = 0.9f
    
    val targetStop = "detener musica"
    val heardStop = "ditinir musicV" // Distorsión por ruido
    
    val targetPass = "pasar musica"
    val heardPass = "pVsVr mVsIcV"
    
    println("\n1. Probando comando 'Detener' (ALF-R v4.0):")
    val keyStop = PhoneticEngine.getPhoneticKey(heardStop, extremeNoise)
    val targetKeyStop = PhoneticEngine.getPhoneticKey(targetStop, extremeNoise)
    val scoreStop = PhoneticEngine.similarity(heardStop, targetStop, extremeNoise)
    
    println("He escuchado: '$heardStop' -> Key: $keyStop")
    println("Objetivo: '$targetStop' -> Key: $targetKeyStop")
    println("Confianza de Match: $scoreStop")

    println("\n2. Probando comando 'Pasar' (Riesgo de colisión):")
    val keyPass = PhoneticEngine.getPhoneticKey(heardPass, extremeNoise)
    val scorePass = PhoneticEngine.similarity(heardPass, targetPass, extremeNoise)
    println("He escuchado: '$heardPass' -> Key: $keyPass")
    println("Confianza de Match: $scorePass")
    
    if (scoreStop > scorePass) {
        println("\n✅ RESULTADO: El sistema prefiere 'DETENER' por estabilidad estructural.")
    } else {
        println("\n⚠️ ADVERTENCIA: Aún existe riesgo de colisión fonética.")
    }
}
