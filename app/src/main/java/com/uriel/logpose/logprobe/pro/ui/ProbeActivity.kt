package com.uriel.logpose.logprobe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

/**
 * Punto de entrada de LogProbe. Se lanza manualmente por el desarrollador
 * (por ejemplo desde un menu de debug de LogPose); no se auto-inicia
 * ni queda registrada como launcher de la app principal.
 *
 * NOTA: usa MaterialTheme por defecto para no asumir el theme propio
 * de LogPose, que no vimos. Reemplazar por el tema real del proyecto
 * si corresponde.
 */
class ProbeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProbeScreen()
                }
            }
        }
    }
}
