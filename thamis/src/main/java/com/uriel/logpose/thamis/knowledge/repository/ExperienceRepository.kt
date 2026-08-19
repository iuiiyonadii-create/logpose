package com.uriel.logpose.thamis.knowledge.repository

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE FINAL — ENGINEERING LEARNING
 * Repositorio de experiencias exitosas para aprendizaje técnico proactivo.
 */
object ExperienceRepository {

    data class Experience(
        val taskId: String,
        val solution: String,
        val efficiency: Float // 0.0 to 1.0
    )

    private val experiences = mutableListOf<Experience>()

    fun learn(taskId: String, solution: String, efficiency: Float) {
        experiences.add(Experience(taskId, solution, efficiency))
        LogPoseLogger.i("ExperienceRepository: Aprendida nueva solución para $taskId")
    }

    fun getBestSolution(taskId: String): String? {
        return experiences.filter { it.taskId == taskId }
            .maxByOrNull { it.efficiency }?.solution
    }
}
