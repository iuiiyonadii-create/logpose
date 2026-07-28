package com.uriel.logpose.core.bluetooth

import android.content.Context
import com.uriel.logpose.data.bluetooth.BluetoothRepositoryImpl
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.domain.models.DeviceType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BluetoothRepositoryTest {

    private val context = mockk<Context>(relaxed = true)
    private lateinit var repository: BluetoothRepositoryImpl

    @Before
    fun setup() {
        repository = BluetoothRepositoryImpl(context)
    }

    @Test
    fun `saveSelectedDevice updates preference`() {
        // Test placeholder - currently repository only calls preferences
        repository.saveSelectedDevice("00:11:22:33:44:55")
        assertEquals("00:11:22:33:44:55", repository.getSelectedDeviceMac())
    }
}
