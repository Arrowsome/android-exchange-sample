package com.example.exchange.data

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    val KEY_PREF_EXCHANGE_COUNT = intPreferencesKey("exchange_count")
    val KEY_PREF_USD_BALANCE = doublePreferencesKey("balance_usd")
    val KEY_PREF_EUR_BALANCE = doublePreferencesKey("balance_eur")
    val KEY_PREF_GBP_BALANCE = doublePreferencesKey("balance_gbp")
}