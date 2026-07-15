package com.uriel.logpose.logcore.tools

/**
 * Utilidades para inspección y manejo seguro de excepciones.
 */
object ExceptionTools {

    /** Devuelve el mensaje de [throwable], o [default] si es `null` o está vacío. */
    fun messageOrDefault(throwable: Throwable, default: String = "Unknown error"): String {
        return throwable.message?.takeIf { it.isNotBlank() } ?: default
    }

    /** Devuelve la causa raíz de [throwable], siguiendo la cadena de `cause`. */
    fun rootCause(throwable: Throwable): Throwable {
        var current = throwable
        while (current.cause != null && current.cause !== current) {
            current = current.cause!!
        }
        return current
    }

    /** Convierte el stack trace de [throwable] en una cadena de texto. */
    fun stackTraceAsString(throwable: Throwable): String {
        return throwable.stackTraceToString()
    }

    /**
     * Ejecuta [block] capturando cualquier excepción; devuelve el resultado o [default] si falla.
     */
    inline fun <T> runCatchingOrDefault(default: T, block: () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            default
        }
    }

    /** Indica si [throwable] es (o desciende de) alguno de los tipos en [types]. */
    fun isOneOf(throwable: Throwable, vararg types: Class<out Throwable>): Boolean {
        return types.any { it.isInstance(throwable) }
    }
}
