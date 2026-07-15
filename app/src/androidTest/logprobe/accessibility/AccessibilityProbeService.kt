package com.uriel.logpose.logprobe.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityProbeService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("LOGPROBE", "========== LOGPROBE STARTED ==========")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        Log.i("LOGPROBE", "")
        Log.i("LOGPROBE", "===================================")
        Log.i("LOGPROBE", "PACKAGE : ${event.packageName}")
        Log.i("LOGPROBE", "TYPE    : ${event.eventType}")
        Log.i("LOGPROBE", "CLASS   : ${event.className}")
        Log.i("LOGPROBE", "TEXT    : ${event.text}")
        Log.i("LOGPROBE", "CONTENT : ${event.contentDescription}")

        rootInActiveWindow?.let {
            dumpTree(it, 0)
        }

    }

    override fun onInterrupt() {}

    private fun dumpTree(
        node: AccessibilityNodeInfo,
        depth: Int
    ) {

        val prefix = " ".repeat(depth * 2)

        Log.i(
            "TREE",
            "$prefix${node.className} | text=${node.text} | desc=${node.contentDescription}"
        )

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                dumpTree(it, depth + 1)
            }
        }

    }

}