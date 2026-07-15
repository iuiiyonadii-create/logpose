package com.uriel.logpose.logprobe.exporters

import com.uriel.logpose.logprobe.models.AccessibilityNodeSnapshot
import com.uriel.logpose.logprobe.models.AccessibilitySnapshot
import com.uriel.logpose.logprobe.models.BluetoothSnapshot
import com.uriel.logpose.logprobe.models.MediaSnapshot
import com.uriel.logpose.logprobe.models.NotificationSnapshot
import com.uriel.logpose.logprobe.models.ProbeEvent
import com.uriel.logpose.logprobe.models.ProbeReport
import com.uriel.logpose.logprobe.models.WindowSnapshot
import org.json.JSONArray
import org.json.JSONObject

/**
 * Exportador a JSON usando org.json (incluido en el SDK de Android,
 * sin agregar dependencias nuevas). Si el proyecto ya usa
 * kotlinx.serialization o Moshi, se puede reemplazar esta clase sin
 * tocar el resto del modulo: es el unico punto que conoce el formato
 * de salida.
 */
object JsonExporter {

    fun export(report: ProbeReport): String {
        val root = JSONObject()
        root.put("sessionId", report.sessionId)
        root.put("startedAtMillis", report.startedAtMillis)
        root.put("endedAtMillis", report.endedAtMillis)
        root.put("allowedPackages", JSONArray(report.allowedPackages.toList()))
        root.put("eventCount", report.eventCount)

        val eventsArray = JSONArray()
        report.events.forEach { eventsArray.put(eventToJson(it)) }
        root.put("events", eventsArray)

        return root.toString(2)
    }

    private fun eventToJson(event: ProbeEvent): JSONObject {
        val obj = JSONObject()
        obj.put("id", event.id)
        obj.put("sessionId", event.sessionId)
        obj.put("type", event.type.name)
        obj.put("timestampMillis", event.timestampMillis)
        obj.put("sourcePackage", event.sourcePackage)
        obj.put("snapshot", snapshotToJson(event.snapshot))
        return obj
    }

    private fun snapshotToJson(snapshot: Any): JSONObject = when (snapshot) {
        is AccessibilitySnapshot -> JSONObject().apply {
            put("packageName", snapshot.packageName)
            put("className", snapshot.className)
            put("eventType", snapshot.eventType)
            put("timestampMillis", snapshot.timestampMillis)
            put("rootNode", snapshot.rootNode?.let { nodeToJson(it) })
        }
        is NotificationSnapshot -> JSONObject().apply {
            put("packageName", snapshot.packageName)
            put("postedAtMillis", snapshot.postedAtMillis)
            put("category", snapshot.category)
            put("isOngoing", snapshot.isOngoing)
            put("isClearable", snapshot.isClearable)
            put("actionCount", snapshot.actionCount)
            put("hasContentIntent", snapshot.hasContentIntent)
            put("removed", snapshot.removed)
        }
        is MediaSnapshot -> JSONObject().apply {
            put("packageName", snapshot.packageName)
            put("playbackState", snapshot.playbackState)
            put("timestampMillis", snapshot.timestampMillis)
            put("hasActiveSession", snapshot.hasActiveSession)
        }
        is BluetoothSnapshot -> JSONObject().apply {
            put("timestampMillis", snapshot.timestampMillis)
            put("isEnabled", snapshot.isEnabled)
            put("connectedDeviceNames", JSONArray(snapshot.connectedDeviceNames))
        }
        is WindowSnapshot -> JSONObject().apply {
            put("timestampMillis", snapshot.timestampMillis)
            put("packageName", snapshot.packageName)
            put("className", snapshot.className)
        }
        else -> JSONObject().apply { put("raw", snapshot.toString()) }
    }

    private fun nodeToJson(node: AccessibilityNodeSnapshot): JSONObject = JSONObject().apply {
        put("className", node.className)
        put("text", node.text)
        put("contentDescription", node.contentDescription)
        put("viewIdResourceName", node.viewIdResourceName)
        put("childCount", node.childCount)
        put("children", JSONArray(node.children.map { nodeToJson(it) }))
    }
}
