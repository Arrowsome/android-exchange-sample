package com.example.exchange.adapters

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import java.text.DecimalFormat

@BindingAdapter("onFocusChanged")
fun bindOnFocusChanged(editText: EditText, listener: FocusChangeListener) {
    editText.setOnFocusChangeListener { _, focused ->
        listener.onChanged(focused)
    }
}

interface FocusChangeListener {
    fun onChanged(focused: Boolean)
}

//object DecimalUtil {
//    private val formatter = DecimalFormat("0.00").apply {
//        maximumFractionDigits = 2
//    }
//
//    @JvmStatic
//    @InverseMethod("limitDecimal")
//    fun limitDecimal(input: String?): String {
//        val value = input?.toDoubleOrNull() ?: return ""
//        return if (value.rem(1) > 0.99) {
//            return formatter.format(value)
//        } else {
//            return value.toString()
//        }
//    }
//}