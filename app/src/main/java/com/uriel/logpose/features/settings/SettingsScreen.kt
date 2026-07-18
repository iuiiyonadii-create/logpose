package com.uriel.logpose.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uriel.logpose.core.app.AppContainer

/**
 * Skeleton de la pantalla de Ajustes (Sprint 06, Settings Core Foundation).
 *
 * Este Sprint construye unicamente la infraestructura de Settings Core: la
 * pantalla observa [SettingsManager.state] y muestra su forma general, pero
 * deliberadamente NO incluye ninguna fila de ajuste, toggle ni logica de
 * negocio (eso pertenece a un Sprint futuro de UI final, ver Sprint 06 /
 * OBJETIVO).
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val state by AppContainer.settingsManager.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (state.isLoaded) "Estado: cargado" else "Estado: cargando..."
        )

        Text(
            text = "Claves almacenadas: " +
                "${state.strings.size + state.booleans.size + state.ints.size}"
        )
    }
}
