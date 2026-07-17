package com.uriel.logpose.core.parser.normalization

object CommandAliasRepository {

    private val aliases = mapOf(

        // Escuchar
        "escucha" to "escuchar",
        "escuchar" to "escuchar",
        "escuchame" to "escuchar",
        "oir" to "escuchar",
        "oye" to "escuchar",

        // Detener
        "detene" to "detener",
        "detener" to "detener",
        "para" to "detener",
        "parar" to "detener",
        "stop" to "detener",

        // Llamar
        "llama" to "llamar",
        "llamar" to "llamar",
        "llamame" to "llamar",

        // Navegar
        "anda" to "ir",
        "andar" to "ir",
        "ir" to "ir",
        "ve" to "ir",
        "vamos" to "ir",
        "dirigite" to "ir",

        // Música
        "musica" to "musica",
        "spotify" to "spotify",

        // Abrir
        "abre" to "abrir",
        "abrime" to "abrir",
        "abrir" to "abrir"
    )

    fun resolve(text: String): String {

        return text
            .split(" ")
            .joinToString(" ") { word ->
                aliases[word] ?: word
            }
    }
}