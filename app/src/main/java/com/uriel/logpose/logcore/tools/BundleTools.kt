package com.uriel.logpose.logcore.tools

import android.os.Bundle

/**
 * Utilidades para leer/escribir [Bundle] de forma nula-segura.
 * Requiere el SDK de Android para compilar.
 */
object BundleTools {

    /** Devuelve el [String] asociado a [key], o [default] si no existe o [bundle] es `null`. */
    fun getString(bundle: Bundle?, key: String, default: String = ""): String {
        return bundle?.getString(key) ?: default
    }

    /** Devuelve el [Int] asociado a [key], o [default] si no existe o [bundle] es `null`. */
    fun getInt(bundle: Bundle?, key: String, default: Int = 0): Int {
        return bundle?.getInt(key, default) ?: default
    }

    /** Devuelve el [Boolean] asociado a [key], o [default] si no existe o [bundle] es `null`. */
    fun getBoolean(bundle: Bundle?, key: String, default: Boolean = false): Boolean {
        return bundle?.getBoolean(key, default) ?: default
    }

    /** Devuelve el [Long] asociado a [key], o [default] si no existe o [bundle] es `null`. */
    fun getLong(bundle: Bundle?, key: String, default: Long = 0L): Long {
        return bundle?.getLong(key, default) ?: default
    }

    /** Construye un [Bundle] a partir de un mapa de pares clave-valor primitivos soportados. */
    fun of(vararg pairs: Pair<String, Any?>): Bundle {
        val bundle = Bundle()
        for ((key, value) in pairs) {
            when (value) {
                null -> bundle.putString(key, null)
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                else -> throw IllegalArgumentException("Tipo no soportado para la clave '$key': ${value::class}")
            }
        }
        return bundle
    }

    /** Combina dos bundles; en caso de claves repetidas gana [override]. */
    fun merge(base: Bundle?, override: Bundle?): Bundle {
        val result = Bundle()
        base?.let { result.putAll(it) }
        override?.let { result.putAll(it) }
        return result
    }
}
