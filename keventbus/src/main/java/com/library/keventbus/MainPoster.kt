package com.library.keventbus

import android.os.Handler
import android.os.Looper
import android.os.Message

class MainPoster(private val eventBus: KEventBus) : Handler(Looper.getMainLooper()) {
    private val queue = mutableListOf<Pair<SubscriberMethod, Any>>()

    fun post(subscriberMethod: SubscriberMethod, event: Any) {
        queue.add(subscriberMethod to event)
        sendEmptyMessage(0)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val pair = queue.removeFirst()
        eventBus.invokeSubscriber(pair.first, pair.second)
    }
}