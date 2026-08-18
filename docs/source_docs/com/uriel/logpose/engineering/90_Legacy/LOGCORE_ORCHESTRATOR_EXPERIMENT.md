# LEGACY: LogCore Orchestrator Experiment

Archived from: app/src/androidTest/logcore/
Archived on: architectural cleanup preceding LogCore Providers implementation.

This code never compiled (wrong source directory) and is preserved
verbatim, for historical reference only. Do not resurrect as-is.

------------------------------------------------------------------------------
## LogCore.kt
```kotlin
package com.uriel.logpose.logcore

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
import com.uriel.logpose.logcore.core.intent.VoiceRequest
import com.uriel.logpose.logcore.orchestrator.ActionEngine
import com.uriel.logpose.logcore.pipeline.VoicePipeline

class LogCore(

    private val actionEngine: ActionEngine

) {

    fun execute(
        action: ActionRequest
    ): ActionResult {

        return actionEngine.dispatch(action)
    }

    fun executeVoice(
        request: VoiceRequest
    ): ActionResult {

        val action = VoicePipeline.process(request)

        return execute(action)
    }
}
```

------------------------------------------------------------------------------
## common/LogCoreLogger.kt
```kotlin
package com.uriel.logpose.logcore.common

object LogCoreLogger {

    fun info(message: String) {
        println(message)
    }

}
```

------------------------------------------------------------------------------
## core/action/ActionRequest.kt
```kotlin
package com.uriel.logpose.logcore.core.action

import com.uriel.logpose.logcore.core.capability.Capability

/**
 * Solicitud lista para ser ejecutada por LogCore.
 */
data class ActionRequest(

    val capability: Capability,

    val parameters: List<Parameter> = emptyList()

)
```

------------------------------------------------------------------------------
## core/action/ActionResult.kt
```kotlin
package com.uriel.logpose.logcore.core.action

sealed interface ActionResult {

    data object Success : ActionResult

    data class Failure(
        val message: String
    ) : ActionResult

    data class BooleanResult(
        val value: Boolean
    ) : ActionResult

}
```

------------------------------------------------------------------------------
## core/action/Parameter.kt
```kotlin
package com.uriel.logpose.logcore.core.action

/**
 * Representa un parámetro extraído de una orden de voz.
 *
 * Ejemplos:
 * - volumen = 7
 * - contacto = Juan
 * - mensaje = "Llego en cinco"
 */
data class Parameter(

    val name: String,

    val value: String
)
```

------------------------------------------------------------------------------
## core/capability/Capability.kt
```kotlin
package com.uriel.logpose.logcore.core.capability

/**
 * Represents an action that LogCore is capable of performing.
 *
 * A capability describes WHAT the system can do,
 * independent of HOW it is implemented.
 */
sealed interface Capability {

    // Música
    data object PlayMusic : Capability
    data object PauseMusic : Capability
    data object NextTrack : Capability
    data object PreviousTrack : Capability
    data object RepeatTrack : Capability
    data object SetVolume : Capability
    data object Mute : Capability
    data object Silence : Capability

    // Navegación
    data object NavigationStatus : Capability
    data object BetterRoute : Capability
    data object TrafficStatus : Capability

    // Llamadas
    data object CallContact : Capability
    data object AnswerCall : Capability
    data object RejectCall : Capability
    data object EndCall : Capability
    data object MuteContact : Capability

    // Mensajes
    data object SendMessage : Capability
    data object ReadMessage : Capability

    // Notificaciones
    data object ReadNotifications : Capability
    data object IgnoreNotifications : Capability

    // Emergencia
    data object Emergency : Capability
}
```

------------------------------------------------------------------------------
## core/intent/Intent.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

/**
 * Representa la intención detectada por el motor de voz.
 * No ejecuta acciones; solamente describe qué quiso hacer el usuario.
 */
enum class Intent {

    // Música
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    REPEAT_TRACK,
    SET_VOLUME,
    MUTE,
    SILENCE,

