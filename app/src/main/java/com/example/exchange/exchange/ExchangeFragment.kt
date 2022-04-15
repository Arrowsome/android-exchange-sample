package com.example.exchange.exchange

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.exchange.R
import com.example.exchange.databinding.FragmentExchangeBinding
import com.example.exchange.utils.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeFragment : Fragment() {
    private lateinit var binding: FragmentExchangeBinding
    private val viewModel: ExchangeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(inflater, R.layout.fragment_exchange, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.snackEvent.observe(viewLifecycleOwner, EventObserver { msgResId ->
            Snackbar.make(binding.root, msgResId, Snackbar.LENGTH_SHORT).show()
        })

        viewModel.sellInputChangeEvent.observe(viewLifecycleOwner, EventObserver { amount ->
            if (viewModel.isFocusedOnSelling.value == true) {
                if (amount == "-1.0") {
                    binding.receiveAmountInput.setText("")
                } else {
                    binding.receiveAmountInput.setText(amount)
                }
            }
        })

        viewModel.receiveInputChangeEvent.observe(viewLifecycleOwner, EventObserver { amount ->
            if (viewModel.isFocusedOnReceiving.value == true) {
                if (amount == "-1.0") {
                    binding.sellAmountInput.setText("")
                } else {
                    binding.sellAmountInput.setText(amount)
                }
            }
        })

        viewModel.updateOriginCurrencyEvent.observe(viewLifecycleOwner, EventObserver { index ->
            binding.originCurrencySpinner.setSelection(index)
        })

        viewModel.updateTargetCurrencyEvent.observe(viewLifecycleOwner, EventObserver { index ->
            binding.targetCurrencySpinner.setSelection(index)
        })

        viewModel.insufficientBalance.observe(viewLifecycleOwner, EventObserver { insufficient ->
            val color = if (insufficient) android.R.color.holo_red_dark else android.R.color.black
            binding.sellAmountInput.setTextColor(ContextCompat.getColor(requireContext(), color))
        })

        binding.sellAmountInput.requestFocus()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler(Looper.getMainLooper()).postDelayed({
                binding.targetCurrencySpinner.setSelection(1)
            }, 500,
        )
    }
}