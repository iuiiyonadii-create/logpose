package com.uriel.logpose.core.parser.multicommand

data class MultiCommand(

    val commands: List<String>

) {

    val size: Int
        get() = commands.size

    fun isEmpty(): Boolean =
        commands.isEmpty()

    fun isNotEmpty(): Boolean =
        commands.isNotEmpty()
}