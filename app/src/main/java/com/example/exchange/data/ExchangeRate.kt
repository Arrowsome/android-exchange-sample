package com.example.exchange.data

data class ExchangeRate(
    val success: Boolean,
    val timestamp: Long,
    val date: String,
    val base: String,
    val rates: List<Rate>,
)

data class Rate(
    val symbol: String,
    val rate: Double,
)
