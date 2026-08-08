package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.Action
import com.uriel.logpose.thamis.context.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class SafetyDecisionStressTest {

    @Before
    fun setup() {
        ContextEngine.reset()
    }

    private fun injectSpeed(speed: Float) {
        val snapshot = ContextSnapshot(
            speedKmh = speed,
            gpsActive = true,
            isPlayingMusic = false,
            isPhoneCallActive = false,
            bluetoothConnected = true,
            currentFocus = FocusDomain.NONE,
            conversationState = ConversationState.IDLE,
            batteryLevel = 100,
            noiseLevelDb = 40f,
            lastIntent = null,
            activeDomain = null
        )
        ContextEngine.takeSnapshot(snapshot)
    }

    @Test
    fun `Ley 0 - Bloqueo absoluto a mas de 130 kmh`() {
        injectSpeed(140f)
        
        // Intentar llamar
        val action = DecisionEngine.evaluate(LogPoseCommand.Call(""))
        
        assertTrue("Debería ser una respuesta de voz", action is Action.VoiceResponse)
        val response = action as Action.VoiceResponse
        assertTrue("Debería mencionar la seguridad", response.message.contains("seguro"))
    }

    @Test
    fun `Ley 0 - Permitir detener sistema incluso a alta velocidad`() {
        injectSpeed(150f)
        
        val action = DecisionEngine.evaluate(LogPoseCommand.StopListening)
        
        assertEquals(Action.StopService, action)
    }

    @Test
    fun `Restriccion Notificaciones - Bloqueo a 100 kmh`() {
        injectSpeed(100f)
        
        val action = DecisionEngine.evaluate(LogPoseCommand.ReadNotifications)
        
        assertTrue(action is Action.VoiceResponse)
        val response = action as Action.VoiceResponse
        assertTrue(response.message.contains("seguro"))
    }

    @Test
    fun `Restriccion Notificaciones - Permitir a 40 kmh`() {
        injectSpeed(40f)
        
        val action = DecisionEngine.evaluate(LogPoseCommand.ReadNotifications)
        
        assertTrue("A 40kmh debería permitir leer notificaciones", action is Action.NotificationAction)
    }

    @Test
    fun `Llamada - Permitir solo si no esta conduciendo`() {
        injectSpeed(0f)
        ActivityDetector.detectActivity(0)
        
        val action = DecisionEngine.evaluate(LogPoseCommand.Call(""))
        assertTrue(action is Action.CallAction)
    }

    @Test
    fun `Integracion Contexto - Detectar Accion Pendiente`() {
        injectSpeed(20f)
        val pending = PendingAction(
            domain = FocusDomain.NAVIGATION,
            actionType = "CONFIRMATION",
            payload = "ChangeRoute"
        )
        ContextEngine.setPendingAction(pending)
        
        // Al evaluar cualquier comando, el log debería mostrar que detectó la acción pendiente
        // (En este test solo validamos que no rompa y que el flujo siga)
        val action = DecisionEngine.evaluate(LogPoseCommand.PlayMusic(""))
        assertTrue(action is Action.MediaAction)
    }
}
