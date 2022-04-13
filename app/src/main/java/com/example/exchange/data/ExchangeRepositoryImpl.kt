package com.example.exchange.data

import com.example.exchange.data.Result.Success
import com.example.exchange.data.Result.Error

// TODO: testing
class ExchangeRepositoryImpl(private val service: ExchangeService) : ExchangeRepository {

    override suspend fun getRate(): Result<ExchangeRate> {
        return try {
            val response = service.getRate()
            if (response.isSuccessful) {
                Success(response.body())
            } else {
                Error(Exception("Unable to fetch data!"))
            }
        } catch (exc: Exception) {
            Error(exc)
        }
    }

}