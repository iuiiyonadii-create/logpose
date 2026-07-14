package com.uriel.logpose.logcore.orchestrator.validator

import com.uriel.logpose.logcore.core.action.ActionRequest

/**
 * Responsable de validar si una ActionRequest puede ejecutarse.
 *
 * Actualmente toda acción es válida.
 * En futuras versiones se agregarán reglas como:
 *
 * - Permisos
 * - Bluetooth conectado
 * - GPS disponible
 * - Sesión activa
 * - Estado del dispositivo
 * - Restricciones del usuario
 */
class ActionValidator {

    fun validate(request: ActionRequest): ValidationResult {
        return ValidationResult.Valid
    }
}

/**
 * Resultado de una validación.
 */
sealed class ValidationResult {

    /**
     * La acción puede ejecutarse.
     */
    data object Valid : ValidationResult()

    /**
     * La acción fue rechazada.
     */
    data class Invalid(
        val reason: String
    ) : ValidationResult()
}