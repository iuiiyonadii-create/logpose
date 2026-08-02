package com.thamis.lab.core.common.error

/**
 * Base sealed class for all THAMIS Lab typed errors.
 */
public sealed class LabError(
    public open val message: String,
    public open val cause: Throwable? = null
) {
    public data class SystemError(
        override val message: String,
        override val cause: Throwable? = null
    ) : LabError(message, cause)

    public data class ValidationError(
        override val message: String,
        public val field: String? = null
    ) : LabError(message)

    public data class ExecutionError(
        override val message: String,
        public val code: Int = -1,
        override val cause: Throwable? = null
    ) : LabError(message, cause)

    public data class TimeoutError(
        override val message: String,
        public val timeoutMs: Long
    ) : LabError(message)
}