    // Navegación
    NAVIGATION_STATUS,
    BETTER_ROUTE,
    TRAFFIC_STATUS,

    // Llamadas
    CALL_CONTACT,
    ANSWER_CALL,
    REJECT_CALL,
    END_CALL,
    MUTE_CONTACT,

    // Mensajes
    SEND_MESSAGE,
    READ_MESSAGE,

    // Notificaciones
    READ_NOTIFICATIONS,
    IGNORE_NOTIFICATIONS,

    // Emergencia
    EMERGENCY
}
```

------------------------------------------------------------------------------
## core/intent/IntentCategory.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

/**
 * Categorías principales de intenciones.
 */
enum class IntentCategory {

    MUSIC,

    NAVIGATION,

    CALL,

    MESSAGE,

    NOTIFICATION,

    EMERGENCY
}
```

------------------------------------------------------------------------------
## core/intent/IntentMapper.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

import com.uriel.logpose.logcore.core.capability.Capability

/**
 * Convierte una Intent en la Capability correspondiente.
 */
object IntentMapper {

    fun toCapability(intent: Intent): Capability {

        return when (intent) {

            // Música
            Intent.PLAY_MUSIC -> Capability.PlayMusic
            Intent.PAUSE_MUSIC -> Capability.PauseMusic
            Intent.NEXT_TRACK -> Capability.NextTrack
            Intent.PREVIOUS_TRACK -> Capability.PreviousTrack
            Intent.REPEAT_TRACK -> Capability.RepeatTrack
            Intent.SET_VOLUME -> Capability.SetVolume
            Intent.MUTE -> Capability.Mute
            Intent.SILENCE -> Capability.Silence

            // Navegación
            Intent.NAVIGATION_STATUS -> Capability.NavigationStatus
            Intent.BETTER_ROUTE -> Capability.BetterRoute
            Intent.TRAFFIC_STATUS -> Capability.TrafficStatus

            // Llamadas
            Intent.CALL_CONTACT -> Capability.CallContact
            Intent.ANSWER_CALL -> Capability.AnswerCall
            Intent.REJECT_CALL -> Capability.RejectCall
            Intent.END_CALL -> Capability.EndCall
            Intent.MUTE_CONTACT -> Capability.MuteContact

            // Mensajes
            Intent.SEND_MESSAGE -> Capability.SendMessage
            Intent.READ_MESSAGE -> Capability.ReadMessage

            // Notificaciones
            Intent.READ_NOTIFICATIONS -> Capability.ReadNotifications
            Intent.IGNORE_NOTIFICATIONS -> Capability.IgnoreNotifications

            // Emergencia
            Intent.EMERGENCY -> Capability.Emergency
        }
    }
}
```

------------------------------------------------------------------------------
## core/intent/IntentParser.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

/**
 * Convierte una VoiceRequest en un Intent utilizando
 * los patrones registrados en IntentPatterns.
 */
object IntentParser {

    fun parse(request: VoiceRequest): Intent {

        val text = request.text
            .lowercase()
            .trim()

        for (pattern in IntentPatterns.patterns) {

            if (pattern.phrases.any { phrase ->
                    text.contains(phrase.lowercase())
                }) {
                return pattern.intent
            }
        }

        throw IllegalArgumentException(
            "No se pudo interpretar: ${request.text}"
        )
    }
}
```

------------------------------------------------------------------------------
## core/intent/IntentPattern.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

/**
 * Describe un patrón de reconocimiento para una intención.
 */
data class IntentPattern(

    val category: IntentCategory,

    val intent: Intent,

    val phrases: List<String>

)
```

------------------------------------------------------------------------------
## core/intent/IntentPatterns.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

object IntentPatterns {

    val patterns = listOf(

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PLAY_MUSIC,
            phrases = listOf(
                "reproducir música",
                "poner música",
                "poné música",
                "play",
                "iniciar música"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PAUSE_MUSIC,
            phrases = listOf(
                "pausa",
                "pausar",
                "detener música"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.NEXT_TRACK,
            phrases = listOf(
                "siguiente",
                "siguiente canción"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PREVIOUS_TRACK,
            phrases = listOf(
                "anterior",
                "canción anterior"
            )
        )
    )
}
```

