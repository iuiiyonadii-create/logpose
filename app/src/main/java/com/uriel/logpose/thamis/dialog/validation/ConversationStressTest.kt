package com.uriel.logpose.thamis.dialog.validation

import com.uriel.logpose.thamis.dialog.context.DialogContext
import com.uriel.logpose.thamis.dialog.model.PendingQuestion
import com.uriel.logpose.thamis.dialog.resolver.ConversationResolver

/**
 * Suite de pruebas para el motor conversacional.
 */
class ConversationStressTest {

    fun run() {
        // Escenario 1: Confirmación directa
        DialogContext.setPendingQuestion(PendingQuestion(text = "¿Llamar a Juan?", expectedDomain = "CALL"))
        ConversationResolver.resolve("Sí")

        // Escenario 2: Cancelación
        DialogContext.setPendingQuestion(PendingQuestion(text = "¿Poner música?", expectedDomain = "MUSIC"))
        ConversationResolver.resolve("Cancelá")

        // Escenario 3: Selección de entidad
        DialogContext.setPendingQuestion(PendingQuestion(text = "¿Juan Pérez o Juan Gómez?", expectedDomain = "CALL"))
        ConversationResolver.resolve("Pérez")
    }
}
