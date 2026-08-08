package com.uriel.logpose.core.parser

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import org.json.JSONArray
import org.json.JSONObject

/**
 * Cargador dinámico del Unified Language Core (logpose_glosario.json).
 */
class PhoneticDictionary(context: Context) {

    private val root: JSONObject

    init {
        val jsonString = context.assets.open("logpose_glosario.json").bufferedReader().use { it.readText() }
        root = JSONObject(jsonString)
    }

    /**
     * Devuelve una lista de strings de cualquier sección del glosario (usando dot notation).
     * Si es un objeto, recopila recursivamente todos los valores de tipo String o JSONArray.
     */
    fun listaDe(path: String): List<String> {
        return try {
            val partes = path.split(".")
            var nodo: Any = root
            for (parte in partes) {
                if (nodo is JSONObject) {
                    nodo = nodo.get(parte)
                }
            }
            collectValues(nodo)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun collectValues(nodo: Any): List<String> {
        val result = mutableListOf<String>()
        when (nodo) {
            is JSONArray -> {
                for (i in 0 until nodo.length()) {
                    result.add(nodo.getString(i))
                }
            }
            is JSONObject -> {
                val keys = nodo.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = nodo.get(key)
                    result.addAll(collectValues(value))
                    // También agregamos la llave por si acaso
                    result.add(key)
                }
            }
            is String -> result.add(nodo)
        }
        return result.distinct()
    }

    fun mapaDe(path: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            val partes = path.split(".")
            var nodo: Any = root
            for (parte in partes) {
                if (nodo is JSONObject) {
                    nodo = nodo.get(parte)
                }
            }
            if (nodo is JSONObject) {
                val keys = nodo.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    result[key] = nodo.getString(key)
                }
            }
        } catch (e: Exception) {}
        return result
    }

    fun matchesAny(input: String, path: String): Boolean {
        return listaDe(path).any { it.equals(input, ignoreCase = true) }
    }

    fun resolverApp(path: String): String? {
        val apps = root.getJSONObject("apps")
        val keys = apps.keys()
        while (keys.hasNext()) {
            val appName = keys.next()
            val synonyms = apps.getJSONArray(appName)
            for (i in 0 until synonyms.length()) {
                if (synonyms.getString(i).equals(path, ignoreCase = true)) return appName
            }
        }
        return null
    }

    fun limpiarMuletillas(text: String): String {
        var clean = text.lowercase().trim()
        val muletillas = listaDe("muletillas_a_ignorar")
        for (m in muletillas) {
            clean = clean.replace(Regex("\\b$m\\b"), "").trim()
        }
        return clean.replace(Regex("\\s+"), " ")
    }

    fun corregirTitulo(text: String): String {
        val correcciones = mapaDe("musica.correcciones_foneticas")
        var result = text.lowercase()
        for ((err, fixed) in correcciones) {
            if (result.contains(err)) {
                result = result.replace(err, fixed)
            }
        }
        return result
    }

    fun contieneWakeWord(text: String): Boolean = listaDe("fonetica.wake_words").any { text.contains(it, ignoreCase = true) }
}
