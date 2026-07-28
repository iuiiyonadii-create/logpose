package com.uriel.logpose.features.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.logpose.thamis.multiagent.CollaborationBus

object GloveUiTokens {
    val hugeTouchTarget = 96.dp
    val commandTextSize = 40.sp
    val statusIconSize = 36.dp
    val spacingXL = 24.dp

    val bgColor = Color(0xFF000000)
    val activeGreen = Color(0xFF00E676)
    val dangerRed = Color(0xFFFF3B30)
    val idleGray = Color(0xFF1C1C1E)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF8E8E93)
}

@Composable
fun TripDashboardScreen(
    uiState: TripUiState,
    onStartTrip: () -> Unit,
    onEndTrip: () -> Unit,
    onDismissBanner: () -> Unit
) {
    var isProcessingTap by remember { mutableStateOf(false) }
    var showLabPanel by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isTripActive, uiState.tripStatus) {
        isProcessingTap = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GloveUiTokens.bgColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GloveUiTokens.spacingXL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BARRA DE ESTADO (Clickable para abrir el Lab Panel)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLabPanel = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusChip(
                        icon = Icons.Default.BatteryFull, 
                        label = "${uiState.phoneBatteryPct}%", 
                        tint = GloveUiTokens.textPrimary
                    )
                    StatusChip(
                        icon = if (uiState.headsetConnected) Icons.Default.BluetoothAudio else Icons.Default.BluetoothDisabled,
                        label = if (uiState.headsetConnected) "CASCO OK" else "SIN CASCO",
                        tint = if (uiState.headsetConnected) GloveUiTokens.activeGreen else GloveUiTokens.dangerRed
                    )
                }

                // BANNER DE ERROR
                AnimatedVisibility(visible = uiState.bannerText != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GloveUiTokens.dangerRed.copy(alpha = 0.2f))
                            .clickable { onDismissBanner() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = GloveUiTokens.dangerRed)
                        Spacer(Modifier.width(12.dp))
                        Text(uiState.bannerText ?: "", color = GloveUiTokens.dangerRed, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(40.dp))

                // NAVEGACIÓN
                AnimatedVisibility(visible = uiState.navigationInstruction != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = GloveUiTokens.activeGreen.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Navigation, null, tint = GloveUiTokens.activeGreen, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = uiState.navigationInstruction ?: "",
                                color = GloveUiTokens.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                        }
                    }
                }

                // VISOR DE COMANDOS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(GloveUiTokens.idleGray)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (uiState.isTripActive) "THAMIS ESCUCHA:" else "LOGPOSE IDLE",
                            color = GloveUiTokens.textSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = uiState.lastRecognizedCommand?.uppercase() ?: "---",
                            color = GloveUiTokens.textPrimary,
                            fontSize = GloveUiTokens.commandTextSize,
                            lineHeight = 48.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(GloveUiTokens.spacingXL))

                // BOTÓN DE ACCIÓN
                Button(
                    onClick = { 
                        if (!isProcessingTap && uiState.tripStatus != TripStatus.CONNECTING) {
                            isProcessingTap = true
                            if (uiState.isTripActive) onEndTrip() else onStartTrip()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(GloveUiTokens.hugeTouchTarget),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isProcessingTap,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isTripActive) GloveUiTokens.dangerRed else GloveUiTokens.activeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isTripActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (uiState.tripStatus == TripStatus.CONNECTING) "CONECTANDO..." 
                               else if (uiState.isTripActive) "FINALIZAR VIAJE" 
                               else "INICIAR VIAJE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // PANEL THAMIS LAB (HIDDEN OVERLAY)
            if (showLabPanel) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable { showLabPanel = false }
                        .padding(32.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "THAMIS LAB - AGENT DEBATE",
                            color = GloveUiTokens.activeGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        val history = CollaborationBus.getHistory()
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            history.forEach { msg ->
                                Text(
                                    text = msg,
                                    color = Color.LightGray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { com.uriel.logpose.thamis.lab.simulation.ThamisLabSimulator.simulateRemoteAiQuery() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
                        ) {
                            Text("PROBAR IA REMOTA (PC)")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { com.uriel.logpose.thamis.lab.simulation.ThamisLabSimulator.simulateSystemDegradation() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SIMULAR DEGRADACIÓN")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { showLabPanel = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("CERRAR")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(icon: ImageVector, label: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(GloveUiTokens.statusIconSize))
        Spacer(Modifier.width(8.dp))
        Text(label, color = tint, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
