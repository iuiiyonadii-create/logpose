package com.uriel.logpose.thamis.communication

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * ContactNormalizer: Inyector de alias y fix para nombres cortos/deformados.
 * v1.0: Resolución de hardware hallucinations (ymk, el gordo).
 */
object ContactNormalizer {

    private val CONTACTS_FONETICO = mapOf(
        "y m k" to "ymk",
        "i m k" to "ymk",
        "y meca" to "ymk",
        "gregorio" to "ymk",
        "el gordo" to "brian",
        "gordo" to "brian",
        "la vieja" to "mamá",
        "el viejo" to "papá"
    )

    /**
     * Normaliza el contacto detectado unificando fonemas rotos y aplicando alias.
     */
    fun normalize(rawContact: String): String {
        val clean = rawContact.lowercase().trim()
        
        // 1. Check de match directo en el mapa de alias
        CONTACTS_FONETICO[clean]?.let { 
            LogPoseLogger.d("ContactNormalizer: Alias detectado -> $it")
            return it 
        }

        // 2. Fix para deletreo de iniciales (ej: "y m k" -> "ymk")
        if (clean.contains(" ")) {
            val collapsed = clean.replace(" ", "")
            if (CONTACTS_FONETICO.containsKey(collapsed) || collapsed == "ymk") {
                return "ymk"
            }
        }

        return clean
    }
}
