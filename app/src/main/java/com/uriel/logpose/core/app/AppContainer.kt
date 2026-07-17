package com.uriel.logpose

import android.content.Context
import com.uriel.logpose.data.preferences.SettingsPreferences
import com.uriel.logpose.features.bluetooth.BluetoothRepository
import com.uriel.logpose.features.bluetooth.BluetoothSessionManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.logcore.providers.DefaultProviderRegistry
import com.uriel.logpose.logcore.providers.ProviderModule
import com.uriel.logpose.logcore.providers.ProviderRegistry

object AppContainer {

    private var initialized = false

    lateinit var bluetoothRepository: BluetoothRepository
        private set

    lateinit var bluetoothSessionManager: BluetoothSessionManager
        private set

    lateinit var settingsManager: SettingsManager
        private set

    val providerRegistry: ProviderRegistry = DefaultProviderRegistry()

    private val providerModules: List<ProviderModule> = emptyList()

    fun initialize(context: Context) {

        if (initialized) return

        bluetoothRepository =
            BluetoothRepository(context.applicationContext)

        bluetoothSessionManager =
            BluetoothSessionManager(bluetoothRepository)

        settingsManager =
            SettingsManager(
                SettingsPreferences(context.applicationContext)
            )

        settingsManager.start()

        providerModules.forEach { module ->
            module.registerInto(providerRegistry)
        }

        initialized = true
    }
}