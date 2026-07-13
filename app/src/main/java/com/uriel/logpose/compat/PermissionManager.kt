package com.uriel.logpose.compat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

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

        return bluetoothPermissions() +
                microphonePermissions()

    }

    fun hasBluetoothPermission(
        context: Context
    ): Boolean {

        return bluetoothPermissions().all {

            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED

        }

    }

    fun hasMicrophonePermission(
        context: Context
    ): Boolean {

        return microphonePermissions().all {

            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED

        }

    }

}