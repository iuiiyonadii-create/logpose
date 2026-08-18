package com.uriel.logpose.features.voice

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.thamis.communication.resolver.ContactResolver
import org.json.JSONArray

/**
 * VoskGrammarBuilder V7: Gramática dinámica (ULC + Social Knowledge).
 * Mejorado (Misión #013): Inyección automática de la agenda del usuario.
 */
object VoskGrammarBuilder {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    fun buildMinimalGrammar(): String {
        val phrases = linkedSetOf<String>()

        // --- ESTRATEGIA STAFF v5.0: SÓLO ANCLAS DE ACTIVACIÓN ---
        // 1. Palabras de Activación (Wake words)
        phrases.addAll(dictionary.listaDe("fonetica.wake_words").map { normalizeForVosk(it) })

        // 2. Verbos Críticos (Short-Circuit)
        val essentialVerbs = listOf("pone", "llama", "abri", "ir", "cancela", "viaje", "musica")
        phrases.addAll(essentialVerbs)
        
        // 3. Muletillas mínimas para no perder el ritmo
        phrases.addAll(listOf("de", "el", "la", "a").map { normalizeForVosk(it) })

        phrases.add("[unk]")
        val result = JSONArray(phrases.toList()).toString()
        com.uriel.logpose.core.compat.core.LogPoseLogger.d("VoskGrammar: Sentinela v5.0 cargado (Modo Protegido).")
        return result
    }

    fun buildFullGrammar(): String {
        val phrases = linkedSetOf<String>()

        // 1. Base Gramática (Wake words, Verbos, Muletillas)
        phrases.addAll(dictionary.listaDe("fonetica.wake_words").map { normalizeForVosk(it) })
        val triggerCategories = listOf("abrir", "reproducir", "enviar_mensaje", "navegar", "sistema")
        for (cat in triggerCategories) {
            phrases.addAll(dictionary.listaDe("verbos.$cat").map { normalizeForVosk(it) })
        }
        phrases.addAll(dictionary.listaDe("muletillas_a_ignorar").map { normalizeForVosk(it) })
        phrases.addAll(listOf("de", "con", "a", "por", "el", "la").map { normalizeForVosk(it) })

        // 2. ENTIDADES MUSICALES (Misión #027 / Sherlock v5.0 Fix)
        // Inyectamos tanto frases completas como tokens individuales para mantener contexto n-gram y fidelidad
        val musicEntities = (MusicVocabulary.getAllArtists() + MusicVocabulary.getAllSongs() + MusicVocabulary.getAllPlaylists())
        musicEntities.forEach { entity ->
            phrases.add(normalizeForVosk(entity))
            entity.split(" ").forEach { token -> phrases.add(normalizeForVosk(token)) }
        }

        // 3. CONTACTOS (Agenda del usuario)
        val contactNames = ContactResolver.getAllNames()
        contactNames.forEach { name ->
            phrases.add(normalizeForVosk(name))
            name.split(" ").forEach { token -> phrases.add(normalizeForVosk(token)) }
        }

        phrases.add("[unk]")
        val result = JSONArray(phrases.toList()).toString()
        com.uriel.logpose.core.compat.core.LogPoseLogger.i("VoskGrammar: Full v4.7 cargado con ${phrases.size} anclas.")
        return result
    }

    /**
     * Normaliza un token para que sea compatible con el vocabulario base del modelo Vosk.
     * Mapea términos regionales a palabras estándar que el modelo SI tiene en su diccionario.
     */
    private fun normalizeForVosk(text: String): String {
        val lower = text.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")
            .replace("ñ", "n")
            .replace(Regex("[^a-z0-9 ]"), "")
            .trim()
            
        // v5.1: Mapeo estricto solo a palabras que el modelo SI tiene en su words.txt
        return when (lower) {
            "largame", "lárgame", "poneme", "ponete", "ponele", "poned", "pon", "ponéme" -> "pone"
            "mandale", "mándale", "mandala", "manda" -> "manda"
            "tirame", "tiráme", "tirale", "tira", "tirá" -> "pone"
            "kuelgue" -> "colgue"
            "wasap", "wasapp", "guasap", "wasa", "guasa" -> "whatsapp"
            "insta", "instagran", "ig" -> "instagram"
            "spoti", "spoty", "espotifai" -> "spotify"
            "atende", "atendeme" -> "atiende"
            "abrime", "abrite" -> "abrir"
            "reproduci", "reproducime" -> "reproduce"
            "para", "pará", "detene", "detené" -> "para"
            "llevame" -> "lleva"
            "anda", "andá" -> "ve"
            else -> lower
        }
    }
}
