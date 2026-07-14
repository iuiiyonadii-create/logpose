package com.uriel.logpose.thamis.decision

/**
 * Evidencia utilizada por THAMIS para construir una hipótesis.
 */
data class DecisionEvidence(

    /**
     * Frase del usuario.
     */
    val input: String,

    /**
     * Regla de conocimiento que coincidió.
     */
    val matchedPhrase: String,

    /**
     * Dominio del conocimiento.
     */
    val source: String,

    /**
     * Similitud obtenida.
     */
    val similarity: Float

)