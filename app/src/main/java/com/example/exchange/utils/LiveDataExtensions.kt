package com.example.exchange.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.*

fun <T> LiveData<T>.debounce(
    coroutineScope: CoroutineScope,
    duration: Long = 1000L,
): MediatorLiveData<T> {
    return MediatorLiveData<T>().also { mediator ->  
        val source = this
        var job: Job? = null

        mediator.addSource(source) {
            job?.cancel()
            job = coroutineScope.launch {
                delay(duration)
                mediator.value = source.value
            }
        }
    }
}