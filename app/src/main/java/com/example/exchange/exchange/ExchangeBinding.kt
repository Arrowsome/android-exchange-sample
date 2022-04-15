@file:JvmName("ExchangeBinding")

package com.example.exchange.exchange

import androidx.lifecycle.LiveData
import com.example.exchange.data.Balance
import com.example.exchange.data.Currency

fun LiveData<List<Balance>>?.getAmount(currency: Currency): String {
    return this?.value?.find { it.currency == currency }?.amount?.toString() + " $currency"
}