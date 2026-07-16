package com.uriel.logpose

import android.content.Context
import com.uriel.logpose.features.bluetooth.BluetoothRepository
import com.uriel.logpose.features.bluetooth.BluetoothSessionManager
import com.uriel.logpose.logcore.providers.DefaultProviderRegistry
import com.uriel.logpose.logcore.providers.ProviderModule
import com.uriel.logpose.logcore.providers.ProviderRegistry

object AppContainer {

    private var initialized = false

    lateinit var bluetoothRepository: BluetoothRepository
        private set

    lateinit var bluetoothSessionManager: BluetoothSessionManager
        private set

    /**
     * Process-wide provider registry. Feature modules register their
     * providers into this via a [ProviderModule] — see
     * com.uriel.logpose.logcore.providers and
     * logcore/providers/INTEGRATION.md.
     */
    val providerRegistry: ProviderRegistry = DefaultProviderRegistry()

    /**
     * Feature modules that contribute providers. Empty today: no feature
     * has migrated to the provider pattern yet (Bluetooth, Voice, Music,
     * and Automation are all Pending — see
     * engineering/00_Project/PROJECT_STATE.md). Add entries here as
     * features adopt logcore/providers; logcore itself never changes.
     */
    private val providerModules: List<ProviderModule> = emptyList()

    fun initialize(context: Context) {

        if (initialized) return

        bluetoothRepository =
            BluetoothRepository(context.applicationContext)

        bluetoothSessionManager =
            BluetoothSessionManager(bluetoothRepository)

        providerModules.forEach { module ->
            module.registerInto(providerRegistry)
        }

        initialized = true
    }
}