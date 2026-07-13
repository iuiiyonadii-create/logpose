package com.uriel.logpose.compat

import android.Manifest

object MicrophonePermissions {

    fun permissions(): Array<String> {

        return arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

    }

}