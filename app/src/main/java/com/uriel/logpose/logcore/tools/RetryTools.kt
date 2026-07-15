package com.uriel.logpose.logcore.tools

import kotlinx.coroutines.delay

/**
 * Utilidades de reintento, en variantes bloqueantes y suspendibles.
 */
object RetryTools {

    /**
     * Reintenta [block] hasta [maxAttempts] veces (bloqueante), devolviendo el primer resultado
     * exitoso. Lanza la última excepción si todos los intentos fallan.
     */
    fun <T> retry(maxAttempts: Int, block: (attempt: Int) -> T): T {
        require(maxAttempts > 0) { "maxAttempts debe ser mayor que 0" }
        var lastError: Throwable? = null
        for (attempt in 1..maxAttempts) {
            try {
                return block(attempt)
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IllegalStateException("retry falló sin excepción registrada")
    }

    /**
     * Reintenta [block] (suspendible) hasta [maxAttempts] veces, esperando [delayMillis]
     * entre cada intento. Lanza la última excepción si todos los intentos fallan.
     */
    suspend fun <T> retryWithDelay(maxAttempts: Int, delayMillis: Long, block: suspend (attempt: Int) -> T): T {
        require(maxAttempts > 0) { "maxAttempts debe ser mayor que 0" }
        var lastError: Throwable? = null
        for (attempt in 1..maxAttempts) {
            try {
                return block(attempt)
            } catch (e: Exception) {
                lastError = e
                if (attempt < maxAttempts) delay(delayMillis)
            }
        }
        throw lastError ?: IllegalStateException("retryWithDelay falló sin excepción registrada")
    }

    /**
     * Reintenta [block] (suspendible) hasta que devuelva `true` o se alcance [maxAttempts],
     * esperando [delayMillis] entre cada intento. Devuelve `true` si tuvo éxito.
     */
    suspend fun retryUntil(maxAttempts: Int, delayMillis: Long, block: suspend (attempt: Int) -> Boolean): Boolean {
        require(maxAttempts > 0) { "maxAttempts debe ser mayor que 0" }
        for (attempt in 1..maxAttempts) {
            if (block(attempt)) return true
            if (attempt < maxAttempts) delay(delayMillis)
        }
        return false
    }
}
