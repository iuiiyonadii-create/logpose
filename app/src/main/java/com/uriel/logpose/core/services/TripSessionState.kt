package com.uriel.logpose.core.services

enum class TripSessionState {
    IDLE,           // App abierta, sin viaje activo
    STARTING,       // Intentando abrir la 'llamada' de intercom
    ACTIVE,         // SCO abierto y anclado, listo para hablar
    RECONNECTING,   // Casco desconectado momentáneamente
    ENDING          // Cerrando sesión
}
