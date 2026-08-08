package com.uriel.logpose.features.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.core.compat.core.AppState

object CallManager {

    private lateinit var context: Context
    private var previousState: AppState = AppState.READY

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    fun onIncomingCall(phoneNumber: String?) {
        if (phoneNumber == null || phoneNumber == "null") {
            LogPoseLogger.d("CallManager: Ignorando llamada fantasma o sin número.")
            return
        }
        LogPoseLogger.i("Llamada entrante detectada: $phoneNumber")
        
        val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context)
        
        // 1. Guardar estado anterior
        previousState = core.getState()
        
        // 2. Cambiar a estado de llamada entrante
        core.setIncomingCall()

        // 3. Bajar música progresivamente (Sector 4.1)
        MusicManager.pause() // Por ahora pausamos, luego implementaremos fade out parcial

        // 4. Informar llamada (Sector 4.1)
        FeedbackManager.speak("Número desconocido está llamando.")
    }

    fun onCallAnswered() {
        LogPoseLogger.i("Llamada atendida")
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context).setCallActive()
    }

    fun onCallEnded() {
        LogPoseLogger.i("Llamada finalizada")
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context).setCallEnding()
        
        // Recuperar estado anterior (Sector 4.6)
        restorePreviousState()
    }

    private fun restorePreviousState() {
        LogPoseLogger.i("Restaurando estado previo: $previousState")
        val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context)
        when (previousState) {
            AppState.READY -> core.ready()
            AppState.STOPPED -> core.shutdown()
            else -> core.ready()
        }
        
        // Si había música, retomamos (Sector 4.6)
        MusicManager.play("") 
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun makeCall(contactName: String) {
        LogPoseLogger.i("CallManager: Solicitud de llamada para: $contactName")
        
        // 1. Resolver contacto usando el motor de inteligencia
        val resolution = com.uriel.logpose.thamis.communication.resolver.ContactResolver.resolve(contactName)
        
        if (resolution.resolvedContact != null) {
            val contact = resolution.resolvedContact
            LogPoseLogger.i("CallManager: Contacto resuelto: ${contact.name} (${contact.phoneNumber})")
            executeCall(contact.phoneNumber)
            
            // 2. Aprender del éxito
            com.uriel.logpose.thamis.learning.LearningEngine.registerUsage(com.thamis.lab.core.contracts.intent.Intent.CALL_CONTACT)
        } else if (resolution.isAmbiguous) {
            val names = resolution.candidates.take(3).joinToString(" o ") { it.name }
            FeedbackManager.speak("¿Querés llamar a $names?")
        } else {
            // Intento desesperado: ver si el nombre tiene números
            val number = contactName.filter { it.isDigit() }
            if (number.isNotBlank()) {
                executeCall(number)
            } else {
                FeedbackManager.speak("No encontré a $contactName en tus contactos.")
            }
        }
    }

    private fun executeCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        try {
            LogPoseLogger.i("Llamada: Lanzando vía TRAMPOLÍN")
            val trampolineIntent = Intent(context, com.uriel.logpose.core.app.TrampolineActivity::class.java).apply {
                putExtra("TARGET_INTENT", intent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(trampolineIntent)
        } catch (e: Exception) {
            LogPoseLogger.e("Error al iniciar llamada vía Trampolín: ${e.message}")
            context.startActivity(intent)
        }
    }

    fun acceptCall() {
        LogPoseLogger.i("Aceptando llamada via LogPose")
        // En Android moderno, aceptar una llamada programáticamente requiere permisos especiales 
        // o usar TelecomManager. Por ahora simulamos la intención.
    }

    fun rejectCall() {
        LogPoseLogger.i("Rechazando llamada via LogPose")
    }
}
