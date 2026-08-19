package com.uriel.logpose.thamis.lab.events

import com.uriel.logpose.thamis.lab.model.RealWorldEvent

/**
 * Generador de eventos realistas para poblar escenarios de simulación.
 */
object RealWorldEventGenerator {

    fun createIncomingCall(name: String = "Juan", number: String = "123456"): RealWorldEvent.IncomingCall {
        return RealWorldEvent.IncomingCall(name, number)
    }

    fun createMessage(sender: String = "Maria", content: String = "Hola, ¿cómo va?", isPriority: Boolean = false): RealWorldEvent.NewMessage {
        return RealWorldEvent.NewMessage(sender, content, isPriority)
    }

    fun createWeatherUpdate(condition: String = "Rainy", temp: Float = 18.5f): RealWorldEvent.WeatherChange {
        return RealWorldEvent.WeatherChange(condition, temp)
    }

    fun createNavigationTurn(instruction: String = "Doble a la derecha", distance: Int = 300): RealWorldEvent.NavigationUpdate {
        return RealWorldEvent.NavigationUpdate(instruction, distance)
    }

    fun createBluetoothDisconnect(deviceName: String = "Cardo Freecom 4+"): RealWorldEvent.BluetoothEvent {
        return RealWorldEvent.BluetoothEvent("DISCONNECTED", deviceName)
    }
}
