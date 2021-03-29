package com.library.keventbus

object KEventBus {

    private val subscriptionsByEventType = mutableMapOf<Class<*>, MutableList<SubscriberMethod>>()
    private val stickyEvents = mutableMapOf<Class<*>, Any>()

    private val mainPoster = MainPoster(this)
    private val asyncPoster = AsyncPoster(this)


    private val index: SubscriberInfoIndex by lazy { Class.forName("com.example.eventbus.KEventBusIndex").newInstance() as SubscriberInfoIndex }

    fun register(obj: Any) {
        index.methodsByClass[obj.javaClass]?.forEach {
            if (it.eventType !in subscriptionsByEventType) {
                subscriptionsByEventType[it.eventType] = mutableListOf()
            }
            val subscriberMethod = SubscriberMethod(obj, obj.javaClass.getDeclaredMethod(it.methodName, it.eventType), it.threadMode)
            subscriptionsByEventType[it.eventType]!!.add(subscriberMethod)
            if (it.sticky) {
                if (stickyEvents[it.eventType] != null) {
                    subscriberMethod.method(obj, stickyEvents[it.eventType])
                }
            }
        }
    }

    fun unregister(obj: Any) {
        index.methodsByClass[obj.javaClass]?.forEach {
            if (it.eventType in subscriptionsByEventType) {
                subscriptionsByEventType[it.eventType]?.remove(SubscriberMethod(obj, obj.javaClass.getDeclaredMethod(it.methodName, it.eventType), it.threadMode))
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
            it.method(it.obj, event)
        }
    }
}