package com.uriel.logpose

import android.content.Context
import com.uriel.logpose.data.preferences.SettingsPreferences
import com.uriel.logpose.features.bluetooth.BluetoothRepository
import com.uriel.logpose.features.bluetooth.BluetoothSessionManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.voice.VoiceRepository
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


    lateinit var voiceRepository: VoiceRepository
        private set



    val providerRegistry: ProviderRegistry =
        DefaultProviderRegistry()



    private val providerModules: List<ProviderModule> =
        emptyList()



    fun initialize(
        context: Context
    ) {


        if (initialized) return



        val appContext =
            context.applicationContext



        bluetoothRepository =
            BluetoothRepository(
                appContext
            )



        bluetoothSessionManager =
            BluetoothSessionManager(
                bluetoothRepository
            )



        settingsManager =
            SettingsManager(
                SettingsPreferences(
                    appContext
                )
            )



        settingsManager.start()



        voiceRepository =
            VoiceRepository()



        voiceRepository.initialize()



        providerModules.forEach { module ->

            module.registerInto(
                providerRegistry
            )

        }



        initialized = true

    }

}