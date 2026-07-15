package com.uriel.logpose.logprobe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

/**
 * UI deliberadamente simple, como pide la especificacion: campos para
 * definir alcance de sesion + 4 botones + estado actual. No hay forma
 * en esta pantalla de "desactivar" la denylist: ese control ni siquiera
 * existe en el ViewModel.
 */
@Composable
fun ProbeScreen(viewModel: ProbeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSessionActive) {
        while (state.isSessionActive) {
            viewModel.refreshEventCount()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("LogProbe", style = MaterialTheme.typography.headlineSmall)
        Text(state.statusMessage, style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = state.allowedPackagesInput,
            onValueChange = viewModel::onAllowedPackagesInputChanged,
            label = { Text("Packages a investigar (separados por coma)") },
            enabled = !state.isSessionActive,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.extraDenylistInput,
            onValueChange = viewModel::onExtraDenylistInputChanged,
            label = { Text("Denylist adicional (opcional)") },
            enabled = !state.isSessionActive,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Eventos capturados: ${state.eventCount}")

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        state.lastExportPath?.let {
            Text("Ultima exportacion: $it", style = MaterialTheme.typography.bodySmall)
        }

        Button(onClick = viewModel::startProbe, enabled = !state.isSessionActive) {
            Text("Start Probe")
        }
        Button(onClick = viewModel::stopProbe, enabled = state.isSessionActive) {
            Text("Stop Probe")
        }
        Button(onClick = viewModel::exportProbe) {
            Text("Export")
        }
        Button(onClick = viewModel::clearData, enabled = !state.isSessionActive) {
            Text("Limpiar")
        }
    }
}
