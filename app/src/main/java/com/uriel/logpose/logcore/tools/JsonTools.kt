package com.uriel.logpose.logcore.tools

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Utilidades puras de JSON basadas en `org.json`, disponible de forma nativa en Android
 * (no se agrega ninguna librería externa nueva).
 *
 * Nota: estas funciones cubren el caso de uso general de LogPose (objetos y arrays simples).
 * Para modelos de datos complejos con serialización tipada, cada módulo puede optar por
 * su propia solución sin que eso afecte a este módulo base.
 */
object JsonTools {

    /**
     * Serializa un [Map] a una cadena JSON.
     */
    fun serialize(map: Map<String, Any?>): String {
        return JSONObject(map).toString()
    }

    /**
     * Serializa una [List] a una cadena JSON (array).
     */
    fun serializeList(list: List<Any?>): String {
        return JSONArray(list).toString()
    }

    /**
     * Deserializa una cadena JSON de objeto a un [Map], o `null` si el JSON es inválido.
     */
    fun deserialize(json: String): Map<String, Any?>? {
        return try {
            val obj = JSONObject(json)
            jsonObjectToMap(obj)
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Deserializa una cadena JSON de array a una [List], o `null` si el JSON es inválido.
     */
    fun deserializeList(json: String): List<Any?>? {
        return try {
            val array = JSONArray(json)
            jsonArrayToList(array)
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Formatea [json] con sangría de [indent] espacios para hacerlo legible.
     * Devuelve el texto original si no es JSON válido.
     */
    fun prettyPrint(json: String, indent: Int = 2): String {
        return try {
            when {
                json.trim().startsWith("[") -> JSONArray(json).toString(indent)
                else -> JSONObject(json).toString(indent)
            }
        } catch (e: JSONException) {
            json
        }
    }

    /**
     * Indica si [json] es un texto JSON válido (objeto o array).
     */
    fun isValidJson(json: String): Boolean {
        return try {
            when {
                json.trim().startsWith("[") -> {
                    JSONArray(json)
                    true
                }
                else -> {
                    JSONObject(json)
                    true
                }
            }
        } catch (e: JSONException) {
            false
        }
    }

    private fun jsonObjectToMap(obj: JSONObject): Map<String, Any?> {
        val map = LinkedHashMap<String, Any?>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = convertValue(obj.get(key))
        }
        return map
    }

    private fun jsonArrayToList(array: JSONArray): List<Any?> {
        return (0 until array.length()).map { convertValue(array.get(it)) }
    }

    private fun convertValue(value: Any): Any? {
        return when (value) {
            JSONObject.NULL -> null
            is JSONObject -> jsonObjectToMap(value)
            is JSONArray -> jsonArrayToList(value)
            else -> value
        }
    }
}
