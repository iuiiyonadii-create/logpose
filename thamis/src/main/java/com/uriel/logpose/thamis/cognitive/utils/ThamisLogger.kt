package com.uriel.logpose.thamis.cognitive.utils

/**
 * Interfaz pura de logging para el núcleo cognitivo.
 * Evita dependencias de android.util.Log.
 */
interface ThamisLogger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Proveedor global de logger para el cerebro.
 * La infraestructura (app) debe inyectar una implementación real al inicio.
 */
object ThamisLogProvider {
    var logger: ThamisLogger? = null
}
