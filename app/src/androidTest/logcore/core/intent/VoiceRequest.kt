package com.uriel.logpose.logcore.core.intent

/**
 * Representa una solicitud recibida desde el motor de voz.
 *
 * Esta clase es independiente del proveedor de reconocimiento
 * y contiene únicamente la información necesaria para que
 * LogCore interprete la intención del usuario.
 */
data class VoiceRequest(

    /**
     * Texto reconocido.
     */
    val text: String,

    /**
     * Nivel de confianza del reconocimiento.
     * Valor entre 0.0 y 1.0.
     */
    val confidence: Float
)