package com.uriel.logpose.thamis.language.advanced

import java.util.Locale

/**
 * Spanish Metaphone Rioplatense v1.0.
 * Una adaptación del algoritmo de Alejandro Mosquera optimizada para el acento rioplatense.
 * Trata 'll' e 'y' como sibilantes (sh) para máxima compatibilidad con el habla argentina.
 */
object SpanishMetaphoneRioplatense {

    fun generate(text: String): String {
        var str = text.lowercase(Locale.getDefault()).trim()
        if (str.isEmpty()) return ""

        // 1. Normalización inicial
        str = str.replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
            .replace("ñ", "n")

        val result = StringBuilder()
        var i = 0
        val length = str.length

        while (i < length) {
            val current = str[i]
            val next = if (i + 1 < length) str[i + 1] else ' '
            val prev = if (i > 0) str[i - 1] else ' '

            when (current) {
                'a', 'e', 'i', 'o', 'u' -> {
                    // Vocales solo al principio
                    if (i == 0) result.append(current)
                }
                'b' -> {
                    // V y B suenan igual
                    result.append('b')
                }
                'v' -> {
                    result.append('b')
                }
                'c' -> {
                    if (next == 'h') {
                        // CH rioplatense -> SH
                        result.append('y')
                        i++
                    } else if (next == 'e' || next == 'i') {
                        result.append('s')
                    } else {
                        result.append('k')
                    }
                }
                'z' -> result.append('s')
                's' -> {
                    // Ignorar S final (Aspiración rioplatense)
                    if (i != length - 1) result.append('s')
                }
                'd' -> result.append('d')
                'f' -> result.append('f')
                'g' -> {
                    if (next == 'e' || next == 'i') {
                        result.append('j')
                    } else {
                        result.append('g')
                    }
                }
                'j' -> result.append('j')
                'h' -> {
                    // Silenciosa excepto en CH (manejado arriba)
                }
                'k' -> result.append('k')
                'l' -> {
                    if (next == 'l') {
                        // LL Rioplatense -> SH (representado como Y para el cerebro)
                        result.append('y')
                        i++
                    } else {
                        result.append('l')
                    }
                }
                'y' -> {
                    // Y Rioplatense -> SH
                    result.append('y')
                }
                'm' -> result.append('m')
                'n' -> result.append('n')
                'p' -> result.append('p')
                'q' -> result.append('k')
                'r' -> {
                    if (next == 'r') i++
                    result.append('r')
                }
                't' -> result.append('t')
                'x' -> {
                    result.append('k')
                    result.append('s')
                }
                'w' -> result.append('u')
            }
            i++
        }

        // Eliminar caracteres repetidos (ej: rr -> r, ya hecho arriba pero por seguridad)
        return result.toString().replace(Regex("([a-z])\\1+"), "$1")
    }
}
