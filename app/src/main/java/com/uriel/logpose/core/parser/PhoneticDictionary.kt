package com.uriel.logpose.core.parser

import android.content.Context
import org.json.JSONObject

/**
 * PhoneticDictionary.kt
 * ----------------------
 * Carga logpose_glosario.json (en /assets) y expone helpers de matching.
 */
class PhoneticDictionary(context: Context) {

    private val root: JSONObject

    init {
        val jsonString = context.assets.open("logpose_glosario.json").bufferedReader().use { it.readText() }
        root = JSONObject(jsonString)
    }

    private fun listaDe(path: String): List<String> {
        val partes = path.split(".")
        var nodo: JSONObject = root
        for (i in 0 until partes.size - 1) {
            nodo = nodo.getJSONObject(partes[i])
        }
        val arr = nodo.getJSONArray(partes.last())
        return (0 until arr.length()).map { arr.getString(it) }
    }

    fun matchesAny(texto: String, path: String): Boolean {
        val t = texto.lowercase()
        return try {
            listaDe(path).any { alias -> t.contains(alias.lowercase()) }
        } catch (e: Exception) {
            false
        }
    }

    fun resolverApp(texto: String): String? {
        val apps = root.getJSONObject("apps")
        val t = texto.lowercase()
        for (key in apps.keys()) {
            val arr = apps.getJSONArray(key)
            for (i in 0 until arr.length()) {
                if (t.contains(arr.getString(i).lowercase())) return key
            }
        }
        return null
    }

    fun limpiarMuletillas(texto: String): String {
        var t = texto.lowercase()
        val muletillas = listaDe("muletillas_a_ignorar")
        for (m in muletillas) {
            t = t.replace(m.lowercase(), " ")
        }
        return t.replace(Regex("\\s+"), " ").trim()
    }

    fun corregirTitulo(texto: String): String {
        val t = texto.lowercase()
        for (categoria in listOf("titulos_netflix", "artistas")) {
            if (!root.has(categoria)) continue
            val nodo = root.getJSONObject(categoria)
            for (correcto in nodo.keys()) {
                val alias = nodo.getJSONArray(correcto)
                for (i in 0 until alias.length()) {
                    if (t.contains(alias.getString(i).lowercase())) return correcto
                }
            }
        }
        return texto
    }

    fun contieneWakeWord(texto: String): Boolean = matchesAny(texto, "control.logpose_alias")
}
