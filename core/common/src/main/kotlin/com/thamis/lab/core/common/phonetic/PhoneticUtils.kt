package com.thamis.lab.core.common.phonetic

/**
 * PhoneticUtils: Single source of truth for phonetic normalization across LogPose.
 * Unified Rioplatense Morphological Collapse (Kernel v60.0).
 */
public object PhoneticUtils {

    private val S_END_REGEX = Regex("s\\b")
    private val S_BEFORE_CONS_REGEX = Regex("s(?=[bcdfghjklmnpqrstvwxyz])")
    private val CLEAN_DNA_REGEX = Regex("[^a-z0-9]")
    private val REPEATING_CHARS_REGEX = Regex("([a-z])\\1+")
    private val VOWELS_COLLAPSE_REGEX = Regex("[aeo]")

    /**
     * Core normalization for production and simulation.
     * @param extremeNoise If true, applies asonante vowel collapse for high-noise scenarios.
     */
    public fun normalizeToDNA(text: String, extremeNoise: Boolean = false): String {
        // Misión #061: Blindaje de palabra clave LOG (Sea Ley)
        if (text.trim().equals("LOG", ignoreCase = true)) return "log"

        var key = text.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("ü", "u").replace("ñ", "n")
            // 1. Yeísmo Rioplatense: y/ll -> j (Staff v60.0 Alignment)
            .replace("ll", "j").replace("y", "j").replace("sh", "j")
            // 2. Aspiración de S: Ignorar S final o antes de consonante
            .replace(S_END_REGEX, "")
            .replace(S_BEFORE_CONS_REGEX, "")
            // 3. Colapso de Oclusivas (v->b, z/c->s)
            .replace("v", "b").replace("z", "s").replace("c", "s")

        if (extremeNoise) {
            // Colapso de rimas asonantes v4.0 (Staff Simulation)
            key = key.replace(VOWELS_COLLAPSE_REGEX, "v")
        }

        // 4. Limpieza final alfanumérica y colapso de repetidas
        return key.replace(CLEAN_DNA_REGEX, "")
            .replace(REPEATING_CHARS_REGEX, "$1")
    }
}
