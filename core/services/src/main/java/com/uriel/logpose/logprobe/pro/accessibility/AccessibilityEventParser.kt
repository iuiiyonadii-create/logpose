package com.uriel.logpose.logprobe.accessibility

import android.view.accessibility.AccessibilityEvent

/**
 * Traduce un AccessibilityEvent crudo del SO a datos primitivos simples.
 * No decide si el evento se puede registrar (eso es ProbeGuard); solo
 * extrae los campos que otras clases van a necesitar evaluar.
 */
object AccessibilityEventParser {

    fun packageNameOf(event: AccessibilityEvent): String? =
        event.packageName?.toString()

    fun classNameOf(event: AccessibilityEvent): String? =
        event.className?.toString()

    fun eventTypeNameOf(event: AccessibilityEvent): String =
        AccessibilityEvent.eventTypeToString(event.eventType)
}
