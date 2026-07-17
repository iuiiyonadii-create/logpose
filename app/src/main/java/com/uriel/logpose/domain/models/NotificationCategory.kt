package com.uriel.logpose.domain.models

/**
 * Categoria de producto de una notificacion dentro de Notification Core.
 * No confundir con Android NotificationChannel/NotificationCategory
 * (android.app.Notification.CATEGORY_*), que es una de las senales usadas
 * para calcular esta.
 */
enum class NotificationCategory {
    MESSAGE,
    CALL,
    EMAIL,
    SOCIAL,
    NAVIGATION,
    MEDIA,
    ALARM,
    SYSTEM,
    OTHER;

    companion object {

        private val SYSTEM_CATEGORY_MAP = mapOf(
            "msg" to MESSAGE,
            "call" to CALL,
            "email" to EMAIL,
            "social" to SOCIAL,
            "navigation" to NAVIGATION,
            "transport" to NAVIGATION,
            "alarm" to ALARM,
            "event" to ALARM,
            "reminder" to ALARM,
            "sys" to SYSTEM,
            "service" to SYSTEM,
            "status" to SYSTEM
        )

        private val MEDIA_PACKAGE_HINTS = listOf(
            "spotify", "youtube.music", "music", "soundcloud", "deezer"
        )

        private val SOCIAL_PACKAGE_HINTS = listOf(
            "whatsapp", "telegram", "instagram", "facebook", "twitter", "x.com",
            "tiktok", "discord", "signal"
        )

        private val EMAIL_PACKAGE_HINTS = listOf("gmail", "outlook", "mail")

        private val NAVIGATION_PACKAGE_HINTS = listOf("maps", "waze", "navigation")

        /**
         * Clasifica una notificacion combinando la categoria declarada por
         * el sistema (mas confiable cuando esta presente) con heuristica de
         * paquete (fallback para apps que no declaran category).
         */
        fun classify(packageName: String, systemCategory: String?): NotificationCategory {
            systemCategory?.let { raw ->
                SYSTEM_CATEGORY_MAP[raw]?.let { return it }
            }

            val normalizedPackage = packageName.lowercase()

            return when {
                SOCIAL_PACKAGE_HINTS.any { normalizedPackage.contains(it) } -> {
                    // WhatsApp/Telegram/Signal son mensajeria directa, no "social feed".
                    if (normalizedPackage.contains("whatsapp") ||
                        normalizedPackage.contains("telegram") ||
                        normalizedPackage.contains("signal")
                    ) {
                        MESSAGE
                    } else {
                        SOCIAL
                    }
                }
                EMAIL_PACKAGE_HINTS.any { normalizedPackage.contains(it) } -> EMAIL
                NAVIGATION_PACKAGE_HINTS.any { normalizedPackage.contains(it) } -> NAVIGATION
                MEDIA_PACKAGE_HINTS.any { normalizedPackage.contains(it) } -> MEDIA
                else -> OTHER
            }
        }
    }
}
