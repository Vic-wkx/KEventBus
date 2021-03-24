package com.library.keventbus

object KEventBus {

    private val subscriptionsByEventType = mutableMapOf<Class<*>, MutableList<SubscriberMethod>>()
    private val stickyEvents = mutableMapOf<Class<*>, Any>()

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
                    if (it.getAnnotation(Subscribe::class.java)!!.sticky) {
                        if (stickyEvents[eventType] != null) {
                            it(obj, stickyEvents[eventType])
                        }
                    }
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

    fun postSticky(event: Any) {
        stickyEvents[event.javaClass] = event
        post(event)
    }

    fun removeSticky(event: Class<*>) {
        stickyEvents.remove(event)
    }

    fun invokeSubscriber(event: Any) {
        subscriptionsByEventType[event.javaClass]?.forEach {
            it.method(it.eventType, event)
        }
    }
}