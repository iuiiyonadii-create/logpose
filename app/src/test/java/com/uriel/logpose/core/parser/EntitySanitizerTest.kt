package com.uriel.logpose.core.parser

import org.junit.Test
import org.junit.Assert.*

class EntitySanitizerTest {

    @Test
    fun `duplicacion con conector se sanitiza correctamente`() {
        val result = EntitySanitizer.sanitize("se poné rockstar")
        assertEquals("rockstar", result)
    }

    @Test
    fun `duplicacion completa pone al inicio y al final`() {
        val result = EntitySanitizer.sanitize("pone duki pone")
        assertEquals("duki", result)
    }

    @Test
    fun `sin basura no modifica entidad valida`() {
        val result = EntitySanitizer.sanitize("bizarrap")
        assertEquals("bizarrap", result)
    }

    @Test
    fun `artista multi-palabra se preserva`() {
        val result = EntitySanitizer.sanitize("se pone ysy a")
        assertTrue(result.contains("ysy") || result.contains("ysy a"))
    }

    @Test
    fun `nunca deja el string vacio si habia tokens`() {
        val result = EntitySanitizer.sanitize("se de el")
        assertTrue(result.isNotBlank())
    }
}
