package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.features.voice.MusicVocabulary

/**
 * FastParser V5: Prioridad de Aplicaciones y Descubrimiento Dinámico.
 */
object FastParser {

    private val MUSIC_ANCHORS = listOf(
        "ponete algo de", "ponele algo de", "poneme algo de", "tira algo de", "tirá algo de",
        "reproducí algo de", "reproduci algo de", "buscame algo de", "buscáme algo de",
        "buscá algo de", "busca algo de", "hace sonar algo de", "hacé sonar algo de",
        "poner", "poné", "pone", "pony", "reproducir", "reproducí", "reproducime", "reproducíme",
        "poneme", "ponéle", "ponele", "tira", "tirá", "play", "plei", "musica", "música",
        "buscame", "buscáme", "buscá", "busca", "sonar", "hace sonar", "hacé sonar"
    ).sortedByDescending { it.length }

    private val APP_ANCHORS = listOf("abrir", "abrí", "abri", "habria", "habría", "lanzar", "lanzá").sortedByDescending { it.length }

    private val TRANSPORT_COMMANDS = mapOf(
        listOf("siguiente", "pasala", "pasa", "cambiala") to TransportAction.NEXT,
        listOf("atras", "anterior", "atrás", "repetir", "repetí") to TransportAction.PREVIOUS,
        listOf("pausa", "parar", "detener", "detené", "shh") to TransportAction.PAUSE,
        listOf("reanudar", "seguir", "continuar", "dale", "play") to TransportAction.PLAY,
        listOf("subir volumen", "sube", "subi", "más fuerte") to TransportAction.VOLUME_UP,
        listOf("bajar volumen", "baja", "baji", "más bajo") to TransportAction.VOLUME_DOWN
    )

    enum class TransportAction { NEXT, PREVIOUS, PAUSE, PLAY, REPEAT, VOLUME_UP, VOLUME_DOWN }

    fun parse(phoneticClean: String): ParseResult {
        if (phoneticClean.isBlank()) return ParseResult.Unknown

        val text = MusicVocabulary.normalize(phoneticClean)

        // 1. SINCRO CLAUDE: Prioridad de Aplicaciones (WhatsApp, Mapas)
        // Esto evita que 'pone whatsapp' se considere música por culpa del 'pone'.
        if (text.contains("whatsapp")) return ParseResult.Success(Command.OpenApp("whatsapp"))
        if (text.contains("mapas") || text.contains("google maps")) return ParseResult.Success(Command.OpenApp("mapas"))
        if (text.contains("bateria") || text.contains("batería")) return ParseResult.Success(Command.GetStatus)
        if (text.contains("viaje") && (text.contains("fin") || text.contains("finalizar"))) return ParseResult.Success(Command.EndTrip)

        // 2. SINCRO CLAUDE: Guardián de cordura para evitar ANRs por basura repetitiva
        when (val verdict = TranscriptSanityGuard.evaluate(phoneticClean)) {
            is TranscriptSanityGuard.Verdict.Reject -> {
                com.uriel.logpose.core.compat.core.LogPoseLogger.d("FastParser: Descartado por falta de cordura -> ${verdict.reason}")
                return ParseResult.Unknown
            }
            is TranscriptSanityGuard.Verdict.Accept -> { /* Continuar */ }
        }

        // 3. Comandos de transporte
        for ((keywords, action) in TRANSPORT_COMMANDS) {
            if (keywords.any { text.contains(it) }) {
                return ParseResult.Success(when(action) {
                    TransportAction.NEXT -> Command.NextTrack
                    TransportAction.PREVIOUS -> Command.PreviousTrack
                    TransportAction.PAUSE -> Command.PauseMusic
                    TransportAction.PLAY -> Command.PlayMusic("")
                    TransportAction.VOLUME_UP -> Command.VolumeUp
                    TransportAction.VOLUME_DOWN -> Command.VolumeDown
                    else -> Command.Unknown
                })
            }
        }

        // 4. Música
        for (anchor in MUSIC_ANCHORS) {
            if (text.startsWith(anchor)) {
                val remainder = text.removePrefix(anchor).trim()
                val sanitized = EntitySanitizer.sanitize(remainder)
                
                if (sanitized.isEmpty() || sanitized.length < 3) {
                    return ParseResult.Success(Command.PlayMusic(""))
                }

                return ParseResult.Success(Command.PlayMusic(sanitized))
            }
        }

        // 5. Apps (Soporte para 'Abrir X')
        for (anchor in APP_ANCHORS) {
            if (text.startsWith(anchor)) {
                val remainder = text.removePrefix(anchor).trim()
                val sanitized = EntitySanitizer.sanitize(remainder)
                return ParseResult.Success(Command.OpenApp(sanitized))
            }
        }

        // Fallback: Match directo de vocabulario
        MusicVocabulary.findBestMatch(text, threshold = 0.80)?.let { match ->
            return ParseResult.Success(Command.PlayMusic(match.first))
        }

        return ParseResult.Unknown
    }
}
