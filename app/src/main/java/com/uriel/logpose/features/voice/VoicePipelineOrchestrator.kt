package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.core.parser.FastParser
import com.uriel.logpose.core.parser.ParseResult
import com.uriel.logpose.thamis.language.PhoneticEngine
import com.thamis.lab.core.contracts.command.LogPoseCommand
import kotlinx.coroutines.*

/**
 * VoicePipelineOrchestrator: El cerebro unificado de voz.
 */
class VoicePipelineOrchestrator(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun processVoiceResult(rawText: String, confidence: Float) {
        scope.launch(Dispatchers.Default) { // SINCRO CLAUDE: Procesamos fuera del Main Thread
            // 1. Corrección fonética
            val phoneticClean = PhoneticEngine.normalize(rawText)
            if (phoneticClean.isEmpty()) return@launch

            // 2. Parseo de intención (Aquí se hace el cálculo pesado de Levenshtein)
            val result = FastParser.parse(phoneticClean)
            
            // 3. Ejecución en el hilo principal
            withContext(Dispatchers.Main) {
                if (result is ParseResult.Success) {
                    LogPoseLogger.i("Orchestrator: Ejecutando comando -> ${result.command::class.simpleName}")
                    CommandDispatcher.execute(result.command)
                }
            }
        }
    }

    /**
     * SINCRO CLAUDE: Procesa una query directa (salta el parser).
     * Usado para slots numéricos.
     */
    fun processMusicQuery(query: String) {
        LogPoseLogger.i("Orchestrator: Ejecutando query directa -> '$query'")
        CommandDispatcher.execute(LogPoseCommand.PlayMusic(query))
    }

    fun cancel() {
        scope.cancel()
    }
}
