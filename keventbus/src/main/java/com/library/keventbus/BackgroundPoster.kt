package com.library.keventbus

import java.util.concurrent.Executors

class BackgroundPoster(private val eventBus: KEventBus) : Runnable {
    private val queue = mutableListOf<Any>()
    private val executors = Executors.newCachedThreadPool()

    fun post(event: Any) {
        queue.add(event)
        executors.execute(this)
    }

    override fun run() {
        val event = queue.removeFirst()
        eventBus.invokeSubscriber(event)
    }
}