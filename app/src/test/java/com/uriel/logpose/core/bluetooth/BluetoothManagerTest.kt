package com.uriel.logpose.core.bluetooth

import android.content.Context
import com.uriel.logpose.core.domain.ConnectionState
import com.uriel.logpose.features.bluetooth.BluetoothManager
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BluetoothManagerTest {

    private val context = mockk<Context>(relaxed = true)
    private lateinit var bluetoothManager: BluetoothManager

    @Before
    fun setup() {
        bluetoothManager = BluetoothManager(context)
    }

    @Test
    fun `initial state is DISCONNECTED`() {
        assertEquals(ConnectionState.DISCONNECTED, bluetoothManager.connectionState.value)
    }

    @Test
    fun `disconnect updates state to DISCONNECTED`() {
        bluetoothManager.disconnect()
        assertEquals(ConnectionState.DISCONNECTED, bluetoothManager.connectionState.value)
    }
}
