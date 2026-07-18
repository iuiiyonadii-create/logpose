package com.uriel.logpose.core.parser.normalization

object CommandAliasRepository {

    private val aliases = mapOf(

        // Escuchar
        "escucha" to "escuchar",
        "escuchar" to "escuchar",
        "escuchame" to "escuchar",
        "escúchame" to "escuchar",
        "oir" to "escuchar",
        "oye" to "escuchar",

        // Detener
        "detene" to "detener",
        "detené" to "detener",
        "detener" to "detener",
        "para" to "detener",
        "parar" to "detener",
        "stop" to "detener",

        // Llamar
        "llama" to "llamar",
        "llamar" to "llamar",
        "llamame" to "llamar",
        "llamamele" to "llamar",
        "llamalo" to "llamar",

        // Navegar
        "anda" to "ir",
        "andar" to "ir",
        "ve" to "ir",
        "vamos" to "ir",
        "dirigite" to "ir",
        "dirigitea" to "ir",
        "dirigirse" to "ir",
        "ir" to "ir",

        // Abrir
        "abre" to "abrir",
        "abrime" to "abrir",
        "abrir" to "abrir",

        // Música
        "musica" to "musica",
        "música" to "musica",
        "spotify" to "spotify",

        // Conectores frecuentes
        "al" to "a",
        "del" to "de"
    )

    fun resolve(text: String): String {

        return text
            .split(Regex("\\s+"))
            .filter(String::isNotBlank)
            .joinToString(" ") { word ->
                aliases[word] ?: word
            }
    }
}