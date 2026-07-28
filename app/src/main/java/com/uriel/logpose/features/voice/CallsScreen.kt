package com.uriel.logpose.features.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.logpose.core.app.AppContainer

@Composable
fun CallsScreen(onOpenDrawer: () -> Unit, isDark: Boolean) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = if (isDark) Color(0xFF00CEC9) else Color.Black

    val settingsState by AppContainer.settingsManager.state.collectAsState()
    val autoAnswer = settingsState.booleans["auto_answer"] ?: false
    val announceCaller = settingsState.booleans["announce_caller"] ?: true
    val emergencyNumber = settingsState.strings["emergency_contact"] ?: "No configurado"
    val assistanceNumber = settingsState.strings["assistance_contact"] ?: "No configurado"

    var showEditDialog by remember { mutableStateOf<String?>(null) } // null, "emergency" o "assistance"
    var tempPhone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = text)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "LLAMADAS",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = "PREFERENCIAS DEL CASCO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )
        
        Spacer(Modifier.height(16.dp))

        CallToggleItem("Respuesta automática", autoAnswer, text, accent) {
            AppContainer.settingsManager.setBoolean("auto_answer", it)
        }

        CallToggleItem("Anunciar contacto", announceCaller, text, accent) {
            AppContainer.settingsManager.setBoolean("announce_caller", it)
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = "CONTACTOS FRECUENTES",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )
        
        Spacer(Modifier.height(16.dp))

        FrequentContactItem(
            name = "CONTACTO DE EMERGENCIA", 
            description = emergencyNumber, 
            icon = Icons.Default.Warning, 
            textColor = text, 
            variantColor = variant
        ) {
            tempPhone = if(emergencyNumber == "No configurado") "" else emergencyNumber
            showEditDialog = "emergency"
        }

        FrequentContactItem(
            name = "ASISTENCIA", 
            description = assistanceNumber, 
            icon = Icons.Default.Support, 
            textColor = text, 
            variantColor = variant
        ) {
            tempPhone = if(assistanceNumber == "No configurado") "" else assistanceNumber
            showEditDialog = "assistance"
        }

        Spacer(Modifier.weight(1f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CONTROL POR VOZ",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = variant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Puedes decir 'LogPose, llama a [contacto]' en cualquier momento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = text.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }

    // Diálogo de edición minimalista
    if (showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White,
            title = { 
                Text(
                    if (showEditDialog == "emergency") "EDITAR EMERGENCIA" else "EDITAR ASISTENCIA",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                    color = text
                )
            },
            text = {
                OutlinedTextField(
                    value = tempPhone,
                    onValueChange = { tempPhone = it },
                    label = { Text("Número de teléfono", color = variant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = variant,
                        focusedTextColor = text,
                        unfocusedTextColor = text
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val key = if (showEditDialog == "emergency") "emergency_contact" else "assistance_contact"
                    AppContainer.settingsManager.setString(key, tempPhone)
                    showEditDialog = null
                }) {
                    Text("GUARDAR", color = if (isDark) accent else Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("CANCELAR", color = variant)
                }
            }
        )
    }
}

@Composable
fun CallToggleItem(label: String, checked: Boolean, textColor: Color, accent: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
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

@Composable
fun FrequentContactItem(name: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, textColor: Color, variantColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F2F6).copy(alpha = if (textColor == Color.White) 0.1f else 1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = textColor)
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                Text(description, style = MaterialTheme.typography.labelSmall, color = variantColor)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Edit, "Editar", modifier = Modifier.size(16.dp), tint = variantColor)
        }
    }
}
