package com.example.exchange.data

import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    val balance: Flow<List<Balance>>
    val exchangeCount: Flow<Int>
    suspend fun updateBalance(currency: Currency, newAmount: Double)
    suspend fun registerSwap()
}