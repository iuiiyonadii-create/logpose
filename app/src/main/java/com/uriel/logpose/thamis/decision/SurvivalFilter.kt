package com.uriel.logpose.thamis.decision

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.FastParser
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.features.voice.MusicVocabulary
import com.uriel.logpose.features.voice.VoskVoiceEngine

enum class Criticality { LOW, HIGH }

sealed class FilterResult {
    data class Confirmed(val command: Command) : FilterResult()
    object TooUncertain : FilterResult()
    object MissingObject : FilterResult()
    object NoIntent : FilterResult()
}

/**
 * SurvivalFilter (Muro de Verbos): Protege al sistema contra falsos positivos 
 * disparados por el ruido del viento o interferencias en el casco.
 */
object SurvivalFilter {
    private const val BASE_THRESHOLD = 0.82f
    private const val MAX_THRESHOLD = 0.95f

    fun evaluate(result: VoskVoiceEngine.RecognizedCommand): FilterResult {
        // 1. Parseo de intención rápida antes del umbral
        val parseResult = FastParser.parse(result.text)
        if (parseResult !is ParseResult.Success) {
            return FilterResult.NoIntent
        }
        val command = parseResult.command

        // 2. Calculamos el umbral dinámico basado en el ruido ambiente actual
        val noiseLevel = VoskVoiceEngine.getAmbientNoiseLevel()
        var currentThreshold = BASE_THRESHOLD + (MAX_THRESHOLD - BASE_THRESHOLD) * noiseLevel

        // SINCRO CLAUDE: Override Semántico (Punto 4 del Análisis)
        // Si el objeto matchea el vocabulario, le damos prioridad absoluta
        val semanticBoost = if (command is Command.PlayMusic && MusicVocabulary.isKnown(command.query)) {
            LogPoseLogger.i("SurvivalFilter: Match semántico detectado (${command.query}). Prioridad ALTA.")
            0.40f // Reducimos el umbral necesario a la mitad
        } else if (isMultimediaCommand(command)) {
            0.15f // Reducción estándar para música
        } else 0f

        if (result.confidence < (currentThreshold - semanticBoost).coerceAtLeast(0.40f)) {
            LogPoseLogger.w("SurvivalFilter: Dudoso (${result.confidence} < ${currentThreshold - semanticBoost}) -> '${result.text}'")
            return FilterResult.TooUncertain
        }

        // SINCRO CLAUDE: Validación de Objeto contra Diccionario
        if (command is Command.PlayMusic) {
            val query = command.query.trim()
            if (query.isNotEmpty()) {
                // SINCRO CLAUDE: Si el comando es genérico ("poné música"), lo dejamos pasar.
                val isGeneric = query == "música" || query == "musica" || query == "algo"
                
                if (isGeneric || MusicVocabulary.isKnown(query)) {
                    LogPoseLogger.i("SurvivalFilter: Match semántico detectado ($query). Confirmado.")
                    return FilterResult.Confirmed(command)
                } else {
                    // Solo rechazamos si la confianza es MUY baja y no lo conocemos
                    if (result.confidence < 0.60f) {
                        LogPoseLogger.w("SurvivalFilter: Alucinación probable -> '$query'. Rechazando.")
                        return FilterResult.TooUncertain
                    }
                }
            }
        }

        val criticality = getCriticality(command)

        // 3. Evaluación de criticidad
        return when (criticality) {
            Criticality.HIGH -> evaluateHighCriticality(command, result)
            Criticality.LOW -> FilterResult.Confirmed(command)
        }
    }

    private fun evaluateHighCriticality(command: Command, result: VoskVoiceEngine.RecognizedCommand): FilterResult {
        // Para acciones críticas, el umbral es el dinámico + un pequeño margen de seguridad
        val noiseLevel = VoskVoiceEngine.getAmbientNoiseLevel()
        val criticalThreshold = (BASE_THRESHOLD + (MAX_THRESHOLD - BASE_THRESHOLD) * noiseLevel) + 0.05f
        val finalThreshold = criticalThreshold.coerceAtMost(0.98f)

        if (result.confidence < finalThreshold) {
            return FilterResult.TooUncertain
        }

        // Requisito de Token Dual para acciones sensibles
        return when (command) {
            is Command.OpenApp -> {
                // "Abrir" solo no vale, tiene que tener el nombre de la app
                if (command.appName.length < 3) FilterResult.MissingObject else FilterResult.Confirmed(command)
            }
            is Command.CloseApp -> {
                if (command.appName.length < 3) FilterResult.MissingObject else FilterResult.Confirmed(command)
            }
            is Command.Call -> {
                // Exigimos frase reforzada para llamadas para evitar disparos accidentales
                val text = result.text.lowercase()
                val hasVerb = text.contains("llama") || text.contains("llamá")
                if (!hasVerb || command.contact.isBlank()) FilterResult.MissingObject else FilterResult.Confirmed(command)
            }
            is Command.SendMessage -> {
                if (command.contact.isBlank()) FilterResult.MissingObject else FilterResult.Confirmed(command)
            }
            is Command.EndTrip -> {
                // Finalizar viaje requiere mucha certeza
                if (result.confidence < 0.96f) FilterResult.TooUncertain else FilterResult.Confirmed(command)
            }
            else -> FilterResult.Confirmed(command)
        }
    }

    private fun getCriticality(command: Command): Criticality {
        return when (command) {
            is Command.Call, is Command.OpenApp, is Command.CloseApp, 
            is Command.SendMessage, is Command.EndTrip,
            is Command.GetWeather, is Command.ReadNotifications -> Criticality.HIGH
            else -> Criticality.LOW
        }
    }

    private fun isMultimediaCommand(command: Command): Boolean {
        return command is Command.PlayMusic || 
               command is Command.PauseMusic || 
               command is Command.NextTrack || 
               command is Command.PreviousTrack || 
               command is Command.VolumeUp || 
               command is Command.VolumeDown || 
               command is Command.VolumeAbsolute
    }
}
