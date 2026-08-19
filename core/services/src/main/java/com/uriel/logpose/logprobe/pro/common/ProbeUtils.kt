package com.uriel.logpose.logprobe.common

object ProbeUtils {

    /**
     * Normaliza un nombre de paquete para comparaciones consistentes
     * (lowercase, sin espacios accidentales).
     */
    fun normalizePackage(packageName: String?): String =
        packageName?.trim()?.lowercase().orEmpty()

    fun safeTrim(text: CharSequence?, maxLength: Int = 4000): String {
        val value = text?.toString().orEmpty()
        return if (value.length > maxLength) value.substring(0, maxLength) + "…" else value
    }

    fun newEventId(): String = java.util.UUID.randomUUID().toString()
}
