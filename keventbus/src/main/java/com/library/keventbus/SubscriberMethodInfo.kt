package com.library.keventbus

data class SubscriberMethodInfo(val methodName: String, val eventType: Class<*>, val threadMode: ThreadMode, val sticky: Boolean)