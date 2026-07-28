package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence
import com.uriel.logpose.thamis.cognitive.model.Hypothesis
import com.uriel.logpose.thamis.cognitive.model.WorldState

/**
 * Evalúa intenciones de navegación basadas en verbos y contexto de GPS.
 * Hardening v1.0: Incluye penalizadores por velocidad y prioridad por conducción.
 */
class NavigationEvidenceEvaluator : EvidenceEvaluator {
    override fun evaluate(hypothesis: Hypothesis, worldState: WorldState): List<Evidence> {
        val evidences = mutableListOf<Evidence>()
        val goal = hypothesis.candidateGoal

        if (goal.category == com.uriel.logpose.thamis.cognitive.model.Goal.Category.NAVIGATION) {
            // A favor: GPS disponible (+0.3)
            evidences.add(Evidence(
                type = Evidence.Type.POSITIVE,
                source = Evidence.Source.CONTEXT_WORLD,
                impact = 0.3f,
                description = "GPS disponible",
                expirationMs = 10000L
            ))

            // A favor: Destino reconocido (+0.4)
            if (goal.parameters.containsKey("destination") || goal.targetState.contains("route")) {
                evidences.add(Evidence(
                    type = Evidence.Type.POSITIVE,
                    source = Evidence.Source.GRAMMAR,
                    impact = 0.4f,
                    description = "Destino o intención de ruta reconocida",
                    expirationMs = 5000L
                ))
            }

            // A favor: Conducción activa (+0.2)
            if (worldState.driving.isMoving) {
                evidences.add(Evidence(
                    type = Evidence.Type.POSITIVE,
                    source = Evidence.Source.CONTEXT_WORLD,
                    impact = 0.2f,
                    description = "Conducción activa favorece navegación",
                    expirationMs = 30000L
                ))
            }

            // En contra: Velocidad alta (-0.2)
            if (worldState.driving.speedKmh > 100) {
                evidences.add(Evidence(
                    type = Evidence.Type.NEGATIVE,
                    source = Evidence.Source.CONTEXT_WORLD,
                    impact = -0.2f,
                    description = "Velocidad alta aumenta riesgo de distracción",
                    expirationMs = 2000L
                ))
            }

            // En contra: Llamada activa (-0.5) - Máxima prioridad para comunicación
            if (worldState.system.activeCall) {
                evidences.add(Evidence(
                    type = Evidence.Type.NEGATIVE,
                    source = Evidence.Source.CONTEXT_SYSTEM,
                    impact = -0.5f,
                    description = "Llamada activa bloquea intenciones secundarias",
                    expirationMs = 10000L
                ))
            }
        }
        
        return evidences
    }
}
