package com.uriel.logpose.features.voice

import com.uriel.logpose.core.parser.PhoneticDictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import kotlin.system.measureNanoTime

class MusicVocabularyTest {

    companion object {
        private lateinit var testDictionary: PhoneticDictionary

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            val possiblePaths = listOf(
                "src/main/assets/logpose_glosario.json",
                "app/src/main/assets/logpose_glosario.json",
                "../app/src/main/assets/logpose_glosario.json"
            )
            var jsonContent: String? = null
            for (path in possiblePaths) {
                val f = File(path)
                if (f.exists()) {
                    jsonContent = f.readText()
                    break
                }
            }
            if (jsonContent == null) {
                jsonContent = """
                {
                  "musica": {
                    "artistas": ["duki", "ysy a", "bizarrap"],
                    "canciones": ["uzbekistan"],
                    "playlists": ["viaje"],
                    "correcciones_foneticas": {
                      "vx tu estan": "uzbekistan",
                      "vx tu x estan": "uzbekistan"
                    }
                  }
                }
                """.trimIndent()
            }
            testDictionary = PhoneticDictionary(jsonContent)
        }
    }

    @Before
    fun setUp() {
        MusicVocabulary.setDictionaryForTesting(testDictionary)
        MusicVocabulary.clearCache()
    }

    @Test
    fun testExactCorrectionVxTuEstan() {
        val result = MusicVocabulary.normalize("pone vx tu estan")
        assertEquals("Exact correction mismatch. Returned: '$result'", "poné uzbekistan", result)
    }

    @Test
    fun testLooseTokenCorrectionWithX() {
        val result = MusicVocabulary.normalize("pone vx tu x estan")
        assertEquals("Loose token correction mismatch. Returned: '$result'", "poné uzbekistan", result)
    }

    @Test
    fun testUnrelatedNavigationText() {
        val input = "llevame a rivadavia"
        val result = MusicVocabulary.normalize(input)
        assertEquals(input, result)
    }

    @Test
    fun testLongTextANRGuard() {
        val garbage = "numero ".repeat(60) + "uno"
        val elapsedMs = measureNanoTime {
            MusicVocabulary.normalize(garbage)
        } / 1_000_000.0
        assertTrue("normalize() tardó ${elapsedMs}ms en texto largo, riesgo de ANR", elapsedMs < 200.0)
    }

    @Test
    fun testWarmCacheLatencyUnder2ms() {
        val input = "pone vx tu x estan"
        MusicVocabulary.normalize(input) // primera pasada, llena la caché
        val elapsedMs = measureNanoTime {
            MusicVocabulary.normalize(input)
        } / 1_000_000.0
        assertTrue("normalize() en caliente tardó ${elapsedMs}ms, esperado <2ms", elapsedMs < 2.0)
    }

    @Test
    fun testEdgeCasePhoneticCascadeWithMultipleArtists() {
        val input = "pone duque y isi a con biza"
        val result = MusicVocabulary.normalize(input)
        assertEquals("Multi-artist cascade mismatch", "poné duki y ysy a con bizarrap", result)
    }
}
