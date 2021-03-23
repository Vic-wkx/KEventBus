package com.library.keventbus

object KEventBus {

    private val subscriptionsByEventType = mutableMapOf<Class<*>, MutableList<SubscriberMethod>>()

    private val mainPoster = MainPoster(this)
    private val asyncPoster = AsyncPoster(this)

    fun register(obj: Any) {
        // Reflect to get all subscribed methods
        obj.javaClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(Subscribe::class.java)) {
                if (it.parameterTypes.size == 1) {
                    val eventType = it.parameterTypes.first()
                    if (eventType !in subscriptionsByEventType) {
                        subscriptionsByEventType[eventType] = mutableListOf()
                    }
                    subscriptionsByEventType[eventType]!!.add(SubscriberMethod(obj, it, it.getAnnotation(Subscribe::class.java)!!.threadMode))
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
                        subscriptionsByEventType[event]?.remove(SubscriberMethod(obj, it, it.getAnnotation(Subscribe::class.java)!!.threadMode))
                    }
                }
            }
        }
    }

    fun post(event: Any) {
        subscriptionsByEventType[event.javaClass]?.forEach {
            when (it.threadMode) {
                ThreadMode.MAIN -> {
                    mainPoster.post(event)
                }
                ThreadMode.ASYNC -> {
                    asyncPoster.post(event)
                }
            }
        }
    }

    fun invokeSubscriber(event: Any) {
        subscriptionsByEventType[event.javaClass]?.forEach {
            it.method(it.eventType, event)
        }
    }
}