package com.uriel.logpose.thamis.intelligence.actuation

/**
 * Representa el resultado de una operación de actuación sobre el sistema de archivos.
 */
sealed class ActuationResult {
    data class Success(
        val path: String,
        val type: ActuationType
    ) : ActuationResult()

    data class Failure(
        val path: String,
        val errorMessage: String,
        val errorType: FailureType
    ) : ActuationResult()
}

enum class ActuationType {
    NEW_FILE,
    OVERWRITE
}

enum class FailureType {
    SECURITY_VIOLATION,
    FILE_EXISTS,
    IO_ERROR,
    VALIDATION_ERROR
}
