package com.uriel.logpose.logcore.tools

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Utilidades de bajo nivel para ejecución en hilos usando `java.util.concurrent`.
 * No depende de `android.os.Handler` ni de Looper de Android: para ejecutar en el
 * hilo principal de una app Android, ver [AndroidTools].
 */
object ThreadTools {

    private val backgroundExecutor: ScheduledExecutorService by lazy {
        Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
        )
    }

    /** Ejecuta [block] en un hilo del pool de background compartido de este objeto. */
    fun background(block: () -> Unit) {
        backgroundExecutor.execute(block)
    }

    /** Ejecuta [block] en el hilo actual de forma síncrona (útil como no-op explícito para el "hilo main"). */
    fun main(block: () -> Unit) {
        block()
    }

    /** Ejecuta [block] luego de [delayMillis] milisegundos, en el pool de background. */
    fun delay(delayMillis: Long, block: () -> Unit) {
        backgroundExecutor.schedule(block, delayMillis, TimeUnit.MILLISECONDS)
    }

    /** Indica si el hilo actual coincide con [thread]. */
    fun isCurrentThread(thread: Thread): Boolean = Thread.currentThread() === thread

    /** Devuelve el nombre del hilo actual. */
    fun currentThreadName(): String = Thread.currentThread().name
}
