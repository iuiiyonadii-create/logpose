package com.uriel.logpose.logprobe.models

sealed class ProbeResult<out T> {
    data class Success<T>(val value: T) : ProbeResult<T>()
    data class Rejected(val reason: String) : ProbeResult<Nothing>()
    data class Failure(val error: Throwable) : ProbeResult<Nothing>()
}
