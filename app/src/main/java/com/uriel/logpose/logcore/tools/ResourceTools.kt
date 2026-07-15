package com.uriel.logpose.logcore.tools

/**
 * Utilidades para el manejo seguro de recursos que implementan [AutoCloseable].
 */
object ResourceTools {

    /**
     * Abre múltiples recursos [AutoCloseable] y garantiza que todos se cierren al finalizar
     * [block], incluso si alguno de los cierres o el propio [block] lanza una excepción.
     */
    inline fun <T> useAll(vararg resources: AutoCloseable, block: () -> T): T {
        try {
            return block()
        } finally {
            for (resource in resources) {
                StreamTools.closeQuietly(resource)
            }
        }
    }

    /**
     * Ejecuta [block] sobre [resource] y lo cierra automáticamente al finalizar,
     * sin propagar excepciones producidas durante el cierre.
     */
    inline fun <T : AutoCloseable, R> withResource(resource: T, block: (T) -> R): R {
        try {
            return block(resource)
        } finally {
            StreamTools.closeQuietly(resource)
        }
    }
}
