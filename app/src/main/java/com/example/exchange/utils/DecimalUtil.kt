package com.example.exchange.utils

object DecimalUtil {

    @JvmStatic
    fun setDecimalPoint(input: String?): String {
        if (input?.contains("null") == true) return input
        val tokens = input?.split(' ') ?: return ""
        return String.format("%.2f", tokens[0].toDouble()) + " ${tokens[1]}"
    }

}