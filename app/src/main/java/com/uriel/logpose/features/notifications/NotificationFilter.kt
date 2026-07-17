package com.uriel.logpose.features.notifications

/**
 * Reglas de admision de Notification Core, independientes de ProbeGuard
 * (Decision 3, Sprint Notification Core). A diferencia de ProbeGuard —que
 * exige una allowlist obligatoria y no vacia—, el comportamiento por
 * defecto de Notification Core es "permitir salvo lista negra", que es lo
 * que el Sprint pide como "Lista blanca. Lista negra." como controles
 * independientes de usuario final.
 *
 * Reglas, en orden:
 * 1. Si el paquete esta en blacklist -> rechazado (la blacklist siempre gana).
 * 2. Si whitelist no esta vacia -> solo se permiten paquetes en whitelist.
 * 3. Si whitelist esta vacia -> se permite cualquier paquete no bloqueado.
 */
data class NotificationFilter(
    val whitelist: Set<String> = emptySet(),
    val blacklist: Set<String> = emptySet()
) {
    fun isAllowed(packageName: String): Boolean {
        val normalized = normalize(packageName)
        if (normalized.isEmpty()) return false

        if (blacklist.any { normalize(it) == normalized }) return false

        if (whitelist.isEmpty()) return true

        return whitelist.any { normalize(it) == normalized }
    }

    fun withWhitelist(packages: Set<String>): NotificationFilter =
        copy(whitelist = packages.map(::normalize).filter { it.isNotEmpty() }.toSet())

    fun withBlacklist(packages: Set<String>): NotificationFilter =
        copy(blacklist = packages.map(::normalize).filter { it.isNotEmpty() }.toSet())

    private fun normalize(packageName: String): String = packageName.trim().lowercase()
}
