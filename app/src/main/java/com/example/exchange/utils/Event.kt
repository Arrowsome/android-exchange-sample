package com.example.exchange.utils

data class Event<T>(private val data: T) {
    private var hasBeenHandled = false

    fun getContent(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

}
