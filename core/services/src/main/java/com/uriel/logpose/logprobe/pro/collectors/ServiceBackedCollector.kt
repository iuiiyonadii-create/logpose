package com.uriel.logpose.logprobe.collectors

/**
 * Contrato para collectors respaldados por un Service del sistema que
 * empuja eventos de forma asincrona (AccessibilityService,
 * NotificationListenerService). Estos collectors NO hacen polling:
 * reaccionan a callbacks del SO y los reenvian, siempre y cuando
 * ProbeGuard los apruebe.
 *
 * No implementan una interfaz Collector generica junto a los de
 * polling: el ciclo de vida es distinto (atado al binding del Service,
 * no a un scope de coroutine propio) y forzarlos a una interfaz comun
 * solo generaria metodos vacios.
 */
interface ServiceBackedCollector {

    /** Se llama cuando el Service correspondiente se conecta al sistema. */
    fun onServiceConnected()

    /** Se llama cuando el Service correspondiente se desconecta. */
    fun onServiceDisconnected()

    /** true si el collector esta actualmente recibiendo callbacks utiles. */
    fun isActive(): Boolean
}
