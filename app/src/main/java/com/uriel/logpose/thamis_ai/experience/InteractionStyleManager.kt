package com.uriel.logpose.thamis_ai.experience

/**
 * Manages how THAMIS interacts with the user (verbosity, speed, etc).
 */
class InteractionStyleManager {
    private var currentStyle = ResponseStyle.NORMAL

    fun updateStyle(style: ResponseStyle) {
        currentStyle = style
    }

    fun getStyle() = currentStyle
}
