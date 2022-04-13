package com.example.exchange.data

import com.example.exchange.data.Result.Success

sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<ExchangeRate>()
    data class Error(val exc: Exception) : Result<Nothing>()

    override fun toString(): String = when(this) {
        is Success<*> -> "Success[data=$data]"
        is Error -> "Error[exception=$exc]"
    }
}

val Result<*>.succeeded
    get() = this is Success<*> && data != null