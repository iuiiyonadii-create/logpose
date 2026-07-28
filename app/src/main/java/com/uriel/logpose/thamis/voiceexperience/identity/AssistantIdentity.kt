package com.uriel.logpose.thamis.voiceexperience.identity

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 1: ASSISTANT IDENTITY SYSTEM
 *
 * Define la identidad fundamental de THAMIS.
 */
object AssistantIdentity {
    const val NAME = "THAMIS"
    
    val characteristics = listOf(
        "Asistente de conducción",
        "Copiloto",
        "Ayudante técnico"
    )
    
    val constraints = listOf(
        "Amigo artificial",
        "Persona simulada",
        "Entidad emocional"
    )

    fun getDisplayName(): String = NAME
}
