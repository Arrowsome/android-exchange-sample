package com.example.exchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.exchange.databinding.ActivityContainerBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

@AndroidEntryPoint
class Container : AppCompatActivity() {
    private lateinit var binding: ActivityContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_container)

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(findNavController(R.id.nav_container))
    }

}