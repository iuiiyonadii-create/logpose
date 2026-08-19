package com.uriel.logpose.thamis.actuator

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.communication.provider.CommunicationProviderFactory
import com.uriel.logpose.thamis.communication.provider.CommunicationResult
import com.uriel.logpose.thamis.communication.provider.CommunicationProviderType
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Actuador para el dominio de COMUNICACIÓN.
 */
class CommunicationActuator : CognitiveActionExecutor {

    override fun execute(decision: ThamisDecision) {
        val contact = decision.winningEvaluation?.hypothesis?.entities?.get("contact") ?: "Unknown"
        val message = decision.winningEvaluation?.hypothesis?.entities?.get("message") ?: ""
        
        // Decidir proveedor (Por ahora default, luego basado en entidades "via whatsapp")
        val provider = CommunicationProviderFactory.getDefaultProvider()

        val result: CommunicationResult = when (decision.intent) {
            Intent.CALL_CONTACT -> provider.callContact(contact)
            Intent.ANSWER_CALL -> provider.answerCall()
            Intent.REJECT_CALL -> provider.rejectCall()
            Intent.SEND_MESSAGE -> provider.sendMessage(contact, message)
            else -> CommunicationResult(false, CommunicationProviderType.NONE, "Intent not supported")
        }

        LogPoseLogger.i("THAMIS_ACTUATOR: Comm Provider=${result.provider} Success=${result.success} Action=${decision.intent}")
    }
}
