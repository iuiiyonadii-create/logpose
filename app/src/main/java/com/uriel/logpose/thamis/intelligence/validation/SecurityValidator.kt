package com.uriel.logpose.thamis.intelligence.validation

/**
 * FASE 27.0 — THAMIS AUTONOMOUS ENGINEERING CORE
 * FASE 15: SECURITY VALIDATOR
 */
object SecurityValidator {

    fun validateSecurity(plan: String): Boolean {
        return !plan.contains("Unsafe")
    }
}
