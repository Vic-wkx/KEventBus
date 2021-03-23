package com.library.keventbus

import android.os.Handler
import android.os.Looper
import android.os.Message

class MainPoster(private val eventBus: KEventBus) : Handler(Looper.getMainLooper()) {
    private val queue = mutableListOf<Any>()

    fun post(event: Any) {
        queue.add(event)
        sendEmptyMessage(0)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val event = queue.removeFirst()
        eventBus.invokeSubscriber(event)
    }
}