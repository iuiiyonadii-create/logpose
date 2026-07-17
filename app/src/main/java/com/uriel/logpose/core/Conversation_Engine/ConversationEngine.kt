package com.uriel.logpose.core.conversation

object ConversationEngine {

    fun process(
        text: String
    ): ConversationResult {

        if (!ConversationMemory.active()) {

            return ConversationResult(

                understood = false,

                response = ""
            )
        }

        val session =
            ConversationMemory.session()
                ?: return ConversationResult(
                    false,
                    ""
                )

        session.lastAnswer = text

        ConversationRepository.add(

            ConversationContext(

                lastResponse = text
            )
        )

        return ConversationResult(

            understood = true,

            response = text
        )
    }

    fun confirmation(
        text: String
    ): Boolean {

        return text.equals(
            "si",
            true
        ) ||
                text.equals(
                    "sí",
                    true
                ) ||
                text.equals(
                    "ok",
                    true
                )
    }

    fun cancellation(
        text: String
    ): Boolean {

        return text.equals(
            "no",
            true
        ) ||
                text.equals(
                    "cancelar",
                    true
                )
    }
}