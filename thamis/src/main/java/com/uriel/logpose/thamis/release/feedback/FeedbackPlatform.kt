package com.uriel.logpose.thamis.release.feedback

import com.uriel.logpose.thamis.release.model.FeedbackCategory
import com.uriel.logpose.thamis.release.model.FeedbackReport

/**
 * Plataforma para la gestión centralizada de retroalimentación de usuarios.
 */
object FeedbackPlatform {
    private val reports = mutableListOf<FeedbackReport>()

    fun submit(issue: String, category: FeedbackCategory) {
        reports.add(FeedbackReport(issue, category, priority = 50))
    }

    fun getPendingBugs(): List<FeedbackReport> = 
        reports.filter { it.category == FeedbackCategory.BUG && it.status == "OPEN" }

    fun markResolved(issue: String) {
        reports.find { it.issue == issue }?.status = "RESOLVED"
    }
}
