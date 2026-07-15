package com.uriel.logpose.thamis.evidence

/**
 * Recolecta evidencias para que THAMIS pueda razonar.
 */
class EvidenceCollector {

    private val evidences = mutableListOf<Evidence>()

    fun add(evidence: Evidence) {
        evidences.add(evidence)
    }

    fun addAll(items: Collection<Evidence>) {
        evidences.addAll(items)
    }

    fun clear() {
        evidences.clear()
    }

    fun getAll(): List<Evidence> {
        return evidences.toList()
    }

}