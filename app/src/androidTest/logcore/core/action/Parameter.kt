package com.uriel.logpose.logcore.core.action

/**
 * Representa un parámetro extraído de una orden de voz.
 *
 * Ejemplos:
 * - volumen = 7
 * - contacto = Juan
 * - mensaje = "Llego en cinco"
 */
data class Parameter(

    val name: String,

    val value: String
)