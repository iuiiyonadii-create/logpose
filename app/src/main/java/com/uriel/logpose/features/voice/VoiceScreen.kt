package com.uriel.logpose.features.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uriel.logpose.features.settings.SettingsViewModel

@Composable
fun VoiceScreen(
    onOpenDrawer: () -> Unit, 
    isDark: Boolean,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = if (isDark) Color(0xFF00CEC9) else Color.Black

    val settings = viewModel.settingsManager
    val settingsState by viewModel.state.collectAsState()

    // Estados reales desde Settings
    val assistantName = settingsState.strings["assistant_name"] ?: "LogPose"
    val voiceType = settingsState.strings["voice_type"] ?: "Neutral (Sistema)"
    val wakeWordEnabled = settingsState.booleans["wake_word_enabled"] ?: true
    val sensitivity = settingsState.ints["voice_sensitivity"] ?: 70

    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(assistantName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = text)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "CONFIGURACIÓN DE VOZ",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Asistente
        Text(
            text = "ASISTENTE",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Spacer(Modifier.height(24.dp))

        VoiceOptionItem("Nombre del Asistente", assistantName, text, variant) {
            tempName = assistantName
            showNameDialog = true
        }
        
        VoiceOptionItem("Tipo de Voz", voiceType, text, variant) {
            // Ciclar entre tipos de voz para este ejemplo
            val nextVoice = if (voiceType.contains("Neutral")) "Humana (Experimental)" else "Neutral (Sistema)"
            settings.setString("voice_type", nextVoice)
        }

        Spacer(Modifier.height(48.dp))

        // Reconocimiento
        Text(
            text = "RECONOCIMIENTO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )

        Spacer(Modifier.height(24.dp))

        VoiceToggleItem("Palabra de activación", assistantName, wakeWordEnabled, text, variant, accent) {
            settings.setBoolean("wake_word_enabled", it)
        }
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "SENSIBILIDAD ($sensitivity%)",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Slider(
            value = sensitivity.toFloat() / 100f,
            onValueChange = { settings.setInt("voice_sensitivity", (it * 100).toInt()) },
            colors = SliderDefaults.colors(
                thumbColor = text,
                activeTrackColor = accent,
                inactiveTrackColor = variant.copy(alpha = 0.2f)
            )
        )

        Spacer(Modifier.weight(1f))

        // Botón de Prueba de Voz
        OutlinedButton(
            onClick = { 
                FeedbackManager.speak("Hola, soy $assistantName. Estoy listo para ayudarte en la ruta.")
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, variant.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = text)
            Spacer(Modifier.width(12.dp))
            Text("PROBAR VOZ", color = text, style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp))
        }
        
        Spacer(Modifier.height(24.dp))
    }

    // Diálogo para cambiar nombre
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White,
            title = { Text("NOMBRE DEL ASISTENTE", style = MaterialTheme.typography.labelLarge, color = text) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        focusedTextColor = text,
                        unfocusedTextColor = text
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    settings.setString("assistant_name", tempName)
                    showNameDialog = false
                }) {
                    Text("GUARDAR", color = if (isDark) accent else Color.Black)
                }
            }
        )
    }
}

@Composable
fun VoiceOptionItem(label: String, value: String, textColor: Color, variantColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
                Text(value, style = MaterialTheme.typography.bodySmall, color = variantColor)
            }
            Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp), tint = variantColor)
        }
    }
}

@Composable
fun VoiceToggleItem(label: String, sublabel: String, checked: Boolean, textColor: Color, variantColor: Color, accent: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Text(sublabel, style = MaterialTheme.typography.labelSmall, color = variantColor)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = textColor,
                checkedTrackColor = accent.copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFFDFE6E9),
                uncheckedTrackColor = Color(0xFFF1F2F6)
            )
        )
    }
}
