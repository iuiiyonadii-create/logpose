package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withContext



class BluetoothConnectionManager(
    context: Context
) {



    private val appContext =
        context.applicationContext



    private var connectedDevice: BluetoothDevice? =
        null



    private var a2dpConnected =
        false



    private var headsetConnected =
        false







    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return@withContext false
        connectedDevice = device

        LogPoseLogger.d("LOGPOSE_BT", "Verificando estado de conexión para ${device.name}")

        val a2dpDeferred = CompletableDeferred<Boolean>()
        val headsetDeferred = CompletableDeferred<Boolean>()

        val a2dpListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                val isConnected = proxy.connectedDevices.any { it.address == device.address }
                a2dpConnected = isConnected
                LogPoseLogger.d("LOGPOSE_BT", "A2DP conectado: $isConnected")
                // v82.0: Cerramos el proxy SIEMPRE para evitar fugas, incluso si llegamos tarde
                adapter.closeProfileProxy(BluetoothProfile.A2DP, proxy)
                if (!a2dpDeferred.isCompleted) a2dpDeferred.complete(isConnected)
            }

            override fun onServiceDisconnected(profile: Int) {
                a2dpConnected = false
                if (!a2dpDeferred.isCompleted) a2dpDeferred.complete(false)
            }
        }

        val headsetListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                val isConnected = proxy.connectedDevices.any { it.address == device.address }
                headsetConnected = isConnected
                LogPoseLogger.d("LOGPOSE_BT", "HEADSET conectado: $isConnected")
                // v82.0: Cerramos el proxy SIEMPRE para evitar fugas
                adapter.closeProfileProxy(BluetoothProfile.HEADSET, proxy)
                if (!headsetDeferred.isCompleted) headsetDeferred.complete(isConnected)
            }

            override fun onServiceDisconnected(profile: Int) {
                headsetConnected = false
                if (!headsetDeferred.isCompleted) headsetDeferred.complete(false)
            }
        }

        adapter.getProfileProxy(appContext, a2dpListener, BluetoothProfile.A2DP)
        adapter.getProfileProxy(appContext, headsetListener, BluetoothProfile.HEADSET)

        try {
            withTimeout(3000) {
                a2dpDeferred.await()
                headsetDeferred.await()
            }
        } catch (e: Exception) {
            LogPoseLogger.w("LOGPOSE_BT", "Timeout esperando perfiles BT: ${e.message}")
        }

        val result = a2dpConnected || headsetConnected
        LogPoseLogger.i("LOGPOSE_BT", "Resultado final de verificación: $result")
        result
    }









    fun disconnect(){



        connectedDevice =
            null



        a2dpConnected =
            false



        headsetConnected =
            false



    }









    @Suppress("MissingPermission")
    fun isConnected(): Boolean {
        // Actualizamos estado antes de responder (Fallback para Redmi/Xiaomi)
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        
        return try {
            val a2dpState = adapter.getProfileConnectionState(BluetoothProfile.A2DP)
            val hfpState = adapter.getProfileConnectionState(BluetoothProfile.HEADSET)
            
            val systemConnected = a2dpState == BluetoothProfile.STATE_CONNECTED || 
                                 hfpState == BluetoothProfile.STATE_CONNECTED

            systemConnected || a2dpConnected || headsetConnected
        } catch (e: Exception) {
            a2dpConnected || headsetConnected
        }
    }



}