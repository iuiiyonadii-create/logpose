package com.uriel.logpose.thamis.intelligence

import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.decision.Decision
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * ThamisBrain: El motor de inteligencia híbrido.
 * Combina reglas locales (Gratis/Rápido) con LLMs remotos (Gratis/Inteligente).
 */
object ThamisBrain {

    /**
     * Procesa una petición intentando primero el motor local.
     * Si la confianza es baja, consulta al LLM en la PC o en la Nube.
     */
    suspend fun process(request: THAMISRequest): Decision = withContext(Dispatchers.Default) {
        // 1. Motor Local (Rule-based)
        val localDetection = IntentDetector.detect(request.text)
        
        if (localDetection.score > 0.85f) {
            LogPoseLogger.d("ThamisBrain: Usando motor local (Conf: ${localDetection.score})")
            return@withContext Decision(
                intent = localDetection.intent,
                confidence = localDetection.score
            )
        }

        // 2. Si el motor local duda, usamos el "Cerebro Remoto" (Gratis vía PC o API)
        LogPoseLogger.i("ThamisBrain: Confianza baja (${localDetection.score}). Consultando IA Avanzada...")
        
        val remoteDecision = queryRemoteLLM(request.text)
        return@withContext remoteDecision ?: Decision(
            intent = localDetection.intent,
            confidence = localDetection.score
        )
    }

    /**
     * Consulta al servidor proxy de la PC (como free-claude-code) o una API gratuita.
     */
    private suspend fun queryRemoteLLM(text: String): Decision? = withContext(Dispatchers.IO) {
        // OPCIÓN A: Usar la PC del usuario (Gratis total, requiere PC encendida)
        val pcIp = AppContainer.settingsManager.getString("pc_ip", "192.168.1.33")
        val useLocalPC = AppContainer.settingsManager.getBoolean("use_local_ai", true)

        if (useLocalPC) {
            val decision = queryLocalPCProxy(text, pcIp ?: "192.168.1.33")
            if (decision != null) return@withContext decision
        }

        // OPCIÓN B: Usar Gemini API (Gratis hasta 15 RPM, requiere Internet)
        // Esto es lo que el usuario puede usar sin configurar nada en la PC
        return@withContext queryGeminiFree(text)
    }

    private suspend fun queryLocalPCProxy(text: String, ip: String): Decision? = withContext(Dispatchers.IO) {
        val urlString = "http://$ip:5000/process" // Puerto del proxy del repo de GitHub
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 2000

            val jsonInput = JSONObject().apply {
                put("prompt", text)
            }

            conn.outputStream.use { it.write(jsonInput.toString().toByteArray()) }

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                return@withContext Decision(
                    intent = mapStringToIntent(jsonResponse.optString("intent", "UNKNOWN")),
                    confidence = 0.95f
                )
            }
        } catch (e: Exception) {
            LogPoseLogger.w("ThamisBrain: PC Proxy no disponible.")
        }
        return@withContext null
    }

    private suspend fun queryGeminiFree(text: String): Decision? = withContext(Dispatchers.IO) {
        // En un caso real, aquí usaríamos el API Key del usuario o una genérica de desarrollo
        // Para este MVP, simulamos el éxito si hay Internet
        LogPoseLogger.i("ThamisBrain: Consultando Cerebro en la Nube (Gemini Free Tier)...")
        
        // Simulación de respuesta inteligente
        delay(1000) 
        return@withContext null // Fallback al motor local si falla la nube
    }

    private fun mapStringToIntent(name: String): Intent = try {
        Intent.valueOf(name.uppercase())
    } catch (e: Exception) {
        Intent.UNKNOWN
    }
}
