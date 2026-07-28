package com.uriel.logpose.thamis.entity

import com.uriel.logpose.thamis.intent.Intent

/**
 * Extrae entidades (parámetros) del texto del usuario con soporte para variantes lingüísticas.
 */
object EntityExtractor {

    private val patterns = mapOf(
        Intent.PLAY_MUSIC to listOf(
            "mandale (.*)", "pone un tema de (.*)", "poné un tema de (.*)", 
            "un tema de (.*)", "reproducir a (.*)", "reproducir (.*)", 
            "pone (.*)", "poné (.*)", "reproduci (.*)", "reproduccí (.*)", 
            "ponete (.*)", "escuchar (.*)", "musica (.*)", "música (.*)",
            "escuchar a (.*)", "mandale algo de (.*)", "mándale algo de (.*)"
        ),
        Intent.CALL_CONTACT to listOf(
            "llama a (.*)", "llamar a (.*)", "llamame a (.*)", "llamáme a (.*)",
            "comunicame con (.*)", "comunicáme con (.*)", "llama (.*)", "llamalo a (.*)", "llámalo a (.*)"
        ),
        Intent.NAVIGATE to listOf(
            "ir a (.*)", "llevame a (.*)", "lleváme a (.*)", "como llego a (.*)", 
            "cómo llego a (.*)", "navegar a (.*)", "encara para (.*)", "rumbear para (.*)",
            "anda a (.*)", "andá a (.*)", "rumbeá para (.*)"
        ),
        Intent.OPEN_APP to listOf("abrir (.*)", "abri (.*)", "abrí (.*)", "abre (.*)", "arranca (.*)", "arrancá (.*)"),
        Intent.SEND_MESSAGE to listOf(
            "mandale un whatsapp a (.*)", "mandale un mensaje a (.*)", "escribile a (.*)", 
            "mensaje a (.*)", "mandale a (.*)", "mándale a (.*)", "escribile un mensaje a (.*)"
        )
    )

    fun extract(intent: Intent, text: String): Map<String, String> {
        val entities = mutableMapOf<String, String>()
        val normalizedText = text.lowercase().trim()
        val intentPatterns = patterns[intent] ?: return emptyMap()

        for (pattern in intentPatterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(normalizedText)
            if (match != null && match.groupValues.size > 1) {
                val value = cleanValue(match.groupValues[1])
                if (value.isBlank()) continue
                
                val key = when(intent) {
                    Intent.PLAY_MUSIC -> "media"
                    Intent.CALL_CONTACT -> "contact"
                    Intent.NAVIGATE -> "destination"
                    Intent.OPEN_APP -> "app_name"
                    Intent.SEND_MESSAGE -> "contact"
                    else -> "parameter"
                }
                entities[key] = value
                
                // Si es un mensaje, intentamos extraer el cuerpo del mensaje también
                if (intent == Intent.SEND_MESSAGE && value.contains(" que diga ")) {
                    val parts = value.split(" que diga ")
                    entities["contact"] = parts[0].trim()
                    entities["message"] = parts[1].trim()
                }
                
                break // Encontramos el mejor match
            }
        }

        // Fallback si no hay match de regex pero hay una intención clara
        if (entities.isEmpty() && intent != Intent.UNKNOWN) {
            // Intentamos capturar la última parte del texto como el parámetro principal
            val words = normalizedText.split(" ")
            if (words.size > 1) {
                val lastPart = words.drop(1).joinToString(" ")
                val key = when(intent) {
                    Intent.PLAY_MUSIC -> "media"
                    Intent.CALL_CONTACT -> "contact"
                    Intent.NAVIGATE -> "destination"
                    Intent.OPEN_APP -> "app_name"
                    Intent.SEND_MESSAGE -> "contact"
                    else -> "parameter"
                }
                entities[key] = cleanValue(lastPart)
            }
        }

        return entities
    }

    private fun cleanValue(value: String): String {
        return value.replace(Regex("^(la|el|los|las|un|una|unos|unas|algo de|un tema de)\\s+"), "")
            .replace(Regex("\\?+$"), "")
            .replace(Regex("[,.;]$"), "")
            .trim()
    }
}
