package com.library.keventbus

import java.lang.reflect.Method

data class SubscriberMethod(val eventType: Any, val method: Method, val threadMode: ThreadMode)