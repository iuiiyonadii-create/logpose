package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.action.ActionMapper
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.intelligence.ThamisBrain
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class THAMISArgentinoTest {

    @Test
    fun `test mandale una cumbia`() = runBlocking {
        val text = "Che LogPose, mandale una cumbia"
        val request = THAMISRequest(text = text)
        
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.Media.PlayMusic)
        assertEquals("cumbia", (command as LogPoseCommand.Media.PlayMusic).query)
    }

    @Test
    fun `test encara para el centro`() = runBlocking {
        val text = "LogPose encara para el centro"
        val request = THAMISRequest(text = text)
        
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.NAVIGATE, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.Navigation.Navigate)
        assertEquals("el centro", (command as LogPoseCommand.Navigation.Navigate).destination)
    }
    
    @Test
    fun `test llamame a la vieja`() = runBlocking {
        val text = "Che llamame a la vieja"
        val request = THAMISRequest(text = text)
        
        val decision = ThamisBrain.process(request)
        
        assertEquals(Intent.CALL_CONTACT, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.Communication.Call)
        assertEquals("la vieja", (command as LogPoseCommand.Communication.Call).contact)
    }

    @Test
    fun `test cancela el viaje`() = runBlocking {
        val text = "LogPose cancela el viaje"
        val request = THAMISRequest(text = text)
        
        val decision = ThamisBrain.process(request)
        
        // El IntentDetector debería ahora capturar "viaje" como NAVIGATE o similar
        // Pero el ActionMapper tiene el blindaje de prioridad
        val command = ActionMapper.map(decision, text)
        assertEquals(LogPoseCommand.EndTrip, command)
    }
}
