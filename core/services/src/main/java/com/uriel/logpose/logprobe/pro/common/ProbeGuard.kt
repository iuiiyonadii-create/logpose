package com.uriel.logpose.logprobe.common

/**
 * Unico punto de decision de seguridad de LogProbe.
 *
 * Regla de diseno (obligatoria): ningun collector, servicio o parser
 * escribe un evento en memoria sin pasar antes por ProbeGuard. Si
 * ProbeGuard rechaza un dato, ese dato nunca llega a existir en un
 * modelo, en un log, ni en un archivo.
 *
 * ProbeGuard es el UNICO lugar del modulo con autoridad para decidir
 * "esta sesion permite capturar esto". Otros componentes (por ejemplo
 * ProbeTimeline) tambien tienen estado mutable propio, pero solo
 * acumulan datos que ProbeGuard ya aprobo; ninguno decide permisos.
 */
object ProbeGuard {

    @Volatile
    private var activeSession: ProbeSession? = null

    fun currentSession(): ProbeSession? = activeSession

    fun hasActiveSession(): Boolean = activeSession != null

    /**
     * Llamado unicamente por ProbeViewModel al presionar "Start Probe".
     */
    @Synchronized
    fun startSession(session: ProbeSession) {
        activeSession = session
        ProbeLogger.d("Sesion iniciada: ${session.sessionId} paquetes=${session.allowedPackages}")
    }

    /**
     * Llamado unicamente por ProbeViewModel al presionar "Stop Probe".
     * Limpia la referencia: a partir de este punto, TODO evento entrante
     * es rechazado, sin excepcion, incluso si el servicio de Accessibility
     * o Notification sigue tecnicamente registrado en el sistema.
     */
    @Synchronized
    fun stopSession() {
        val id = activeSession?.sessionId
        activeSession = null
        ProbeLogger.d("Sesion detenida: $id")
    }

    /**
     * Decision de paquete: true si el paquete puede generar eventos
     * en la sesion activa. False si no hay sesion, si el paquete no esta
     * en la allowlist, o si esta en la denylist (la denylist siempre gana).
     */
    fun isPackageAllowed(packageName: String?): Boolean {
        val session = activeSession ?: return false
        val normalized = ProbeUtils.normalizePackage(packageName)
        if (normalized.isEmpty()) return false

        if (isDenylisted(normalized, session)) return false

        return session.allowedPackages
            .map { ProbeUtils.normalizePackage(it) }
            .contains(normalized)
    }

    private fun isDenylisted(normalizedPackage: String, session: ProbeSession): Boolean {
        val denylist = session.effectiveDenylist.map { ProbeUtils.normalizePackage(it) }
        if (denylist.contains(normalizedPackage)) return true
        return ProbeConstants.DENYLIST_KEYWORDS.any { keyword ->
            normalizedPackage.contains(keyword)
        }
    }

    /**
     * Decision de nodo de Accessibility. Ademas del paquete, un nodo
     * individual puede estar marcado como sensible por el propio framework
     * (isPassword) o por heuristica de campo (hints tipo "password",
     * "pin", "cvv", "otp", "token").
     */
    fun isAccessibilityNodeAllowed(
        packageName: String?,
        isPasswordField: Boolean,
        fieldHint: String? = null
    ): Boolean {
        if (!isPackageAllowed(packageName)) return false
        if (isPasswordField) return false
        val hint = fieldHint?.lowercase().orEmpty()
        val sensitiveHints = setOf("password", "contraseña", "pin", "cvv", "otp", "token", "clave")
        if (sensitiveHints.any { hint.contains(it) }) return false
        return true
    }

    /**
     * Decision de notificacion. Ademas del paquete, se aplica la misma
     * regla que a Accessibility: nunca contenido de mensajeria, nunca
     * texto que luzca como codigo/credencial.
     */
    fun isNotificationAllowed(packageName: String?): Boolean = isPackageAllowed(packageName)
}
