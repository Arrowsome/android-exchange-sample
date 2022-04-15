package com.example.exchange.adapters

import android.widget.Spinner
import androidx.databinding.BindingAdapter

@BindingAdapter("defaultEntryPosition")
fun bindDefaultEntryPosition(spinner: Spinner, defaultEntryPosition: Int) {
    spinner.setSelection(defaultEntryPosition)
}