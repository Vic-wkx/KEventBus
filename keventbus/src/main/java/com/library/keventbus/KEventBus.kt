package com.library.keventbus

import java.lang.reflect.Method

object KEventBus {

    private val subscriptionsByEventType = mutableMapOf<Class<*>, MutableList<Pair<Any, Method>>>()

    fun register(obj: Any) {
        // Reflect to get all subscribed methods
        obj.javaClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(Subscribe::class.java)) {
                if (it.parameterTypes.size == 1) {
                    val eventType = it.parameterTypes.first()
                    if (eventType !in subscriptionsByEventType) {
                        subscriptionsByEventType[eventType] = mutableListOf()
                    }
                    subscriptionsByEventType[eventType]!!.add(obj to it)
                }
            }
        }
    }

    fun unregister(obj: Any) {
        obj.javaClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(Subscribe::class.java)) {
                if (it.parameterTypes.size == 1) {
                    val event = it.parameterTypes.first()
                    if (event in subscriptionsByEventType) {
                        subscriptionsByEventType[event]?.remove(obj to it)
                    }
                }
            }
        }
    }

    fun post(event: Any) {
        subscriptionsByEventType[event.javaClass]?.forEach {
            it.second.invoke(it.first, event)
        }
    }
}