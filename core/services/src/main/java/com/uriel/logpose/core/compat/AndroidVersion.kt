package com.uriel.logpose.core.compat

import android.os.Build

object AndroidVersion {

    fun isAndroid9OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    fun isAndroid12OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    fun isAndroid13OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun isAndroid14OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    fun isAndroid15OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= 35
    }

}