package com.thamis.lab.core.common.logging

/**
 * Structured logger abstraction for THAMIS Lab.
 */
public interface LabLogger {
    public fun debug(tag: String, message: String)
    public fun info(tag: String, message: String)
    public fun warn(tag: String, message: String, throwable: Throwable? = null)
    public fun error(tag: String, message: String, throwable: Throwable? = null)

    public companion object : LabLogger {
        private var instance: LabLogger = DefaultLabLogger()

        public fun setInstance(logger: LabLogger) {
            instance = logger
        }

        override fun debug(tag: String, message: String): Unit = instance.debug(tag, message)
        override fun info(tag: String, message: String): Unit = instance.info(tag, message)
        override fun warn(tag: String, message: String, throwable: Throwable?): Unit = instance.warn(tag, message, throwable)
        override fun error(tag: String, message: String, throwable: Throwable?): Unit = instance.error(tag, message, throwable)
    }
}

private class DefaultLabLogger : LabLogger {
    override fun debug(tag: String, message: String) {
        println("[DEBUG] [$tag] $message")
    }

    override fun info(tag: String, message: String) {
        println("[INFO] [$tag] $message")
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        println("[WARN] [$tag] $message ${throwable?.message ?: ""}")
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        println("[ERROR] [$tag] $message ${throwable?.message ?: ""}")
    }
}
