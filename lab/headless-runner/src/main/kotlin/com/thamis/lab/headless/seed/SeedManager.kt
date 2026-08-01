package com.thamis.lab.headless.seed

import java.util.Random

/**
 * Deterministic seed manager for pseudo-random state generation.
 */
public class SeedManager(public val masterSeed: Long) {
    private var random = Random(masterSeed)

    public fun nextLong(): Long = random.nextLong()
    public fun nextDouble(): Double = random.nextDouble()
    public fun nextInt(bound: Int): Int = random.nextInt(bound)

    public fun reset() {
        random = Random(masterSeed)
    }
}
