package com.uriel.logpose.logcore.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Wrappers finos sobre `kotlinx.coroutines`, sin ninguna lógica de negocio.
 * Requiere la dependencia `kotlinx-coroutines-core` (ver README, sección "Dependencias").
 */
object CoroutineTools {

    /** Ejecuta [block] en el dispatcher de IO ([Dispatchers.IO]). */
    suspend fun <T> onIO(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.IO, block)

    /** Ejecuta [block] en el dispatcher de cómputo ([Dispatchers.Default]). */
    suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.Default, block)

    /** Ejecuta [block] en el dispatcher principal ([Dispatchers.Main]). */
    suspend fun <T> onMain(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.Main, block)

    /** Suspende la corrutina actual durante [millis] milisegundos sin bloquear el hilo. */
    suspend fun delaySafe(millis: Long) {
        if (millis > 0) delay(millis)
    }

    /**
     * Ejecuta [block] con un límite de tiempo de [timeoutMillis] milisegundos.
     * Devuelve `null` si expira el tiempo, en lugar de lanzar [TimeoutCancellationException].
     */
    suspend fun <T> withTimeoutOrNullSafe(timeoutMillis: Long, block: suspend CoroutineScope.() -> T): T? {
        return withTimeoutOrNull(timeoutMillis, block)
    }

    /** Lanza [block] como una tarea asíncrona dentro de [scope], devolviendo su [Deferred]. */
    fun <T> asyncIn(scope: CoroutineScope, block: suspend CoroutineScope.() -> T): Deferred<T> {
        return scope.async(block = block)
    }
}
