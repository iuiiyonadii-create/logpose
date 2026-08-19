package com.uriel.logpose.core.parser.multicommand

enum class SeparatorType(

    val value: String

) {

    AND(" y "),

    THEN(" despues "),

    THEN_ACCENT(" después "),

    NEXT(" luego "),

    COMMA(","),

    SEMICOLON(";")
}