package com.example.exchange.utils

import androidx.lifecycle.Observer

class EventObserver<T>(private val f: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>) {
        event.getContent()?.let { content ->
            f(content)
        }
    }

}