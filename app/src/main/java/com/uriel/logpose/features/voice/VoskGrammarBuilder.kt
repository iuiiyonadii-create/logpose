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
        val phrases = mutableListOf<String>()

        // --- ESTRATEGIA HÍBRIDA v4.6: GRAMÁTICA DE CENTINELA ---
        // 1. Palabras de Activación
        phrases.addAll(dictionary.listaDe("fonetica.wake_words").map { normalizeForVosk(it) })

        // 2. Verbos de Disparo (Causa de Handover)
        val triggerCategories = listOf("abrir", "reproducir", "enviar_mensaje", "navegar", "sistema")
        for (cat in triggerCategories) {
            phrases.addAll(dictionary.listaDe("verbos.$cat").map { normalizeForVosk(it) })
        }
        
        // 3. Muletillas y Fillers para mantener el contexto
        phrases.addAll(dictionary.listaDe("muletillas_a_ignorar").map { normalizeForVosk(it) })
        phrases.addAll(listOf("de", "con", "a", "por", "el", "la").map { normalizeForVosk(it) })

        phrases.add("[unk]")
        val result = JSONArray(phrases.distinct()).toString()
        com.uriel.logpose.core.compat.core.LogPoseLogger.d("VoskGrammar: Centinela v4.6 cargado con ${phrases.distinct().size} anclas.")
        return result
    }

    fun buildFullGrammar(): String {
        val phrases = mutableListOf<String>()

        // 1. Base Gramática (Wake words, Verbos, Muletillas)
        phrases.addAll(dictionary.listaDe("fonetica.wake_words").map { normalizeForVosk(it) })
        val triggerCategories = listOf("abrir", "reproducir", "enviar_mensaje", "navegar", "sistema")
        for (cat in triggerCategories) {
            phrases.addAll(dictionary.listaDe("verbos.$cat").map { normalizeForVosk(it) })
        }
        phrases.addAll(dictionary.listaDe("muletillas_a_ignorar").map { normalizeForVosk(it) })
        phrases.addAll(listOf("de", "con", "a", "por", "el", "la").map { normalizeForVosk(it) })

        // 2. ENTIDADES MUSICALES (Misión #027)
        // Inyectamos todo el catálogo para que Vosk tenga "fidelidad acústica"
        val musicEntities = (MusicVocabulary.getAllArtists() + MusicVocabulary.getAllSongs() + MusicVocabulary.getAllPlaylists())
        phrases.addAll(musicEntities.flatMap { it.split(" ") }.map { normalizeForVosk(it) })

        // 3. CONTACTOS (Agenda del usuario)
        phrases.addAll(ContactResolver.getAllNames().flatMap { it.split(" ") }.map { normalizeForVosk(it) })

        phrases.add("[unk]")
        val result = JSONArray(phrases.distinct()).toString()
        com.uriel.logpose.core.compat.core.LogPoseLogger.i("VoskGrammar: Full v4.7 cargado con ${phrases.distinct().size} anclas.")
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
            
        // --- MAPEO DE TRADUCCIÓN ACÚSTICA (Misión #021.1 / #029) ---
        // Mapeamos términos regionales a palabras "estándar" que Vosk SI conoce.
        return when (lower) {
            "uzbekistan" -> "un tan"
            "kuelgue" -> "colgué"
            "wasap", "wasapp", "guasap", "wasa", "guasa" -> "whatsapp"
            "insta", "instagran", "ig" -> "instagram"
            "spoti", "spoty", "espotifai" -> "spotify"
            "duki" -> "duque"
            "wos" -> "voz"
            "ysy", "ysy a" -> "y si"
            "bizarrap", "biza" -> "pisa"
            "temon", "temazo" -> "tema"
            "morfar", "morfi" -> "comer"
            "cana", "ratis", "cobani", "yuta" -> "policia"
            "bondi", "colectivo" -> "autobus"
            "laburo", "chamba" -> "trabajo"
            "jermu" -> "mujer"
            "nafta" -> "gasolina"
            "service" -> "servicio"
            "atende", "atendeme" -> "atiende"
            "rechaza", "rechazalo" -> "rechaza"
            "llama", "llamalo", "llamala" -> "llama"
            "desperta", "despertate" -> "despierta"
            "abrime", "abrite" -> "abrir"
            "poneme", "ponete" -> "pone"
            "mandale", "mandala" -> "manda"
            "reproduci", "reproducime" -> "reproduce"
            "tirame", "tirale" -> "tira"
            "avisale", "decile" -> "dile"
            "contale", "contestale" -> "contesta"
            "leeme" -> "lee"
            "para", "pará", "detene", "detené" -> "para"
            "escuchame" -> "escucha"
            "buscame" -> "busca"
            "llevame" -> "lleva"
            "guiame" -> "guia"
            "anda", "andá" -> "ve"
            "luego" -> "luego"
            "despues", "después" -> "después"
            else -> lower
        }
    }
}
