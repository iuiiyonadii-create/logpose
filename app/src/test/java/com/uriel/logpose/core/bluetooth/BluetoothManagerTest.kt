package com.uriel.logpose.core.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context
import com.uriel.logpose.core.permissions.BluetoothPermissionManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BluetoothManagerTest {

    private val context = mockk<Context>(relaxed = true)
    private val androidBtManager = mockk<BluetoothManager>(relaxed = true)
    private lateinit var bluetoothManager: com.uriel.logpose.core.bluetooth.BluetoothManager

    @Before
    fun setup() {
        every { context.getSystemService(Context.BLUETOOTH_SERVICE) } returns androidBtManager
        bluetoothManager = com.uriel.logpose.core.bluetooth.BluetoothManager(context)
    }

    @Test
    fun `initial state is IDLE`() {
        assertEquals(BluetoothState.IDLE, bluetoothManager.connectionState.value)
    }

    @Test
    fun `updateState updates connectionState flow`() {
        bluetoothManager.updateState(BluetoothState.CONNECTED)
        assertEquals(BluetoothState.CONNECTED, bluetoothManager.connectionState.value)
    }

    @Test
    fun `onBluetoothStateChanged updates isEnabled flow`() {
        bluetoothManager.onBluetoothStateChanged(true)
        assertEquals(true, bluetoothManager.isEnabled.value)
        
        bluetoothManager.onBluetoothStateChanged(false)
        assertEquals(false, bluetoothManager.isEnabled.value)
    }
}
