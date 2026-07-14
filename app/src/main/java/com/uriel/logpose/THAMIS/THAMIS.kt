package com.uriel.logpose.THAMIS

import com.uriel.logpose.THAMIS.decision.Decision
import com.uriel.logpose.THAMIS.intent.IntentDetector
import com.uriel.logpose.THAMIS.request.THAMISRequest

/**
 * Núcleo principal de THAMIS.
 */
object THAMIS {

    fun process(request: com.uriel.logpose.THAMIS.request.THAMISRequest): com.uriel.logpose.THAMIS.decision.Decision {

        val intent = _root_ide_package_.com.uriel.logpose.THAMIS.intent.IntentDetector.detect(request.text)

        return _root_ide_package_.com.uriel.logpose.THAMIS.decision.Decision(

            intent = intent,

            confidence = request.speechConfidence

        )

    }

}