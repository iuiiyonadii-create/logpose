package com.uriel.logpose.logprobe.models

data class MediaSnapshot(
    val packageName: String,
    val playbackState: String,
    val timestampMillis: Long,
    val hasActiveSession: Boolean
)
