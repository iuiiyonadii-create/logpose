package com.uriel.logpose.thamis.context

import java.util.*

/**
 * Gestiona el foco activo del sistema THAMIS.
 */
object FocusManager {
    private val focusStack = Stack<FocusDomain>()
    private var currentFocus: FocusDomain = FocusDomain.NONE

    fun requestFocus(domain: FocusDomain) {
        if (currentFocus != domain) {
            if (currentFocus != FocusDomain.NONE) {
                focusStack.push(currentFocus)
            }
            currentFocus = domain
            ContextMetrics.recordFocusChange()
        }
    }

    fun releaseFocus(domain: FocusDomain) {
        if (currentFocus == domain) {
            currentFocus = if (focusStack.isNotEmpty()) focusStack.pop() else FocusDomain.NONE
            ContextMetrics.recordFocusChange()
        } else {
            focusStack.remove(domain)
        }
    }

    fun getCurrentFocus(): FocusDomain = currentFocus

    fun reset() {
        focusStack.clear()
        currentFocus = FocusDomain.NONE
    }
}

/**
 * Métricas de rendimiento del contexto.
 */
object ContextMetrics {
    var focusChanges = 0
    var snapshotsCreated = 0
    var contextsExpired = 0
    var recoveriesPerformed = 0
    var totalSessionTimeMs = 0L

    fun recordFocusChange() { focusChanges++ }
    fun recordSnapshot() { snapshotsCreated++ }
    fun recordExpiration() { contextsExpired++ }
    fun recordRecovery() { recoveriesPerformed++ }
}
