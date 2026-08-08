package com.thamis.lab.core.contracts.intent

/**
 * THAMIS Unified Intent: The common language between App and Labs.
 */
public enum class Intent {
    UNKNOWN,

    // Music & Media
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    REPEAT_MUSIC,
    SET_VOLUME,

    // Communication
    CALL_CONTACT,
    ANSWER_CALL,
    REJECT_CALL,
    SEND_MESSAGE,
    REPLY_MESSAGE,
    MESSAGE_CONTENT,

    // Navigation
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

    // System & Control
    OPEN_APP,
    READ_NOTIFICATION,
    CONFIRM_ACTION,
    CANCEL_ACTION,
    YIELD_CONTROL,
    SWITCH_TAB,

    // Smart Sensing & Safety
    WEATHER,
    EMERGENCY,
    SAFETY_ALERT,
    TRAFFIC_STATUS,
    RESTAURANT_SEARCH,
    TRANSPORT_INFO,
    RECORD_INCIDENT,
    TOGGLE_HUD,

    // Vehicle Diagnostics
    VEHICLE_STATUS,
    FUEL_LEVEL,
    MAINTENANCE_INFO,
    ENGINE_TEMP,

    // Extended Knowledge
    ASK_LEGAL,
    ASK_PLAY_STORE,
    SOCIAL_SEARCH,
    SOCIAL_CAMERA,

    // Roadmap / IoT
    SMART_TV_CONTROL,
    IOT_CONTROL
}
