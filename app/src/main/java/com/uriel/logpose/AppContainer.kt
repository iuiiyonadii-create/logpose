package com.uriel.logpose

import android.content.Context
import com.uriel.logpose.bluetooth.BluetoothRepository

object AppContainer {

    private var initialized = false

    lateinit var bluetoothRepository: BluetoothRepository
        private set

    fun initialize(context: Context) {

        if (initialized) return

        bluetoothRepository =
            BluetoothRepository(context.applicationContext)

        initialized = true
    }
}