------------------------------------------------------------------------------
## core/intent/VoiceRequest.kt
```kotlin
package com.uriel.logpose.logcore.core.intent

/**
 * Representa una solicitud recibida desde el motor de voz.
 *
 * Esta clase es independiente del proveedor de reconocimiento
 * y contiene únicamente la información necesaria para que
 * LogCore interprete la intención del usuario.
 */
data class VoiceRequest(

    /**
     * Texto reconocido.
     */
    val text: String,

    /**
     * Nivel de confianza del reconocimiento.
     * Valor entre 0.0 y 1.0.
     */
    val confidence: Float
)
```

------------------------------------------------------------------------------
## core/session/Session.kt
```kotlin
package com.uriel.logpose.logcore.core.session

interface Session
```

------------------------------------------------------------------------------
## core/state/ExecutionState.kt
```kotlin
package com.uriel.logpose.logcore.core.state

enum class ExecutionState {
    CREATED,
    RUNNING,
    SUCCESS,
    FAILED,
    CANCELLED,
    TIMEOUT
}
```

------------------------------------------------------------------------------
## orchestrator/ActionEngine.kt
```kotlin
package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
import com.uriel.logpose.logcore.core.capability.Capability
import com.uriel.logpose.logcore.orchestrator.validator.ActionValidator
import com.uriel.logpose.logcore.orchestrator.validator.ValidationResult
import com.uriel.logpose.logcore.services.MusicService

class ActionEngine(

    private val resolver: ServiceResolver,
    private val validator: ActionValidator

) {

    fun dispatch(action: ActionRequest): ActionResult {

        when (val validation = validator.validate(action)) {

            ValidationResult.Valid -> Unit

            is ValidationResult.Invalid ->
                return ActionResult.Failure(validation.reason)
        }

        val service = resolver.resolve(action)

        return when (service) {

            is MusicService -> {

                when (action.capability) {

                    Capability.PlayMusic -> service.play()

                    Capability.PauseMusic -> service.pause()

                    Capability.NextTrack -> service.next()

                    Capability.PreviousTrack -> service.previous()

                    Capability.RepeatTrack -> {
                        // TODO
                    }

                    Capability.SetVolume -> {
                        // TODO
                    }

                    Capability.Mute -> {
                        // TODO
                    }

                    Capability.Silence -> {
                        // TODO
                    }

                    else -> {
                        return ActionResult.Failure(
                            "Capability no soportada por MusicService: ${action.capability}"
                        )
                    }
                }

                ActionResult.Success
            }

            else -> ActionResult.Failure(
                "Service no soportado"
            )
        }
    }
}
```

------------------------------------------------------------------------------
## orchestrator/ServiceResolver.kt
```kotlin
package com.uriel.logpose.logcore.orchestrator

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.capability.Capability
import com.uriel.logpose.logcore.services.MusicService

class ServiceResolver(

    private val musicService: MusicService

) {

    fun resolve(action: ActionRequest): Any {

        return when (action.capability) {

            Capability.PlayMusic,
            Capability.PauseMusic,
            Capability.NextTrack,
            Capability.PreviousTrack,
            Capability.RepeatTrack,
            Capability.SetVolume,
            Capability.Mute,
            Capability.Silence -> musicService

            else -> error(
                "No existe un Service para ${action.capability}"
            )
        }
    }
}
```

