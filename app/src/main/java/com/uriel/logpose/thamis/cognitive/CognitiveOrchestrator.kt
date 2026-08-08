package com.uriel.logpose.thamis.cognitive

import com.uriel.logpose.thamis.cognitive.model.*
import com.uriel.logpose.thamis.cognitive.engine.EvidenceEngine
import com.uriel.logpose.thamis.cognitive.decision.DecisionEngine
import com.uriel.logpose.thamis.cognitive.decision.RiskEvaluator
import com.uriel.logpose.thamis.entity.EntityExtractor
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.safety.SafetyGate
import com.uriel.logpose.thamis.actuator.music.MusicActuator
import com.uriel.logpose.thamis.actuator.NavigationActuator
import com.uriel.logpose.thamis.security.ThamisAuthorityGate
import com.uriel.logpose.thamis.navigation.NavigationShadowController
import com.uriel.logpose.thamis.navigation.model.NavigationContext
import com.uriel.logpose.thamis.navigation.NavigationAuthorityValidator
import com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory
import com.uriel.logpose.thamis.navigation.provider.GoogleMapsProvider
import com.uriel.logpose.thamis.navigation.provider.WazeProvider
import com.uriel.logpose.thamis.navigation.provider.LegacyNavigationProvider
import com.uriel.logpose.thamis.actuator.CommunicationActuator
import com.uriel.logpose.thamis.communication.provider.PhoneCallProvider
import com.uriel.logpose.thamis.communication.provider.WhatsAppProvider
import com.uriel.logpose.thamis.communication.provider.CommunicationProviderFactory
import com.uriel.logpose.thamis.navigation.audit.NavigationAuthorityTrace
import com.uriel.logpose.thamis.capabilities.CapabilityManager
import com.uriel.logpose.thamis.cognitive.utils.ThamisLogProvider

/**
 * El orquestador del cerebro cognitivo THAMIS v3.0.
 * Gestiona el pipeline desde la percepción hasta la actuación controlada.
 * Optimizado para robustez y entendimiento multi-dominio.
 */
object CognitiveOrchestrator {

    private val musicActuator = MusicActuator()
    private val navigationActuator = NavigationActuator()
    private val communicationActuator = CommunicationActuator()
    private var initialized = false

    private fun logI(tag: String, msg: String) = ThamisLogProvider.logger?.i(tag, msg)
    private fun logW(tag: String, msg: String) = ThamisLogProvider.logger?.w(tag, msg)
    private fun logE(tag: String, msg: String) = ThamisLogProvider.logger?.e(tag, msg)

    fun initProviders() {
        if (initialized) return
        try {
            NavigationProviderFactory.registerProvider(GoogleMapsProvider())
            NavigationProviderFactory.registerProvider(WazeProvider())
            NavigationProviderFactory.registerProvider(LegacyNavigationProvider())
            
            CommunicationProviderFactory.registerProvider(PhoneCallProvider())
            CommunicationProviderFactory.registerProvider(WhatsAppProvider())

            CapabilityManager.initialize()
            initialized = true
            logI("THAMIS_ORCHESTRATOR", "Providers initialized successfully.")
        } catch (e: Exception) {
            logE("THAMIS_ORCHESTRATOR", "Critical error initializing providers: ${e.message}")
        }
    }

    fun process(text: String, legacyIntent: Intent, worldState: WorldState): ThamisDecision {
        val startTime = System.currentTimeMillis()
        initProviders()
        
        // 1. Clasificación Cognitiva (Mapeo de Categoría)
        val category = mapIntentToCategory(legacyIntent)

        // 2. Registro de contexto para auditoría (Fase 14)
        if (category == Goal.Category.NAVIGATION) {
            recordNavigationContext(text, worldState)
        }

        // 3. Generación de Hipótesis (Mejorado con extracción de entidades robusta)
        val hypothesis = Hypothesis(
            candidateGoal = Goal(
                category = category,
                priority = if (category == Goal.Category.COMMUNICATION) 0.9f else 0.5f,
                parameters = mapOf("raw_text" to text),
                targetState = "intent_execution",
            ),
            entities = EntityExtractor.extract(legacyIntent, text),
            evidences = emptyList(),
            rawConfidence = 0.5f
        )

        // 4. Razonamiento por Evidencias (Ciclo Mental)
        val enrichedHypothesis = EvidenceEngine.process(hypothesis, worldState)
        
        // 5. Evaluación de Riesgo y Juicio Final
        val risk = RiskEvaluator.evaluate(enrichedHypothesis.candidateGoal)
        val evaluation = Evaluation(
            hypothesis = enrichedHypothesis,
            finalScore = enrichedHypothesis.rawConfidence,
            risk = risk,
            reasoning = "Decision based on evidence, risk and world context."
        )

        // 6. Veredicto Cognitivo
        val decision = DecisionEngine.decide(evaluation, worldState)

        // 7. Actuación Controlada (Fase 15)
        handleActuation(decision, worldState, category, startTime)

        return decision
    }

