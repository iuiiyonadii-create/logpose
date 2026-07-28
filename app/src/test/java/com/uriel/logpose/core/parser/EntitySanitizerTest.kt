package com.uriel.logpose.core.parser

import org.junit.Test
import org.junit.Assert.*

/**
 * Casos tomados directo del logcat real de LogPose (24/07) + variantes
 * esperables por el mismo patrón de duplicación de Vosk.
 */
class EntitySanitizerTest {

    // Simula MusicVocabulary.isKnown != null
    private val fakeVocabulary = setOf("rockstar", "duki", "spotify", "bizarrap", "ysy a")
    private val vocabularyCheck: (String) -> Boolean = { candidate ->
        fakeVocabulary.contains(candidate.trim().lowercase())
    }

    @Test
    fun `caso real del logcat - duplicacion con conector se`() {
        val result = EntitySanitizer.extractEntity("se poné rockstar", vocabularyCheck)
        assertEquals("rockstar", result.entity)
        assertEquals(1.0f, result.confidence)
    }

    @Test
    fun `duplicacion completa pone al inicio y al final`() {
        val result = EntitySanitizer.extractEntity("pone duki pone", vocabularyCheck)
        assertEquals("duki", result.entity)
    }

    @Test
    fun `alucinacion fonetica de abri no listada explicitamente`() {
        // "habria" ya está en ANCHOR_ROOTS, pero "avria" no -> debe caer por fuzzy match
        val result = EntitySanitizer.extractEntity("avria spotify", vocabularyCheck)
        assertEquals("spotify", result.entity)
    }

    @Test
    fun `sin basura no modifica nada`() {
        val result = EntitySanitizer.extractEntity("bizarrap", vocabularyCheck)
        assertEquals("bizarrap", result.entity)
        assertEquals("NO_CLEAN_NEEDED", result.method)
    }

    @Test
    fun `artista multi-palabra no se destruye por match parcial con vocabulario`() {
        val result = EntitySanitizer.extractEntity("se pone ysy a", vocabularyCheck)
        assertEquals("ysy a", result.entity)
    }

    @Test
    fun `nunca deja el string vacio aunque todo matchee basura`() {
        val result = EntitySanitizer.extractEntity("se de el", vocabularyCheck)
        // No hay vocabulario real acá: el freno de "size > 1" evita vaciar del todo
        assertTrue(result.entity.isNotBlank())
    }
}
