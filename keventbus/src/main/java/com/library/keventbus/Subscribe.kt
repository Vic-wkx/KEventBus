package com.library.keventbus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(val threadMode: ThreadMode = ThreadMode.MAIN, val sticky: Boolean = false)