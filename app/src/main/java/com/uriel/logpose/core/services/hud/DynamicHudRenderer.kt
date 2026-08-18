package com.uriel.logpose.core.services.hud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DynamicHudRenderer: The Visual Mind of LogPose.
 * v60.0: Fully programmable HUD using Jetpack Compose.
 */
@Composable
fun DynamicHudContent(
    status: String,
    debug: String? = null,
    snapshot: com.uriel.logpose.thamis.world.model.WorldSnapshot
) {
    val riskColor = when(snapshot.vehicle.riskLevel) {
        com.uriel.logpose.thamis.world.model.RiskLevel.LOW -> Color(0xFF3DDC97)
        com.uriel.logpose.thamis.world.model.RiskLevel.MEDIUM -> Color.Yellow
        com.uriel.logpose.thamis.world.model.RiskLevel.HIGH -> Color(0xFFFFA500)
        com.uriel.logpose.thamis.world.model.RiskLevel.CRITICAL -> Color.Red
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LOGPOSE SYSTEM",
                color = Color(0xFF00F2FF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                color = if (status.contains("🔴") || status.contains("⚠️")) Color.Yellow else Color(0xFF3DDC97),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            // [DYNAMIC_UI_ZONE_START]
            // Widget predictivo: Muestra la próxima acción probable según el PIE
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.Cyan, shape = RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NEXT: GPS CASA (18:00)",
                    color = Color.Cyan.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // [WIDGET_START: Motor Temp]
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.DarkGray.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🌡️",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ENGINE: ${snapshot.vehicle.engineTempCelsius}°C",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // [WIDGET_START: Cyber-Risk Sentinel]
            // v66.5: Radar de riesgo proactivo. Cambia de color según el peligro.
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(riskColor.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Anillo de pulso (Simulado)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(riskColor.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
                )
                Text(
                    text = "RISK",
                    color = riskColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = snapshot.vehicle.riskLevel.name,
                color = riskColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            // [WIDGET_END]

            // [WIDGET_START: Lean Angle Meter]
            // v66.7: Visualizador de inclinación lateral en tiempo real.
            Spacer(modifier = Modifier.height(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .background(Color.DarkGray, shape = RoundedCornerShape(2.dp))
                ) {
                    // Indicador de balanceo
                    Box(
                        modifier = Modifier
                            .offset(x = (snapshot.vehicle.leanAngle.coerceIn(-30f, 30f) * 1.0).dp)
                            .size(6.dp)
                            .background(
                                if (kotlin.math.abs(snapshot.vehicle.leanAngle) > 25) Color.Red else Color.Cyan,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .align(Alignment.Center)
                    )
                }
                Text(
                    text = "${snapshot.vehicle.leanAngle.toInt()}° LEAN",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // [WIDGET_START: G-Force Analyzer]
            // v66.8: Monitor de aceleración longitudinal/latitudinal.
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "G-FORCE",
                    color = Color.Yellow.copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "%.2f G".format(snapshot.vehicle.gForce),
                    color = if (snapshot.vehicle.gForce > 1.2f) Color.Red else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            // [WIDGET_END]

            // [WIDGET_START: Proactive AI Insight]
            // v66.8: Consejos del cerebro local (Llama 3.2) basados en el contexto.
            if (snapshot.vehicle.moving && snapshot.vehicle.riskLevel != com.uriel.logpose.thamis.world.model.RiskLevel.LOW) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF00F2FF).copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "💡 STAFF: Cuidado en la curva, asfalto frío detectado.",
                        color = Color(0xFF00F2FF),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    )
                }
            }
            // [WIDGET_END]
            
            // [DYNAMIC_UI_ZONE_END]

            debug?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🛠 $it",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}
