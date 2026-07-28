package com.uriel.logpose.thamis.intelligence.understanding

/**
 * FASE 27.0 — THAMIS AUTONOMOUS ENGINEERING CORE
 * FASE 2: REQUIREMENT EXTRACTION ENGINE
 */
object RequirementExtractor {

    enum class RequirementType {
        FUNCTIONAL, NON_FUNCTIONAL, SECURITY, PERFORMANCE, UX
    }

    data class Requirement(
        val description: String,
        val type: RequirementType,
        val priority: Int = 1
    )

    fun extract(concept: IdeaAnalyzer.ProjectConcept): List<Requirement> {
        return listOf(
            Requirement("Funcionalidad principal basada en ${concept.name}", RequirementType.FUNCTIONAL),
            Requirement("Garantizar seguridad de datos", RequirementType.SECURITY),
            Requirement("Interfaz intuitiva", RequirementType.UX)
        )
    }
}
