package com.uriel.logpose.thamis.interaction

/**
 * Prioridades Globales de THAMIS v3.3.
 * El orden es absoluto y define quién tiene derecho a interrumpir.
 */
enum class InteractionPriority(val level: Int) {
    EMERGENCY(100),    // Accidentes, SOS
    SAFETY(90),       // Alertas de velocidad, radares críticos
    NAVIGATION(80),   // Instrucciones de giro inmediatas
    ACTIVE_CALL(70),  // Comunicación en curso
    COMMUNICATION(60),// Mensajes entrantes (WhatsApp/SMS)
    NOTIFICATIONS(50),// Apps secundarias
    DEVICE(40),       // Batería, conexión Bluetooth
    MULTIMEDIA(30),   // Información de canciones, Spotify
    INFORMATION(20),  // Curiosidades, clima, estado del viaje
    LOW(10)           // Logs o info de depuración
}

enum class InteractionDecision {
    EXECUTE,  // Hablar/Actuar ahora
    WAIT,     // Esperar a que el canal esté libre
    QUEUE,    // Guardar en cola para más tarde
    IGNORE,   // Descartar (ruido o baja prioridad)
    MERGE,    // Fusionar con otros eventos similares
    POSTPONE, // Re-evaluar en X segundos
    CANCEL    // Cancelar ejecución previa
}
