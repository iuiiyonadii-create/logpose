package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.security.ThamisAuthorityGate

import com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory

/**
 * Validador de autoridad para el dominio de navegación.
 */
object NavigationAuthorityValidator {

    fun validate(decision: ThamisDecision, worldState: WorldState): ValidationResult {
        // 1. Verificar Feature Flag global y local
        if (!ThamisConfiguration.authorityEnabled || !ThamisConfiguration.navigationEnabled) {
            return ValidationResult.DENY("Authority disabled")
        }

        // 2. Verificar AuthorityGate
        if (!ThamisAuthorityGate.canExecute(ThamisAuthorityGate.Domain.NAVIGATION)) {
            return ValidationResult.DENY("Gate blocked")
        }

        // 3. Confianza mínima
        val confidence = decision.winningEvaluation?.finalScore ?: 0f
        if (confidence < 0.90f) {
            return ValidationResult.CONFIRM_REQUIRED("Confidence below 0.90 ($confidence)")
        }

        // 4. GPS Disponible
        if (!worldState.driving.gpsAvailable) {
            return ValidationResult.DENY("GPS not available")
        }

        // 5. Sin llamada activa
        if (worldState.system.activeCall) {
            return ValidationResult.DENY("Active call restriction")
        }

        // 6. Sin alerta crítica
        if (worldState.system.hasCriticalAlert) {
            return ValidationResult.DENY("Critical alert active")
        }

        // 7. Provider Disponible
        val provider = NavigationProviderFactory.getBestAvailableProvider()
        if (!provider.isAvailable()) {
            return ValidationResult.DENY("No available provider")
        }

        // 8. Destino ambiguo (Simulado - basado en una entidad hipotética 'is_ambiguous')
        val isAmbiguous = decision.winningEvaluation?.hypothesis?.entities?.get("is_ambiguous") == "true"
        if (isAmbiguous) {
            return ValidationResult.CONFIRM_REQUIRED("Ambiguous destination")
        }

        // 9. Seguridad por velocidad
        val speed = worldState.driving.speedKmh
        return when {
            speed > 120 -> ValidationResult.DENY("Speed > 120km/h unsafe for route start")
            speed > 100 -> ValidationResult.CONFIRM_REQUIRED("High speed confirmation required")
            else -> ValidationResult.APPROVED
        }
    }

    sealed class ValidationResult {
        object APPROVED : ValidationResult()
        data class CONFIRM_REQUIRED(val reason: String) : ValidationResult()
        data class DENY(val reason: String) : ValidationResult()
    }
}
