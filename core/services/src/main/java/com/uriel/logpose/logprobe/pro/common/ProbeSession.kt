package com.uriel.logpose.logprobe.common

/**
 * Configuracion INMUTABLE de una sesion de captura.
 *
 * Se crea una unica vez, en el instante en que el desarrollador presiona
 * "Start Probe" en ProbeViewModel. No existe forma de mutar sus campos
 * despues de creada: cualquier cambio de alcance requiere terminar la
 * sesion actual y arrancar una nueva.
 *
 * No hay ProbeConfig global mutable en ningun otro lugar del modulo:
 * este objeto ES la unica fuente de verdad de "que se puede capturar
 * ahora mismo".
 */
data class ProbeSession(
    val sessionId: String = ProbeUtils.newEventId(),
    val startedAtMillis: Long = ProbeClock.nowMillis(),

    /**
     * Paquetes que el desarrollador selecciono explicitamente para
     * inspeccionar. Allowlist obligatoria: si esta vacia, ProbeGuard
     * rechaza todo evento (no existe modo "capturar todo").
     */
    val allowedPackages: Set<String>,

    /**
     * Denylist efectiva de esta sesion = denylist fija de ProbeConstants
     * + cualquier paquete adicional que el desarrollador haya sumado.
     * Nunca se le puede restar nada a ProbeConstants.DEFAULT_PACKAGE_DENYLIST.
     */
    val extraDenylist: Set<String> = emptySet()
) {
    val effectiveDenylist: Set<String> =
        ProbeConstants.DEFAULT_PACKAGE_DENYLIST + extraDenylist

    init {
        require(allowedPackages.isNotEmpty()) {
            "ProbeSession requiere al menos un paquete en la allowlist."
        }
        val illegalOverlap = allowedPackages.intersect(effectiveDenylist)
        require(illegalOverlap.isEmpty()) {
            "No se puede iniciar sesion: los paquetes $illegalOverlap " +
                "estan en la denylist obligatoria y no pueden inspeccionarse."
        }
    }
}
