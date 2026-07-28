package com.uriel.logpose.thamis.beta.feedback

import com.uriel.logpose.thamis.beta.model.UserFeedback

/**
 * Recopila y organiza la retroalimentación de los usuarios beta.
 */
object FeedbackCollector {
    private val feedbackList = mutableListOf<UserFeedback>()

    fun submitFeedback(feedback: UserFeedback) {
        feedbackList.add(feedback)
    }

    fun getAverageHelpfulness(): Double {
        if (feedbackList.isEmpty()) return 0.0
        return feedbackList.map { it.helpfulnessRating }.average()
    }

    fun getFeedbackCount(): Int = feedbackList.size
}
