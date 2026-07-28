package com.uriel.logpose.thamis

import com.uriel.logpose.thamis.request.THAMISRequest
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureTimeMillis

class THAMISPerformanceTest {

    @Test
    fun `test THAMIS processing speed`() {
        val requests = listOf(
            THAMISRequest("log pone musica"),
            THAMISRequest("log abrir whatsapp"),
            THAMISRequest("log llevame a casa"),
            THAMISRequest("subir volumen"),
            THAMISRequest("siguiente cancion"),
            THAMISRequest("log llamar a mama"),
            THAMISRequest("abri instagram"),
            THAMISRequest("log uzbekistan")
        )

        val times = mutableListOf<Long>()

        // Warm up
        repeat(10) { THAMIS.process(requests[0]) }

        requests.forEach { request ->
            val time = measureTimeMillis {
                THAMIS.process(request)
            }
            times.add(time)
            println("THAMIS processing time for '${request.text}': ${time}ms")
        }

        val averageTime = times.average()
        println("Average THAMIS processing time: ${averageTime}ms")

        // Objective: < 100ms
        assertTrue("THAMIS is too slow: ${averageTime}ms", averageTime < 100)
    }

    @Test
    fun `test THAMIS under heavy load`() {
        val request = THAMISRequest("log pone algo de cumbia santafesina")
        
        val startTime = System.currentTimeMillis()
        val iterations = 1000
        
        repeat(iterations) {
            THAMIS.process(request)
        }
        
        val totalTime = System.currentTimeMillis() - startTime
        val timePerIteration = totalTime.toFloat() / iterations
        
        println("Stress test: $iterations iterations took ${totalTime}ms")
        println("Time per iteration: ${timePerIteration}ms")
        
        assertTrue("Stress test failed: ${timePerIteration}ms per call", timePerIteration < 50)
    }
}
