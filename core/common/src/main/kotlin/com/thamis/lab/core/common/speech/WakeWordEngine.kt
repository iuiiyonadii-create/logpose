package com.thamis.lab.core.common.speech

import com.thamis.lab.core.common.result.LabResult

/**
 * Interface for dedicated Wake Word detection.
 */
public interface WakeWordEngine {
    public val keyword: String
    
    /**
     * Analyzes audio buffer to detect the keyword.
     * Returns detection confidence (0.0 to 1.0).
     */
    public fun detect(pcmData: ShortArray): LabResult<Float>
}
