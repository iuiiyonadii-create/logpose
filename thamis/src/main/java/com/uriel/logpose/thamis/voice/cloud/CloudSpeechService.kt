package com.uriel.logpose.thamis.voice.cloud

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.LabDiscoveryDelegator
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.*
import okio.ByteString.Companion.toByteString
import org.json.JSONObject

/**
 * CloudSpeechService: Streaming de audio PCM hacia la PC Bridge (Deepgram Nova-2).
 * v1.0: Conexión dinámica vía LabDiscovery.
 */
class CloudSpeechService(private val context: Context) {

    private var webSocket: WebSocket? = null
    val resultChannel = Channel<String>(Channel.CONFLATED)
    private val client = OkHttpClient()
    @Volatile private var _isConnected = false
    private val reconnectScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reconnectAttempt = 0

    fun isConnected(): Boolean = _isConnected

    fun connect() {
        val pcIp = LabDiscoveryDelegator.getPcIp() ?: return // Mute Web Gateway si no hay PC descubierta
        val url = "ws://$pcIp:8089"
        
        LogPoseLogger.d("CloudSpeech: Intentando conectar a $url...")
        
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                LogPoseLogger.i("CloudSpeech: Conectado al Laboratorio de Audio (PC).")
                _isConnected = true
                reconnectAttempt = 0
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val transcription = json.optString("text", "")
                    if (transcription.isNotBlank()) {
                        LogPoseLogger.d("CloudSpeech: Recibido de Nube -> '$transcription'")
                        resultChannel.trySend(transcription)
                    }
                } catch (e: Exception) {
                    LogPoseLogger.e("CloudSpeech: Error parseando respuesta: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                LogPoseLogger.e("CloudSpeech: Error de conexión WebSocket: ${t.message}")
                _isConnected = false
                scheduleReconnect()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                LogPoseLogger.d("CloudSpeech: Cerrando conexión...")
                _isConnected = false
            }
        })
    }

    fun streamAudio(pcmData: ByteArray) {
        webSocket?.send(pcmData.toByteString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Cierre normal")
        webSocket = null
        _isConnected = false
        reconnectScope.coroutineContext.cancelChildren()
    }

    private fun scheduleReconnect() {
        reconnectScope.launch {
            reconnectAttempt++
            val delayMs = (2000L * reconnectAttempt).coerceAtMost(30000L)
            LogPoseLogger.i("CloudSpeech: Reconexión en ${delayMs/1000}s (intento $reconnectAttempt)...")
            delay(delayMs)
            connect()
        }
    }
}
