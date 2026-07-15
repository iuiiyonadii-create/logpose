package com.uriel.logpose.logcore.providers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit

class DefaultProviderRegistryTest {

    private interface Greeter {
        fun greet(): String
    }

    private class EnglishGreeter : Greeter {
        override fun greet(): String = "hello"
    }

    private class LifecycleGreeter(
        val createCount: AtomicInteger,
        val disposeCount: AtomicInteger
    ) : Greeter, ProviderLifecycle {
        override fun greet(): String = "hello"
        override fun onCreate() {
            createCount.incrementAndGet()
        }
        override fun onDispose() {
            disposeCount.incrementAndGet()
        }
    }

    private class FailingFactoryProbe {
        var attempts = 0
    }

    @Test
    fun `get returns the same singleton instance across repeated calls`() {
        val registry = DefaultProviderRegistry()
        registry.register<Greeter> { EnglishGreeter() }

        val first = registry.get<Greeter>()
        val second = registry.get<Greeter>()

        assertSame(first, second)
        assertEquals("hello", first.greet())
    }

    @Test
    fun `get before register throws ProviderNotRegisteredException`() {
        val registry = DefaultProviderRegistry()

        try {
            registry.get<Greeter>()
            fail("Expected ProviderNotRegisteredException")
        } catch (expected: ProviderNotRegisteredException) {
            assertTrue(expected.message!!.contains("Greeter"))
        }
    }

    @Test
    fun `getOrNull before register returns null`() {
        val registry = DefaultProviderRegistry()

        val result = registry.getOrNull<Greeter>()

        assertNull(result)
    }

    @Test
    fun `duplicate register throws ProviderAlreadyRegisteredException`() {
        val registry = DefaultProviderRegistry()
        registry.register<Greeter> { EnglishGreeter() }

        try {
            registry.register<Greeter> { EnglishGreeter() }
            fail("Expected ProviderAlreadyRegisteredException")
        } catch (expected: ProviderAlreadyRegisteredException) {
            assertTrue(expected.message!!.contains("Greeter"))
        }
    }

    @Test
    fun `factory failure is wrapped and a later get retries construction`() {
        val registry = DefaultProviderRegistry()
        val probe = FailingFactoryProbe()

        registry.register<Greeter> {
            probe.attempts += 1
            if (probe.attempts == 1) {
                throw IllegalStateException("boom")
            }
            EnglishGreeter()
        }

        try {
            registry.get<Greeter>()
            fail("Expected ProviderInitializationException")
        } catch (expected: ProviderInitializationException) {
            assertTrue(expected.cause is IllegalStateException)
        }

        // Second attempt retries the factory rather than replaying the failure.
        val result = registry.get<Greeter>()
        assertEquals("hello", result.greet())
        assertEquals(2, probe.attempts)
    }

    @Test
    fun `lifecycle provider receives onCreate on first resolution and onDispose on dispose`() {
        val registry = DefaultProviderRegistry()
        val createCount = AtomicInteger(0)
        val disposeCount = AtomicInteger(0)

        registry.register<Greeter> { LifecycleGreeter(createCount, disposeCount) }

        assertEquals(0, createCount.get())

        registry.get<Greeter>()
        registry.get<Greeter>()

        assertEquals(1, createCount.get())
        assertEquals(0, disposeCount.get())

        registry.dispose()

        assertEquals(1, disposeCount.get())
    }

    @Test
    fun `provider registered but never resolved does not receive onDispose`() {
        val registry = DefaultProviderRegistry()
        val createCount = AtomicInteger(0)
        val disposeCount = AtomicInteger(0)

        registry.register<Greeter> { LifecycleGreeter(createCount, disposeCount) }

        registry.dispose()

        assertEquals(0, createCount.get())
        assertEquals(0, disposeCount.get())
    }

    @Test
    fun `isRegistered reflects registration state`() {
        val registry = DefaultProviderRegistry()

        assertFalse(registry.isRegistered<Greeter>())

        registry.register<Greeter> { EnglishGreeter() }

        assertTrue(registry.isRegistered<Greeter>())
    }

    @Test
    fun `concurrent first access creates the provider exactly once`() {
        val registry = DefaultProviderRegistry()
        val creationCount = AtomicInteger(0)
        val threadCount = 16
        val readyLatch = CountDownLatch(threadCount)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)

        registry.register<Greeter> {
            creationCount.incrementAndGet()
            EnglishGreeter()
        }

        val results = java.util.Collections.synchronizedList(mutableListOf<Greeter>())

        repeat(threadCount) {
            Thread {
                readyLatch.countDown()
                startLatch.await()
                results.add(registry.get<Greeter>())
                doneLatch.countDown()
            }.start()
        }

        readyLatch.await()
        startLatch.countDown()
        val completed = doneLatch.await(10, TimeUnit.SECONDS)

        assertTrue("Threads did not complete in time", completed)
        assertEquals(1, creationCount.get())
        assertTrue(results.all { it === results.first() })
    }
}