    private fun mapIntentToCategory(intent: Intent): Goal.Category {
        return when (intent) {
            Intent.PLAY_MUSIC, Intent.PAUSE_MUSIC, Intent.NEXT_TRACK, Intent.PREVIOUS_TRACK, Intent.SET_VOLUME -> Goal.Category.MULTIMEDIA
            Intent.NAVIGATE, Intent.START_ROUTE, Intent.GO_HOME, Intent.GO_WORK, Intent.STOP_NAVIGATION -> Goal.Category.NAVIGATION
            Intent.CALL_CONTACT, Intent.ANSWER_CALL, Intent.REJECT_CALL, Intent.SEND_MESSAGE -> Goal.Category.COMMUNICATION
            else -> Goal.Category.UNKNOWN
        }
    }

    private fun recordNavigationContext(text: String, worldState: WorldState) {
        val navContext = NavigationContext(
            gpsAvailable = worldState.driving.gpsAvailable,
            currentLocation = null,
            activeRoute = if (worldState.driving.hasActiveNavigation) "ACTIVE" else null,
            destination = null,
            speedKmh = worldState.driving.speedKmh,
        )
        NavigationShadowController.observe(text, navContext)
    }

    private fun handleActuation(decision: ThamisDecision, worldState: WorldState, category: Goal.Category, startTime: Long) {
        if (category == Goal.Category.NAVIGATION) {
            processNavigationActuation(decision, worldState, startTime)
        } else if (SafetyGate.isApproved(decision, worldState)) {
            val domain = mapCategoryToDomain(category)
            if (domain != null && ThamisAuthorityGate.canExecute(domain)) {
                executeAction(decision)
            } else {
                logW("THAMIS_AUTHORITY", "Domain=$domain Permission=DENIED")
            }
        }
    }

    private fun processNavigationActuation(decision: ThamisDecision, worldState: WorldState, startTime: Long) {
        val validation = NavigationAuthorityValidator.validate(decision, worldState)
        val executionTime = System.currentTimeMillis() - startTime
        
        val trace = NavigationAuthorityTrace(
            authorityGranted = validation is NavigationAuthorityValidator.ValidationResult.APPROVED,
            authorityDenied = validation is NavigationAuthorityValidator.ValidationResult.DENY,
            safetyDecision = if (worldState.driving.speedKmh > 100) "CAUTION" else "SAFE",
            confidence = decision.winningEvaluation?.finalScore ?: 0f,
            destination = decision.winningEvaluation?.hypothesis?.entities?.get("destination") ?: "unknown",
            provider = NavigationProviderFactory.getBestAvailableProvider().getProviderInfo().type,
            executionTimeMs = executionTime,
            validatorResult = validation.toString(),
            reason = (validation as? NavigationAuthorityValidator.ValidationResult.DENY)?.reason ?: "Decision processed"
        )
        
        logI("THAMIS_AUTHORITY", "Domain=NAVIGATION Permission=${if(trace.authorityGranted) "GRANTED" else "DENIED"} Confidence=${trace.confidence}")

        if (validation is NavigationAuthorityValidator.ValidationResult.APPROVED) {
            navigationActuator.execute(decision)
        } else {
            logW("THAMIS_NAVIGATION", "Authority denied. Reason: ${trace.reason}")
        }
    }

    private fun mapCategoryToDomain(category: Goal.Category): ThamisAuthorityGate.Domain? {
        return when (category) {
            Goal.Category.MULTIMEDIA -> ThamisAuthorityGate.Domain.MULTIMEDIA
            Goal.Category.COMMUNICATION -> ThamisAuthorityGate.Domain.COMMUNICATION
            Goal.Category.NAVIGATION -> ThamisAuthorityGate.Domain.NAVIGATION
            else -> null
        }
    }

    private fun executeAction(decision: ThamisDecision) {
        try {
            when (decision.intent) {
                Intent.PLAY_MUSIC, Intent.PAUSE_MUSIC, Intent.NEXT_TRACK, Intent.PREVIOUS_TRACK, Intent.SET_VOLUME -> {
                    musicActuator.execute(decision)
                }
                Intent.NAVIGATE, Intent.START_ROUTE, Intent.STOP_NAVIGATION, Intent.CANCEL_ROUTE, 
                Intent.CHANGE_DESTINATION, Intent.REPEAT_INSTRUCTION, Intent.NEXT_STEP,
                Intent.GO_HOME, Intent.GO_WORK, Intent.GO_FAVORITE, Intent.RESUME_ROUTE -> {
                    navigationActuator.execute(decision)
                }
                Intent.CALL_CONTACT, Intent.ANSWER_CALL, Intent.REJECT_CALL, Intent.SEND_MESSAGE -> {
                    communicationActuator.execute(decision)
                }
                else -> {}
            }
        } catch (e: Exception) {
            logE("THAMIS_ORCHESTRATOR", "Execution error for ${decision.intent}: ${e.message}")
        }
    }
}
