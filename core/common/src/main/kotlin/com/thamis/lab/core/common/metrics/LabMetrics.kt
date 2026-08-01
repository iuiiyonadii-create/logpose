package com.thamis.lab.core.common.metrics

/**
 * Metric collector abstraction for tracking latency, throughput, and errors.
 */
public interface LabMetrics {
    public fun recordLatency(metricName: String, durationMs: Long)
    public fun incrementCounter(metricName: String, value: Long = 1)
    public fun recordGauge(metricName: String, value: Double)

    public companion object : LabMetrics {
        private var instance: LabMetrics = NoOpLabMetrics()

        public fun setInstance(metrics: LabMetrics) {
            instance = metrics
        }

        override fun recordLatency(metricName: String, durationMs: Long): Unit = instance.recordLatency(metricName, durationMs)
        override fun incrementCounter(metricName: String, value: Long): Unit = instance.incrementCounter(metricName, value)
        override fun recordGauge(metricName: String, value: Double): Unit = instance.recordGauge(metricName, value)
    }
}

private class NoOpLabMetrics : LabMetrics {
    override fun recordLatency(metricName: String, durationMs: Long) {}
    override fun incrementCounter(metricName: String, value: Long) {}
    override fun recordGauge(metricName: String, value: Double) {}
}
