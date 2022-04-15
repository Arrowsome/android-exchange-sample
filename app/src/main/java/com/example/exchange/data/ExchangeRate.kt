package com.example.exchange.data

import com.google.gson.annotations.SerializedName

data class ExchangeRate(
    @SerializedName("success") val success: Boolean,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("date") val date: String,
    @SerializedName("base") val base: String,
    @SerializedName("rates") val rates: Rate,
)

data class Rate(
    @SerializedName("USD") val usd: Double,
    @SerializedName("GBP") val gbp: Double,
)