------------------------------------------------------------------------------
## orchestrator/validator/ActionValidator.kt
```kotlin
package com.uriel.logpose.logcore.orchestrator.validator

import com.uriel.logpose.logcore.core.action.ActionRequest

/**
 * Responsable de validar si una ActionRequest puede ejecutarse.
 *
 * Actualmente toda acción es válida.
 * En futuras versiones se agregarán reglas como:
 *
 * - Permisos
 * - Bluetooth conectado
 * - GPS disponible
 * - Sesión activa
 * - Estado del dispositivo
 * - Restricciones del usuario
 */
class ActionValidator {

    fun validate(request: ActionRequest): ValidationResult {
        return ValidationResult.Valid
    }
}

/**
 * Resultado de una validación.
 */
sealed class ValidationResult {

    /**
     * La acción puede ejecutarse.
     */
    data object Valid : ValidationResult()

    /**
     * La acción fue rechazada.
     */
    data class Invalid(
        val reason: String
    ) : ValidationResult()
}
```

------------------------------------------------------------------------------
## pipeline/VoicePipeline.kt
```kotlin
package com.uriel.logpose.logcore.pipeline

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.intent.IntentMapper
import com.uriel.logpose.logcore.core.intent.IntentParser
import com.uriel.logpose.logcore.core.intent.VoiceRequest

/**
 * Convierte una VoiceRequest en una ActionRequest lista
 * para ser ejecutada por LogCore.
 */
object VoicePipeline {

    fun process(
        request: VoiceRequest
    ): ActionRequest {

        val intent = IntentParser.parse(request)

        val capability = IntentMapper.toCapability(intent)

        return ActionRequest(
            capability = capability
        )
    }
}
```

------------------------------------------------------------------------------
## providers/MusicProvider.kt
```kotlin
package com.uriel.logpose.logcore.providers

interface MusicProvider {

    fun play()

    fun pause()

    fun stop()

    fun next()

    fun previous()

    fun isPlaying(): Boolean

}
```

------------------------------------------------------------------------------
## services/MusicService.kt
```kotlin
package com.uriel.logpose.logcore.services

import com.uriel.logpose.logcore.providers.MusicProvider

class MusicService(
    private val provider: MusicProvider
) {

    fun play() =
        provider.play()

    fun pause() =
        provider.pause()

    fun stop() =
        provider.stop()

    fun next() =
        provider.next()

    fun previous() =
        provider.previous()

    fun isPlaying(): Boolean =
        provider.isPlaying()

}
```

------------------------------------------------------------------------------
## testing/FakeMusicProvider.kt
```kotlin
package com.uriel.logpose.logcore.testing

import com.uriel.logpose.logcore.providers.MusicProvider
import com.uriel.logpose.logcore.common.LogCoreLogger

class FakeMusicProvider : MusicProvider {

    private var playing = false

    override fun play() {
        playing = true
        println("▶ Playing music")
    }

    override fun pause() {
        playing = false
        println("⏸ Music paused")
    }

    override fun stop() {
        playing = false
        println("⏹ Music stopped")
    }

    override fun next() {
        println("⏭ Next song")
    }

    override fun previous() {
        println("⏮ Previous song")
    }

    override fun isPlaying(): Boolean {
        return playing
    }
    object LogCoreLogger {
    }

}

```

------------------------------------------------------------------------------
## testing/LogCoreDemo.kt
```kotlin
package com.uriel.logpose.logcore.testing

import com.uriel.logpose.logcore.LogCore
import com.uriel.logpose.logcore.core.intent.VoiceRequest
import com.uriel.logpose.logcore.orchestrator.ActionEngine
import com.uriel.logpose.logcore.orchestrator.ServiceResolver
import com.uriel.logpose.logcore.orchestrator.validator.ActionValidator
import com.uriel.logpose.logcore.pipeline.VoicePipeline
import com.uriel.logpose.logcore.services.MusicService

object LogCoreDemo {

    fun run() {

        val provider = FakeMusicProvider()

        val musicService = MusicService(provider)

        val resolver = ServiceResolver(musicService)

        val validator = ActionValidator()

        val engine = ActionEngine(
            resolver,
            validator
        )

        val logCore = LogCore(engine)

        val request = VoiceRequest(
            text = "reproducir música",
            confidence = 0.98f
        )

        val action = VoicePipeline.process(request)

        val result = logCore.execute(action)

        println(result)
    }
}
```

