package com.thamis.lab.core.common.result

import com.thamis.lab.core.common.error.LabError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LabResultTest {

    @Test
    fun testSuccessResult() {
        val result: LabResult<String> = LabResult.Success("THAMIS Lab")

        assertTrue(result.isSuccess)
        assertEquals("THAMIS Lab", result.getOrNull())
    }

    @Test
    fun testFailureResult() {
        val error = LabError.SystemError("Test Failure")
        val result: LabResult<String> = LabResult.Failure(error)

        assertTrue(result.isFailure)
        assertEquals(error, result.errorOrNull())
    }
}
