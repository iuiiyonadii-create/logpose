package com.uriel.logpose.core.bluetooth

import android.content.Context
import com.uriel.logpose.data.bluetooth.BluetoothRepositoryImpl
import com.uriel.logpose.features.bluetooth.BluetoothManager
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BluetoothRepositoryTest {

    private val context = mockk<Context>(relaxed = true)
    private val bluetoothManager = mockk<BluetoothManager>(relaxed = true)
    private lateinit var repository: BluetoothRepositoryImpl

    @Before
    fun setup() {
        repository = BluetoothRepositoryImpl(context, bluetoothManager)
    }

    @Test
    fun `saveSelectedDevice updates preference`() {
        repository.saveSelectedDevice("00:11:22:33:44:55")
        assertEquals("00:11:22:33:44:55", repository.getSelectedDeviceMac())
    }
}
