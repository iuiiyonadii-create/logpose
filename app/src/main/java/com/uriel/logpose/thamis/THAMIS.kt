package com.uriel.logpose.thamis

import com.uriel.logpose.thamis.decision.Decision
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.request.THAMISRequest

/**
 * Núcleo principal de THAMIS.
 */
object THAMIS {

    fun process(request: THAMISRequest): Decision {

        val intent = IntentDetector.detect(request.text)

        return Decision(
            intent = intent,
            confidence = request.speechConfidence
        )
    }
}