package com.uriel.logpose.core.compat

import android.Manifest

object BluetoothPermissions {

    fun permissions(): Array<String> {

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

}