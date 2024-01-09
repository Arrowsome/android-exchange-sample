package com.example.exchange.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class WalletRepositoryImpl(private val dataStore: DataStore<Preferences>) : WalletRepository {

    override val balance: Flow<List<Balance>> = dataStore.data
        .catch { exc ->
            if (exc is IOException) {
                emit(emptyPreferences())
            } else {
                throw exc
            }
        }
        .map { pref ->
            val usd = pref[Constants.KEY_PREF_USD_BALANCE] ?: 0.0
            val eur = pref[Constants.KEY_PREF_EUR_BALANCE] ?: 1000.0
            val gbp = pref[Constants.KEY_PREF_GBP_BALANCE] ?: 0.0

            listOf(
                Balance(Currency.USD, usd),
                Balance(Currency.EUR, eur),
                Balance(Currency.GBP, gbp),
            )
        }

    override val exchangeCount: Flow<Int> = dataStore.data.map { pref ->
        pref[Constants.KEY_PREF_EXCHANGE_COUNT] ?: 0
    }

    override suspend fun updateBalance(currency: Currency, newAmount: Double) {
        dataStore.edit { prefs ->
            when (currency) {
                Currency.USD -> prefs[Constants.KEY_PREF_USD_BALANCE] = newAmount
                Currency.EUR -> prefs[Constants.KEY_PREF_EUR_BALANCE] = newAmount
                Currency.GBP -> prefs[Constants.KEY_PREF_GBP_BALANCE] = newAmount
            }
        }
    }

    override suspend fun registerSwap() {
        dataStore.edit { pref ->
            val currentSwaps = pref[Constants.KEY_PREF_EXCHANGE_COUNT] ?: 0
            pref[Constants.KEY_PREF_EXCHANGE_COUNT] = currentSwaps + 1
        }
    }

}