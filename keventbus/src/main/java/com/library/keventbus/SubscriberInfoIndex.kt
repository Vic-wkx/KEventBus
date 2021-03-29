package com.library.keventbus

interface SubscriberInfoIndex {
    val methodsByClass: Map<Class<*>, MutableList<SubscriberMethodInfo>>
}