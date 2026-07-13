package com.uriel.logpose.compat

import android.Manifest

object PermissionManager {

    fun bluetoothPermissions(): Array<String> {

        return if (AndroidVersion.isAndroid12OrHigher()) {

            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )

        } else {

            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        }

    }

    fun microphonePermissions(): Array<String> {

        return arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

    }

    fun requiredPermissions(): Array<String> {

        return bluetoothPermissions() + microphonePermissions()

    }

}