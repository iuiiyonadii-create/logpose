package com.uriel.logpose.thamis.intent

/**
 * Intenciones comprendidas por THAMIS.
 */
enum class Intent {
    UNKNOWN,

    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,

    SET_VOLUME,

    CALL_CONTACT,
    ANSWER_CALL,
    REJECT_CALL,

    SEND_MESSAGE,

    NAVIGATE,
    START_ROUTE,
    GO_HOME,
    GO_WORK,
    GO_FAVORITE,
    CHANGE_DESTINATION,
    CANCEL_ROUTE,
    REPEAT_INSTRUCTION,
    NEXT_STEP,
    RESUME_ROUTE,
    STOP_NAVIGATION,

    WEATHER,

    EMERGENCY,

    OPEN_APP,

    READ_NOTIFICATION,

    ASK_LEGAL,
    ASK_PLAY_STORE,

    // PC / Browser
    SWITCH_TAB,
    
    // Multimedia avanzado
    REPEAT_MUSIC,

    // Domótica y Futuro (Roadmap)
    SMART_TV_CONTROL,
    IOT_CONTROL,

    // Control de Handover
    YIELD_CONTROL
}
