package com.uriel.logpose.core.telecom

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat
import com.uriel.logpose.core.services.BluetoothRoutingWatchdog
import com.uriel.logpose.core.services.LogPoseConnectionService
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LogPoseTelecom(
    private val context: Context,
    private val audioManager: AudioManager
) {
    // Exponemos un resultado explícito de la negociación SCO
    sealed class CallReadyResult {
        data class Ready(val scoDevice: AudioDeviceInfo) : CallReadyResult()
        object TimedOut : CallReadyResult()
        object ConnectionFailed : CallReadyResult()
    }

    private var watchdog: BluetoothRoutingWatchdog? = null
    private var activeConnection: android.telecom.Connection? = null
    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun setActiveConnection(connection: android.telecom.Connection) {
        this.activeConnection = connection
    }

    companion object {
        private const val ACCOUNT_ID = "LogPoseIntercom"

        fun getHandle(context: Context): PhoneAccountHandle {
            return PhoneAccountHandle(
                ComponentName(context, LogPoseConnectionService::class.java),
                ACCOUNT_ID
            )
        }

        fun register(context: Context) {
            try {
                val telecomManager = context.getSystemService(TelecomManager::class.java)
                val handle = getHandle(context)

                val account = PhoneAccount.builder(handle, "LogPose Intercom")
                    .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
                    .setHighlightColor(Color.parseColor("#FF6600"))
                    .setShortDescription("Intercomunicador LogPose")
                    .build()

                telecomManager.registerPhoneAccount(account)
            } catch (e: Exception) {
                LogPoseLogger.e("Telecom: Error al registrar PhoneAccount: ${e.message}")
            }
        }

        fun isAccountEnabled(context: Context): Boolean {
            val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return false

            return try {
                val telecomManager = context.getSystemService(TelecomManager::class.java)
                telecomManager.getPhoneAccount(getHandle(context))?.isEnabled == true
            } catch (e: SecurityException) {
                false
            }
        }
    }

    suspend fun startGhostCall(): CallReadyResult {
        val handle = getHandle(context)
        
        // --- FIX: Usamos placeCall (Saliente) en lugar de addNewIncomingCall (Entrante) ---
        // Esto evita que el sistema haga sonar el ringtone o muestre la UI de llamada entrante.
        val extras = android.os.Bundle().apply {
            putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
            putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Usamos un esquema custom "logpose" en lugar de "tel" para evitar el ringtone del V6
                val uri = Uri.fromParts("logpose", "Intercom", null)
                telecomManager.placeCall(uri, extras)
                LogPoseLogger.i("Telecom: Canal Ghost (logpose://) solicitado.")
            } catch (e: Exception) {
                LogPoseLogger.e("Telecom: Error al solicitar canal de audio: ${e.message}")
                return CallReadyResult.ConnectionFailed
            }
        } else {
            LogPoseLogger.e("Telecom: No se puede iniciar Ghost Call sin permiso CALL_PHONE.")
            return CallReadyResult.ConnectionFailed
        }

        // 2. Esperamos a que el Watchdog confirme el ruteo SCO
        return suspendCancellableCoroutine { continuation ->
            watchdog = BluetoothRoutingWatchdog(
                audioManager = audioManager,
                tag = "Session-Global",
                onRouted = { device ->
                    if (continuation.isActive) continuation.resume(CallReadyResult.Ready(device))
                },
                onFallback = {
                    if (continuation.isActive) continuation.resume(CallReadyResult.TimedOut)
                }
            )
            watchdog?.start(graceMs = 6000L)

            continuation.invokeOnCancellation { 
                watchdog?.stop() 
            }
        }
    }

    fun endGhostCall() {
        watchdog?.stop()
        watchdog = null
        
        activeConnection?.let {
            it.onDisconnect()
            LogPoseLogger.i("Telecom: Conexión activa finalizada manualmente.")
        }
        activeConnection = null

        audioManager.mode = AudioManager.MODE_NORMAL
        LogPoseLogger.i("Telecom: Solicitud de cierre de sesión.")
    }
}
