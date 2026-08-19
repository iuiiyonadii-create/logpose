package com.uriel.logpose.core.compat

import android.Manifest

object MicrophonePermissions {

    fun permissions(): Array<String> {

        return arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

    }

}