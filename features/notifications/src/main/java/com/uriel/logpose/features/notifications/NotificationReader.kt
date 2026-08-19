package com.uriel.logpose.features.notifications

import android.content.Context
import android.service.notification.StatusBarNotification
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.FeedbackDelegator
import com.uriel.logpose.core.services.MediaDuckDelegator

/**
 * NotificationReader: El cerebro de Thamis para procesar mensajes entrantes.
 */
object NotificationReader {

    private var lastMessageId: String? = null
    private var lastSenderName: String? = null

    private val recentMessages = mutableListOf<String>()

    fun getLastSender(): String? = lastSenderName

    fun processNotification(context: Context, sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        
        // Filtro de Apps Críticas (WhatsApp & Instagram)
        when (packageName) {
            "com.whatsapp" -> handleWhatsApp(sbn)
            "com.instagram.android" -> handleInstagram(sbn)
        }
    }

    private fun handleInstagram(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: "Alguien"
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        
        if (text.isBlank() || text.contains("solicitud de mensaje")) return
        
        val msgId = "ig_$title$text"
        if (msgId == lastMessageId) return
        lastMessageId = msgId
        lastSenderName = title

        LogPoseLogger.i("📸 Thamis detectó Instagram de: $title")
        
        val phrase = "Che, tenés un mensaje directo en Instagram de $title. Dice: $text"

        synchronized(recentMessages) {
            recentMessages.add(0, phrase)
            if (recentMessages.size > 5) recentMessages.removeAt(5)
        }

        MediaDuckDelegator.duck()
        FeedbackDelegator.speak(phrase) {
            MediaDuckDelegator.unduck()
        }
    }

    private fun handleWhatsApp(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: "Alguien"
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        
        // Evitamos basura: "Buscando mensajes", "X mensajes de Y chats", o duplicados
        if (text.isBlank() || text.contains("mensajes nuevos") || text.contains("WhatsApp Web")) return
        
        val msgId = title + text
        if (msgId == lastMessageId) return
        lastMessageId = msgId
        val cleanSender = title.substringBefore(" (") // Limpia nombres de grupos
        lastSenderName = cleanSender

        LogPoseLogger.i("📩 Thamis detectó WhatsApp de: $title")
        
        val phrase = if (title.contains("@")) {
            "Che, mandaron algo al grupo $cleanSender. Dice: $text"
        } else {
            "Che, tenés un mensaje de $cleanSender. Dice: $text"
        }

        // Guardamos para lectura bajo demanda
        synchronized(recentMessages) {
            recentMessages.add(0, phrase)
            if (recentMessages.size > 5) recentMessages.removeAt(5)
        }

        // Ejecutamos con Ducking
        MediaDuckDelegator.duck()
        FeedbackDelegator.speak(phrase) {
            // Callback: Cuando Thamis termina de hablar, subimos la música
            MediaDuckDelegator.unduck()
        }
    }

    fun announce(item: NotificationItem) {
        val title = item.content.title ?: "Alguien"
        val text = item.content.previewText ?: item.content.text ?: ""
        if (text.isBlank()) return
        
        val phrase = "Tenés un aviso de $title. Dice: $text"
        
        MediaDuckDelegator.duck()
        FeedbackDelegator.speak(phrase) {
            MediaDuckDelegator.unduck()
        }
    }

    fun readFull() {
        val messages = synchronized(recentMessages) { recentMessages.toList() }
        
        if (messages.isEmpty()) {
            FeedbackDelegator.speak("No hay nada nuevo por acá, seguí tranquilo.")
        } else {
            val total = messages.size
            val header = if (total == 1) "Tenés un solo mensaje pendiente. " else "Tenés $total mensajes. "
            
            val fullText = header + messages.joinToString(". ")
            
            MediaDuckDelegator.duck()
            FeedbackDelegator.speak(fullText) {
                MediaDuckDelegator.unduck()
            }
        }
    }
}
