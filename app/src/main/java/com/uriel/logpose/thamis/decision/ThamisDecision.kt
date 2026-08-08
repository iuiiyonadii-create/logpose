package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Representa el veredicto final de THAMIS tras evaluar evidencias y contexto.
 */
data class ThamisDecision(
    val type: DecisionType,
    val intent: Intent,
    val entities: Map<String, String>,
    val confidence: Float,
    val evidence: List<Evidence>,
    val contextUsed: Map<String, String>,
    val reason: String = "Normal",
    val timestamp: Long = System.currentTimeMillis()
)

enum class DecisionType {
    EXECUTE,    // Confianza alta: Acción directa
    CONFIRM,    // Confianza media: Preguntar al usuario
    IGNORE,     // Confianza baja o ruido: Silencio
    DENY        // Entendido pero bloqueado por seguridad
}
