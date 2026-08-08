package com.uriel.logpose.core.engine

import android.util.Log
import com.thamis.lab.core.contracts.intent.Intent

data class DisambiguationResult(
    val intent: Intent,
    val matchedAnchor: AnchorToken,
    val confidenceScore: Float,
    val cleanedPayload: String
)

/**
 * ContextualIntentResolver v4.5: Motor de desempate léxico.
 * Aplica la Matriz de Priorización según el verbo disparador detectado.
 */
class ContextualIntentResolver(private val anchorRepository: AnchorRepository) {

    private val musicVerbs = setOf("pone", "poneme", "reproduce", "reproducir", "escuchar", "sonar", "mandale play", "pasame")
    private val commsVerbs = setOf("llama", "llamar", "manda", "mandale", "envia", "escribile", "wasap", "decile")
    private val appVerbs = setOf("abre", "abrir", "abri", "entra", "entrar")
    private val navigationVerbs = setOf("ir", "anda", "andá", "llevame", "lleváme", "navegar", "encara", "encará", "rumbear", "rumbeá", "ruta", "gps")

    fun resolve(tokens: List<String>): DisambiguationResult? {
        val detectedVerbs = tokens.map { it.lowercase() }
        val verbDomain = when {
            detectedVerbs.any { it in musicVerbs } -> AnchorDomain.MUSIC_TRACK
            detectedVerbs.any { it in commsVerbs } -> AnchorDomain.CONTACT
            detectedVerbs.any { it in appVerbs } -> AnchorDomain.APP_COMMAND
            detectedVerbs.any { it in navigationVerbs } -> AnchorDomain.NAVIGATION
            else -> null
        }

        val candidates = mutableListOf<Pair<AnchorToken, Float>>()

        for (token in tokens) {
            val anchor = anchorRepository.findAnchor(token) ?: continue
            var score = anchor.weight

            if (verbDomain != null) {
                // Matriz de Priorización Léxica (Misión #021.4)
                score *= if (isDomainMatching(anchor.domain, verbDomain)) 2.5f else 0.3f
            }
            candidates.add(Pair(anchor, score))
        }

        val bestMatch = candidates.maxByOrNull { it.second } ?: return null
        val winningAnchor = bestMatch.first
        val finalScore = bestMatch.second

        val intent = mapAnchorToIntent(winningAnchor.domain, verbDomain)
        val payload = cleanPayload(tokens, winningAnchor.rawText)

        Log.d("THAMIS_RESOLVER", "Winner: ${winningAnchor.rawText} (Domain: ${winningAnchor.domain}, Score: $finalScore)")

        return DisambiguationResult(
            intent = intent,
            matchedAnchor = winningAnchor,
            confidenceScore = finalScore,
            cleanedPayload = payload
        )
    }

    private fun isDomainMatching(anchorDomain: AnchorDomain, verbDomain: AnchorDomain): Boolean {
        return when (verbDomain) {
            AnchorDomain.MUSIC_TRACK -> anchorDomain == AnchorDomain.MUSIC_TRACK || anchorDomain == AnchorDomain.MUSIC_ARTIST
            else -> anchorDomain == verbDomain
        }
    }

    private fun mapAnchorToIntent(anchorDomain: AnchorDomain, verbDomain: AnchorDomain?): Intent {
        return when (verbDomain ?: anchorDomain) {
            AnchorDomain.MUSIC_TRACK, AnchorDomain.MUSIC_ARTIST -> Intent.PLAY_MUSIC
            AnchorDomain.CONTACT -> Intent.CALL_CONTACT // El dispatcher decidirá si Call o SendMessage según el texto
            AnchorDomain.APP_COMMAND -> Intent.OPEN_APP
            AnchorDomain.NAVIGATION -> Intent.NAVIGATE
            else -> Intent.UNKNOWN
        }
    }

    private fun cleanPayload(tokens: List<String>, anchorText: String): String {
        val fullPhrase = tokens.joinToString(" ")
        // Sanitización Quirúrgica Pre-API
        return fullPhrase
            .replace("(?i)^(pone|poneme|reproduce|reproducir|llama|llamar|manda|mandale|abre|abrir|abri|escribile|decile|ir a|llevame a|navegar a|anda a|andá a|encara para|ruta a|gps a)\\s+".toRegex(), "")
            .replace("(?i)\\s+(de|con|en|por|a)$".toRegex(), "")
            .trim()
    }
}
