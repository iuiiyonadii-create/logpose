package com.uriel.logpose.thamis.personalization.privacy

/**
 * Controla qué información puede ser aprendida y persistida por THAMIS.
 */
object PrivacyController {
    
    enum class PrivacyState { PRIVATE, STANDARD, ADAPTIVE }
    
    private var currentState: PrivacyState = PrivacyState.STANDARD

    fun setPrivacy(state: PrivacyState) {
        currentState = state
    }

    fun canStorePattern(): Boolean {
        return currentState != PrivacyState.PRIVATE
    }

    fun canLearnImplicitly(): Boolean {
        return currentState == PrivacyState.ADAPTIVE
    }
}
