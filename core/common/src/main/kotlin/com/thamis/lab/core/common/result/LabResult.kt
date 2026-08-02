package com.thamis.lab.core.common.result

import com.thamis.lab.core.common.error.LabError

/**
 * Result pattern for THAMIS Lab operations.
 * Encapsulates success values or typed errors.
 */
public sealed interface LabResult<out T> {
    public data class Success<out T>(public val data: T) : LabResult<T>
    public data class Failure(public val error: LabError) : LabResult<Nothing>

    public val isSuccess: Boolean get() = this is Success
    public val isFailure: Boolean get() = this is Failure

    public fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    public fun errorOrNull(): LabError? = when (this) {
        is Success -> null
        is Failure -> error
    }
}
