package com.library.keventbus

import java.lang.reflect.Method

data class SubscriberMethod(val obj: Any, val method: Method, val threadMode: ThreadMode)