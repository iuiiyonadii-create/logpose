package com.uriel.logpose.logcore.tools

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Utilidades para trabajar con [CompletableFuture].
 */
object FutureTools {

    /** Crea un [CompletableFuture] ya completado con [value]. */
    fun <T> completed(value: T): CompletableFuture<T> = CompletableFuture.completedFuture(value)

    /** Ejecuta [block] de forma asíncrona y devuelve un [CompletableFuture] con su resultado. */
    fun <T> async(block: () -> T): CompletableFuture<T> = CompletableFuture.supplyAsync(block)

    /**
     * Espera el resultado de [future] hasta [timeoutMillis] milisegundos.
     * Devuelve `null` si expira el tiempo o si [future] termina con excepción.
     */
    fun <T> awaitOrNull(future: CompletableFuture<T>, timeoutMillis: Long): T? {
        return try {
            future.get(timeoutMillis, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /** Combina dos futuros, aplicando [combiner] a ambos resultados cuando estén disponibles. */
    fun <A, B, R> combine(
        futureA: CompletableFuture<A>,
        futureB: CompletableFuture<B>,
        combiner: (A, B) -> R
    ): CompletableFuture<R> {
        return futureA.thenCombine(futureB, combiner)
    }

    /** Devuelve un future que se completa cuando todos los [futures] se completan. */
    fun allOf(futures: List<CompletableFuture<*>>): CompletableFuture<Void> {
        return CompletableFuture.allOf(*futures.toTypedArray())
    }
}
