package com.library.keventbus

import java.util.concurrent.Executors

class AsyncPoster(private val eventBus: KEventBus) : Runnable {
    private val queue = mutableListOf<Pair<SubscriberMethod, Any>>()
    private val executors = Executors.newCachedThreadPool()

    fun post(subscriberMethod: SubscriberMethod, event: Any) {
        queue.add(subscriberMethod to event)
        executors.execute(this)
    }

    override fun run() {
        val pair = queue.removeFirst()
        eventBus.invokeSubscriber(pair.first, pair.second)
    }
}