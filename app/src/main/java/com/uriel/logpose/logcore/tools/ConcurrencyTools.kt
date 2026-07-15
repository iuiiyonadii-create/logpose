package com.uriel.logpose.logcore.tools

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Utilidades de concurrencia genéricas basadas en `java.util.concurrent`.
 */
object ConcurrencyTools {

    /** Ejecuta [block] protegido por [lock], liberando el lock incluso si [block] lanza una excepción. */
    inline fun <T> withLock(lock: ReentrantLock, block: () -> T): T {
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    /** Crea un [AtomicInteger] inicializado en [initial]. */
    fun atomicInt(initial: Int = 0): AtomicInteger = AtomicInteger(initial)

    /** Crea un [AtomicLong] inicializado en [initial]. */
    fun atomicLong(initial: Long = 0L): AtomicLong = AtomicLong(initial)

    /** Crea un [AtomicBoolean] inicializado en [initial]. */
    fun atomicBoolean(initial: Boolean = false): AtomicBoolean = AtomicBoolean(initial)

    /**
     * Ejecuta [block] solo si [flag] pasa de `false` a `true` (operación atómica de una sola vez).
     * Devuelve `true` si [block] se ejecutó.
     */
    inline fun runOnce(flag: AtomicBoolean, block: () -> Unit): Boolean {
        val shouldRun = flag.compareAndSet(false, true)
        if (shouldRun) block()
        return shouldRun
    }
}
