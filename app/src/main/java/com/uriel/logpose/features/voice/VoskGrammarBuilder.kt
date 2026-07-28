package com.uriel.logpose.features.voice

import org.json.JSONArray

/**
 * VoskGrammarBuilder V5: Soporte para verbos + números.
 */
object VoskGrammarBuilder {

    private val NUMBER_WORDS = listOf(
        "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez",
        "once", "doce", "trece", "catorce", "quince", "dieciseis", "diecisiete", "dieciocho", "diecinueve", "veinte"
    )

    fun buildMinimalGrammar(): String {
        val phrases = mutableListOf<String>()

        // 1. Palabra de Activación (Separada para compatibilidad de diccionario)
        phrases.addAll(listOf("log", "pose", "log pose", "lock", "lon", "los"))

        // 2. Verbos Ancla (SINCRO Thamis 1.5)
        phrases.addAll(listOf(
            "abrí", "abri", "abre", "poné", "pone", "pon", "reproducir", "reproduce",
            "mensaje", "mandale", "mándale", "mandá", "manda", "envía", "enviá", "escribile",
            "ir", "anda", "andá", "leer", "leé", "ver", "cambiar", "ponele", "mostrame", "mostráme",
            "cancelar", "cancelá", "terminar", "terminá", "detener", "parar", "cortar", "sacá", "sacar", "dejá", "dejar",
            "encara", "encará", "rumbear", "rumbeá", "como vamos", "cómo vamos", "siguiente", "anterior", "pausa"
        ))

        // 2. Destinos y Slots Comunes
        phrases.addAll(listOf("casa", "trabajo", "laburo", "rancho", "choza", "mcdonalds", "burguer", "estación"))

        // 3. Slots Numéricos
        for (word in NUMBER_WORDS) {
            phrases.add("opción $word")
            phrases.add("seleccionar $word")
        }

        // 3. Artistas y Playlists (SINCRO Thamis: Solo Alias seguros)
        val safeNames = MusicVocabulary.getGrammarPhases()
        for (name in safeNames) {
            phrases.add("pone $name")
            phrases.add("reproducir $name")
            phrases.add("pon $name")
            phrases.add(name)
        }

        // 4. Comandos de Sistema
        phrases.addAll(listOf(
            "finalizar viaje", "donde estoy", "estado de bateria", "mandar mensaje whatsapp", "abrir mapas"
        ))
        phrases.add("[unk]")
        return JSONArray(phrases.distinct()).toString()
    }

    fun buildFullGrammar(): String {
        val phrases = mutableListOf<String>()
        val arr = JSONArray(buildMinimalGrammar())
        for (i in 0 until arr.length()) {
            phrases.add(arr.getString(i))
        }

        return JSONArray(phrases.distinct()).toString()
    }
}
