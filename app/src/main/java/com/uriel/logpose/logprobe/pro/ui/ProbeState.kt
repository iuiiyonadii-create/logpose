package com.uriel.logpose.logprobe.ui

/**
 * Estado de UI de LogProbe. El campo `allowedPackagesInput` representa
 * lo que el desarrollador esta escribiendo/seleccionando ANTES de
 * arrancar la sesion; una vez que arranca, la seleccion queda congelada
 * dentro de ProbeSession (ver ProbeViewModel.startProbe).
 */
data class ProbeState(
    val isSessionActive: Boolean = false,
    val sessionId: String? = null,
    val allowedPackagesInput: String = "",
    val extraDenylistInput: String = "",
    val eventCount: Int = 0,
    val statusMessage: String = "Sesion detenida.",
    val lastExportPath: String? = null,
    val errorMessage: String? = null
)
