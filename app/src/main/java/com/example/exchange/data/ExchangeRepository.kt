package com.example.exchange.data

interface ExchangeRepository {
    suspend fun getRate(): Result<ExchangeRate>
}