package com.uriel.logpose.thamis.entity

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent

/**
 * Extrae entidades (parámetros) del texto del usuario con soporte para variantes lingüísticas.
 */
object EntityExtractor {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    private val patterns = mapOf(
        Intent.PLAY_MUSIC to listOf(
            "mandale (.*)", "pone un tema de (.*)", "poné un tema de (.*)", 
            "un tema de (.*)", "reproducir a (.*)", "reproducir (.*)", 
            "pone (.*)", "poné (.*)", "reproduci (.*)", "reproduccí (.*)", 
            "ponete (.*)", "escuchar (.*)", "musica (.*)", "música (.*)",
            "escuchar a (.*)", "mandale algo de (.*)", "mándale algo de (.*)",
            "ponete un temon de (.*)", "ponete un temazo de (.*)", "pasame algo de (.*)",
            "poné algo de (.*)", "pone algo de (.*)", "ponete un (.*)", "reproducime (.*)",
            "pone (.*) de (.*)", "poné (.*) de (.*)"
        ),
        Intent.CALL_CONTACT to listOf(
            "llama a (.*)", "llamar a (.*)", "llamame a (.*)", "llamáme a (.*)",
            "comunicame con (.*)", "comunicáme con (.*)", "llama (.*)", "llamalo a (.*)", "llámalo a (.*)",
            "llamala a (.*)", "llámala a (.*)", "llamale a (.*)", "llámale a (.*)"
        ),
        Intent.NAVIGATE to listOf(
            "ir a (.*)", "llevame a (.*)", "lleváme a (.*)", "como llego a (.*)", 
            "cómo llego a (.*)", "navegar a (.*)", "encara para (.*)", "rumbear para (.*)",
            "anda a (.*)", "andá a (.*)", "rumbeá para (.*)", "llevame al (.*)", "llevame a la (.*)"
        ),
        Intent.OPEN_APP to listOf(
            "abrir (.*)", "abri (.*)", "abrí (.*)", "abre (.*)", "arranca (.*)", "arrancá (.*)",
            "largame (.*)", "tirame (.*)", "poneme (.*)", "entrar a (.*)", "metete en (.*)",
            "abrite (.*)", "abríte (.*)"
        ),
        Intent.SEND_MESSAGE to listOf(
            "mandale un whatsapp a (.*)", "mandale un mensaje a (.*)", "escribile a (.*)", 
            "mensaje a (.*)", "mandale a (.*)", "mándale a (.*)", "escribile un mensaje a (.*)",
            "mandale un wasap a (.*)", "mandale un guasap a (.*)", "mandale un wasa a (.*)",
            "manda mensaje a (.*)", "mandá mensaje a (.*)", "enviale mensaje a (.*)",
            "decile a (.*) que (.*)", "decile a (.*) (.*)"
        ),
        Intent.REPLY_MESSAGE to listOf(
            "respondé (.*)", "responde (.*)", "contestale (.*)", "decile que (.*)", 
            "escribile que (.*)", "ponele que (.*)", "mandale que (.*)"
        ),
        Intent.SOCIAL_SEARCH to listOf(
            "buscá en instagram a (.*)", "busca en instagram a (.*)", 
            "quien es (.*)", "perfil de (.*)", "mira el perfil de (.*)"
        ),
        Intent.SOCIAL_CAMERA to listOf("camara de instagram", "sacá una foto con instagram", "foto instagram"),
        
        Intent.SAFETY_ALERT to listOf(
            "ojo (.*)", "guarda (.*)", "cuidado (.*)", "atenti (.*)", 
            "hay (.*)", "viste (.*)"
        ),
        Intent.TRAFFIC_STATUS to listOf(
            "quilombo en (.*)", "embotellamiento en (.*)", "como viene (.*)", 
            "estado de (.*)", "trafico en (.*)", "tráfico en (.*)"
        ),
        Intent.RESTAURANT_SEARCH to listOf(
            "donde hay (.*)", "donde comer (.*)", "algun (.*)", "tengo (.*)", "quiero (.*)"
        ),
        Intent.TRANSPORT_INFO to listOf(
            "como viene el (.*)", "a que hora pasa el (.*)", "donde esta el (.*)", "el (.*)"
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
                val rawValue = match.groupValues[1]
                val value = cleanValue(rawValue)
                if (value.isBlank()) continue
                
                val key = when(intent) {
                    Intent.PLAY_MUSIC -> "media"
                    Intent.CALL_CONTACT -> "contact"
                    Intent.NAVIGATE -> "destination"
                    Intent.OPEN_APP -> "app_name"
                    Intent.SEND_MESSAGE -> "contact"
                    Intent.SOCIAL_SEARCH -> "parameter"
                    else -> "parameter"
                }

                // Resolución Staff: Resolver apodos de contactos o destinos comunes
                val resolvedValue = when(key) {
                    "contact" -> dictionary.mapaDe("contactos.apodos")[value.lowercase()] ?: value
                    "destination" -> {
                        val modismos = dictionary.mapaDe("modismos")
                        modismos[value.lowercase()] ?: value
                    }
                    "app_name" -> dictionary.resolverApp(value) ?: value
                    else -> value
                }

                entities[key] = resolvedValue
                
                // Si es un mensaje, intentamos extraer el cuerpo del mensaje también
                if (intent == Intent.SEND_MESSAGE && value.contains(" que diga ")) {
                    val parts = value.split(" que diga ")
                    val contactPart = parts[0].trim()
                    entities["contact"] = dictionary.mapaDe("contactos.apodos")[contactPart.lowercase()] ?: contactPart
                    entities["message"] = parts[1].trim()
                }

                // Si es música con 'de', intentamos separar track y artista
                if (intent == Intent.PLAY_MUSIC && value.contains(" de ")) {
                    val parts = value.split(" de ")
                    entities["track"] = parts[0].trim()
                    entities["artist"] = parts[1].trim()
                    entities["media"] = value // Mantenemos el full query por compatibilidad
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
                    Intent.SOCIAL_SEARCH -> "parameter"
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
