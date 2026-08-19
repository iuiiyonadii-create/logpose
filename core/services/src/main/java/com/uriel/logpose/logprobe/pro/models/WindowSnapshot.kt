package com.uriel.logpose.logprobe.models

data class WindowSnapshot(
    val timestampMillis: Long,
    val packageName: String,
    val className: String?
)
