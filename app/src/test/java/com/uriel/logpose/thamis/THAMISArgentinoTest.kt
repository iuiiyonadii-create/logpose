package com.uriel.logpose.thamis

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.action.ActionMapper
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.request.THAMISRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class THAMISArgentinoTest {

    @Test
    fun `test mandale una cumbia`() {
        val text = "Che LogPose, mandale una cumbia"
        val request = THAMISRequest(text = text)
        
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.PlayMusic)
        assertEquals("cumbia", (command as LogPoseCommand.PlayMusic).query)
    }

    @Test
    fun `test encara para el centro`() {
        val text = "LogPose encara para el centro"
        val request = THAMISRequest(text = text)
        
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.NAVIGATE, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.Navigate)
        assertEquals("el centro", (command as LogPoseCommand.Navigate).destination)
    }
    
    @Test
    fun `test llamame a la vieja`() {
        val text = "Che llamame a la vieja"
        val request = THAMISRequest(text = text)
        
        val decision = THAMIS.process(request)
        
        assertEquals(Intent.CALL_CONTACT, decision.intent)
        
        val command = ActionMapper.map(decision, text)
        assertTrue(command is LogPoseCommand.Call)
        assertEquals("la vieja", (command as LogPoseCommand.Call).contact)
    }
}