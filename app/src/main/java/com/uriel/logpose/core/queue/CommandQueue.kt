package com.uriel.logpose.core.queue

object CommandQueue {

    private val queue =
        ArrayDeque<QueuedCommand>()

    fun add(
        command: QueuedCommand
    ) {

        queue.addLast(command)
    }

    fun poll(): QueuedCommand? {

        if (queue.isEmpty()) {
            return null
        }

        return queue.removeFirst()
    }

    fun clear() {

        queue.clear()
    }

    fun size(): Int =
        queue.size

    fun isEmpty(): Boolean =
        queue.isEmpty()
}