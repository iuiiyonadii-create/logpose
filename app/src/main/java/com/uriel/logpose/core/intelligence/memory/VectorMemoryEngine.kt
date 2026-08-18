package com.uriel.logpose.core.intelligence.memory

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * VectorMemoryEngine v1.1: Memoria Semántica Staff (RAG Local).
 * v1.1: Enriquecimiento de contexto para música y navegación rioplatense.
 */
object VectorMemoryEngine {

    private val semanticMap = mutableMapOf<String, String>()

    fun initialize(context: Context) {
        LogPoseLogger.i("VectorMemory", "🚀 Inicializando Memoria Semántica (Enhanced context).")
        seedInitialMemory()
    }

    private fun seedInitialMemory() {
        // --- SECCIÓN MÚSICA ---
        semanticMap["uzbekistán"] = "Canción de Babasónicos, álbum Trinchera. Es un hit muy pedido."
        semanticMap["3 y 33"] = "Canción de Milo J. También conocida como 'tres treinta y tres'."
        semanticMap["platino y oro"] = "Canción de Trueno. Estilo trap/rap."
        semanticMap["trinchera"] = "Álbum de Babasónicos que incluye el tema Uzbekistán."
        semanticMap["milo j"] = "Artista argentino de música urbana."
        
        // --- SECCIÓN NAVEGACIÓN ---
        semanticMap["casa"] = "Destino frecuente del usuario (Hogar)."
        semanticMap["trabajo"] = "Destino frecuente del usuario (Oficina)."
        semanticMap["estación de servicio"] = "Búsqueda de combustible (YPF, Shell, Axion)."
        semanticMap["ypf"] = "Estación de servicio preferida por el rider."
    }

    /**
     * Recupera contexto relevante para un texto dado (Retrieval).
     * Misión #110: Proveer a Gemma de 'hechos' para evitar alucinaciones.
     * v1.2: Refactor STAFF para evitar falsos positivos masivos con palabras cortas.
     */
    fun retrieveContext(text: String): String {
        val lower = text.lowercase().trim()
        if (lower.length < 3) return "No hay contexto previo en memoria para esta orden."

        // Buscamos coincidencias de la memoria DENTRO del texto del usuario (Exact Word Match)
        val context = semanticMap.filter { entry ->
            val regex = Regex("\\b${Regex.escape(entry.key)}\\b", RegexOption.IGNORE_CASE)
            regex.containsMatchIn(lower)
        }.values.distinct().joinToString("\n")
        
        return if (context.isNotBlank()) {
            "CONTEXTO DE MEMORIA DEL USUARIO:\n$context"
        } else {
            "No hay contexto previo en memoria para esta orden."
        }
    }

    /**
     * v1.2: Permite que la IA aprenda nuevos hechos en tiempo real.
     */
    fun learn(key: String, value: String) {
        val lowerKey = key.lowercase()
        if (!semanticMap.containsKey(lowerKey)) {
            LogPoseLogger.i("VectorMemory", "🧠 Nuevo conocimiento adquirido: $key -> $value")
            semanticMap[lowerKey] = value
        }
    }
}
