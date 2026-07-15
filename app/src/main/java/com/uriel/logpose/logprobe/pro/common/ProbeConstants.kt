package com.uriel.logpose.logprobe.common

/**
 * Constantes estaticas de LogProbe.
 * No contiene estado mutable: cualquier valor que dependa de una sesion
 * vive en ProbeSession, nunca aca.
 */
object ProbeConstants {

    const val EXPORT_DIR_NAME = "LogProbe"
    const val REPORT_FILE_PREFIX = "probe_report_"
    const val ZIP_FILE_PREFIX = "probe_export_"

    // Denylist fija de paquetes. No editable en runtime, no expuesta en UI
    // como "desactivable". Ver ProbeGuard.
    val DEFAULT_PACKAGE_DENYLIST: Set<String> = setOf(
        // Mensajeria
        "com.whatsapp",
        "com.whatsapp.w4b",
        "org.telegram.messenger",
        "org.telegram.messenger.web",
        "org.thoughtcrime.securesms", // Signal
        "com.facebook.orca",          // Messenger
        "com.instagram.android",
        "com.facebook.katana",
        // Segundo factor / credenciales
        "com.google.android.apps.authenticator2",
        "com.azure.authenticator",
        "com.microsoft.authenticator",
        "com.authy.authy",
        // Password managers conocidos
        "com.lastpass.lpandroid",
        "com.dashlane",
        "com.agilebits.onepassword",
        "com.bitwarden.mobile",
        "com.keepersecurity.android",
        // Bancos / fintech comunes AR-LATAM (lista best-effort, ampliable
        // por configuracion del desarrollador pero NUNCA removible)
        "com.bancogalicia.mobile",
        "ar.com.santander.mobilebanking",
        "com.bbva.nxt_bbva",
        "com.icbc.imobile.ar",
        "com.mercadopago.wallet",
        "com.ualaappar",
        "com.brubank.brubank"
    )

    // Heuristica adicional: si el nombre de paquete contiene alguno de estos
    // fragmentos, se trata como banking/credencial aunque no este en la
    // lista exacta de arriba. Es un veto adicional, no un reemplazo.
    val DENYLIST_KEYWORDS: Set<String> = setOf(
        "bank", "banco", "authenticator", "password", "wallet", "2fa"
    )
}
