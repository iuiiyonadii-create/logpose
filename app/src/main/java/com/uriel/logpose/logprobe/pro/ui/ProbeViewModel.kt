package com.uriel.logpose.logprobe.ui

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.logprobe.bluetooth.BluetoothProbe
import com.uriel.logpose.logprobe.collectors.AccessibilityCollector
import com.uriel.logpose.logprobe.collectors.BluetoothCollector
import com.uriel.logpose.logprobe.collectors.CollectorManager
import com.uriel.logpose.logprobe.collectors.MediaCollector
import com.uriel.logpose.logprobe.collectors.NotificationCollector
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeLogger
import com.uriel.logpose.logprobe.common.ProbeSession
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.media.MediaProbe
import com.uriel.logpose.logprobe.notification.NotificationProbeService
import com.uriel.logpose.logprobe.reports.ProbeReportBuilder
import com.uriel.logpose.logprobe.reports.ProbeTimeline
import com.uriel.logpose.logprobe.storage.ProbeStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Unico punto que crea un ProbeSession (al presionar Start) y el unico
 * que llama ProbeGuard.startSession / CollectorManager.stopAll (via
 * startProbe/stopProbe). No expone ninguna forma de mutar la sesion
 * una vez iniciada.
 */
class ProbeViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ProbeStorage(application)

    private val notificationListenerComponent =
        ComponentName(application, NotificationProbeService::class.java)

    private val collectorManager = CollectorManager(
        pollingCollectors = listOf(
            BluetoothCollector(BluetoothProbe(application)),
            MediaCollector(MediaProbe(application, notificationListenerComponent))
        ),
        serviceBackedCollectors = listOf(
            AccessibilityCollector,
            NotificationCollector
        )
    )

    private val _state = MutableStateFlow(ProbeState())
    val state: StateFlow<ProbeState> = _state

    fun onAllowedPackagesInputChanged(value: String) {
        _state.update { it.copy(allowedPackagesInput = value, errorMessage = null) }
    }

    fun onExtraDenylistInputChanged(value: String) {
        _state.update { it.copy(extraDenylistInput = value) }
    }

    fun startProbe() {
        val current = _state.value
        val allowed = parsePackageList(current.allowedPackagesInput)
        val extraDeny = parsePackageList(current.extraDenylistInput)

        if (allowed.isEmpty()) {
            _state.update {
                it.copy(errorMessage = "Ingresa al menos un package a inspeccionar antes de iniciar.")
            }
            return
        }

        val session = try {
            ProbeSession(allowedPackages = allowed, extraDenylist = extraDeny)
        } catch (e: IllegalArgumentException) {
            _state.update { it.copy(errorMessage = e.message) }
            return
        }

        ProbeTimeline.clear()
        collectorManager.startAll(session)

        _state.update {
            it.copy(
                isSessionActive = true,
                sessionId = session.sessionId,
                statusMessage = "Sesion activa: ${session.sessionId}",
                eventCount = 0,
                errorMessage = null,
                lastExportPath = null
            )
        }
    }

    fun stopProbe() {
        collectorManager.stopAll()
        _state.update {
            it.copy(
                isSessionActive = false,
                statusMessage = "Sesion detenida. Eventos capturados: ${ProbeTimeline.snapshot().size}",
                eventCount = ProbeTimeline.snapshot().size
            )
        }
    }

    fun exportProbe() {
        val session = ProbeGuard.currentSession()
        val sessionIdForReport = session?.sessionId ?: _state.value.sessionId
        if (sessionIdForReport == null) {
            _state.update { it.copy(errorMessage = "No hay datos de sesion para exportar.") }
            return
        }

        viewModelScope.launch {
            try {
                // Si la sesion sigue activa, se construye el reporte con
                // los datos acumulados hasta este instante sin detenerla.
                val effectiveSession = session ?: run {
                    _state.update { it.copy(errorMessage = "La sesion ya fue detenida y limpiada; no hay datos para exportar.") }
                    return@launch
                }
                val report = ProbeReportBuilder.build(effectiveSession)
                val zip = storage.writeZip(report)
                _state.update {
                    it.copy(
                        statusMessage = "Exportado: ${zip.name}",
                        lastExportPath = zip.absolutePath
                    )
                }
            } catch (t: Throwable) {
                ProbeLogger.e("Error exportando", t)
                _state.update { it.copy(errorMessage = "Error exportando: ${t.message}") }
            }
        }
    }

    fun clearData() {
        if (_state.value.isSessionActive) {
            _state.update { it.copy(errorMessage = "Deten la sesion antes de limpiar.") }
            return
        }
        ProbeTimeline.clear()
        storage.clearAll()
        _state.update {
            it.copy(
                eventCount = 0,
                statusMessage = "Datos limpiados.",
                lastExportPath = null,
                sessionId = null
            )
        }
    }

    /** Llamado periodicamente desde ProbeScreen mientras la sesion esta activa. */
    fun refreshEventCount() {
        if (!_state.value.isSessionActive) return
        _state.update { it.copy(eventCount = ProbeTimeline.snapshot().size) }
    }

    private fun parsePackageList(raw: String): Set<String> =
        raw.split(",", "\n", " ")
            .map { ProbeUtils.normalizePackage(it) }
            .filter { it.isNotEmpty() }
            .toSet()

    override fun onCleared() {
        super.onCleared()
        if (ProbeGuard.hasActiveSession()) {
            collectorManager.stopAll()
        }
    }
}
