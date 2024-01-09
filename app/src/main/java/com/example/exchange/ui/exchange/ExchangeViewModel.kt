package com.example.exchange.ui.exchange

import androidx.lifecycle.*
import com.example.exchange.R
import com.example.exchange.data.*
import com.example.exchange.utils.Event
import com.example.exchange.utils.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _rate = MutableLiveData<ExchangeRate>()
    val rate: LiveData<ExchangeRate> = _rate

//    private val _accountBalance = MutableLiveData<Map<String, Double>>()
//    val accountBalance: LiveData<Map<String, Double>> = _accountBalance

    private val _sellCurrency = MutableLiveData<Currency>()
//    val originCurrency: LiveData<Currency> = _originCurrency

    private val _receiveCurrency = MutableLiveData<Currency>()
//    val targetCurrency: LiveData<Currency> = _targetCurrency

    private val _snackEvent = MutableLiveData<Event<Int>>()
    val snackEvent: LiveData<Event<Int>> = _snackEvent

    val numberOfExchanges = walletRepository.exchangeCount.asLiveData()

    val exchangeFee = numberOfExchanges.map { count ->
        if (count >= 5) 0.5 else 0.0
    }

    private val _done = MutableLiveData<Event<Unit>>()
    val done: LiveData<Event<Unit>> = _done

    private val _updateTargetCurrencyEvent = MutableLiveData<Event<Int>>()
    val updateTargetCurrencyEvent: LiveData<Event<Int>> = _updateTargetCurrencyEvent

    private val _updateOriginCurrencyEvent = MutableLiveData<Event<Int>>()
    val updateOriginCurrencyEvent: LiveData<Event<Int>> = _updateOriginCurrencyEvent

    val userBalance: LiveData<List<Balance>> = walletRepository.balance.asLiveData()

    val isFormValid = MutableLiveData(false)

    val supportedCurrencies = Currency.values().map { it.toString() }

    val exchangeFeeResId = R.string.exchange_label_exchangeFee

    val sellInput = MutableLiveData<String>()

    val receiveInput = MutableLiveData<String>()

    private val _insufficientBalance = MutableLiveData<Event<Boolean>>()
    val insufficientBalance: LiveData<Event<Boolean>> = _insufficientBalance

    val sellInputChangeEvent: LiveData<Event<String>> = sellInput.distinctUntilChanged().debounce(viewModelScope).map {
        isFormValid.value = false
        val amount = it.toDoubleOrNull() ?: return@map Event("-1.0")
        val from = _sellCurrency.value ?: return@map Event("-1.0")
        if (hasEnoughBalance(amount, from)) {
            val to = _receiveCurrency.value ?: return@map Event("-1.0")
            val convertedAmount = convertCurrency(amount, from, to)
            isFormValid.value = true
            _insufficientBalance.value = Event(false)
            return@map Event(String.format("%.2f", convertedAmount))
        } else {
            _insufficientBalance.value = Event(true)
        }
        // TODO: send event for EditText to show insufficient balance
        Event("-1.0")
    }

    val receiveInputChangeEvent = receiveInput.distinctUntilChanged().debounce(viewModelScope).map {
        val amount = it.toDoubleOrNull() ?: return@map Event("-1.0")
        val from = _receiveCurrency.value ?: return@map Event("-1.0")
        val to = _sellCurrency.value ?: return@map Event("-1.0")
        val convertedAmount = convertCurrency(amount, from, to)
        Event(String.format("%.2f", convertedAmount))
    }

    val isFocusedOnSelling = MutableLiveData<Boolean?>(null)
    val isFocusedOnReceiving = MutableLiveData<Boolean?>(null)

    init {
        onRefreshRate()
    }

    @Suppress("UNCHECKED_CAST")
    fun onRefreshRate() {
        viewModelScope.launch {
            val result = exchangeRepository.getRate()
            if (result.succeeded) {
                _rate.value = (result as Result.Success<*>).data as ExchangeRate
            } else {
                _snackEvent.value = Event(R.string.error_connection_failure)
            }
        }
    }

    fun onSubmit() {
        val sellAmount = sellInput.value?.toDoubleOrNull() ?: return
        val from = _sellCurrency.value ?: return
        val receiveAmount = receiveInput.value?.toDoubleOrNull() ?: return
        val to = _receiveCurrency.value ?: return
        dispatchBalanceUpdate(
            sellAmount,
            from,
            receiveAmount,
            to
        )
        dispatchSwapEvent()
        sellInput.value = ""
        receiveInput.value = ""
        isFormValid.value = false
        _done.value = Event(Unit)
    }

    fun onOriginCurrencySelected(position: Int) {
        val currentCurrency = _sellCurrency.value
        val newCurrency = Currency.values()[position]
        _sellCurrency.value = newCurrency
        sellInput.value = ""
        if (_receiveCurrency.value == newCurrency) {
            _updateTargetCurrencyEvent.value = Event(Currency.values().indexOf(currentCurrency))
        }
    }

    fun onTargetCurrencySelected(position: Int) {
        val currentCurrency = _receiveCurrency.value
        val newCurrency = Currency.values()[position]
        _receiveCurrency.value = newCurrency
        receiveInput.value = ""
        if (_sellCurrency.value == newCurrency) {
            _updateOriginCurrencyEvent.value = Event(Currency.values().indexOf(currentCurrency))
        }
    }

    fun onUpdateSellFocus(focused: Boolean) {
        isFocusedOnSelling.value = focused
    }

    fun onUpdateReceiveFocus(focused: Boolean) {
        isFocusedOnReceiving.value = focused
    }

    private fun convertCurrency(amount: Double, from: Currency, to: Currency): Double {
        val rates = rate.value?.rates ?: return -1.0
        when {
            from == Currency.EUR -> {
                return when (to) {
                    Currency.EUR -> throw IllegalStateException("from and to should not be the same currency")
                    Currency.USD -> amount * rates.usd
                    Currency.GBP -> amount * rates.gbp
                }
            }
            to == Currency.EUR -> {
                return when (from) {
                    Currency.EUR -> throw IllegalStateException("from and to should not be the same currency")
                    Currency.USD -> amount / rates.usd
                    Currency.GBP -> amount / rates.gbp
                }
            }
            else -> {
                val euroAmount = convertCurrency(amount, from, Currency.EUR)
                return convertCurrency(euroAmount, Currency.EUR, to)
            }
        }
    }

    private fun hasEnoughBalance(amount: Double, currency: Currency): Boolean {
        val swapFee = ((exchangeFee.value ?: 0.0) / 100) * amount
        return userBalance.value?.any { it.currency == currency && it.amount >= amount + swapFee } ?: false
    }

    private fun dispatchBalanceUpdate(sellAmount: Double, fromCurrency: Currency, buyAmount: Double, toCurrency: Currency) {
        val formatter = DecimalFormat("0.00").apply {
            maximumFractionDigits = 2
        }

        viewModelScope.launch {
            val sellingBalance = userBalance.value?.find { it.currency == fromCurrency } ?: return@launch
            val sellingAmount = sellingBalance.amount - sellAmount
            val feeAmount = (exchangeFee.value!! / 100.0) * sellAmount
            val totalAmountToWithdraw = sellingAmount - feeAmount
            walletRepository.updateBalance(fromCurrency, totalAmountToWithdraw)

            val toBalance = userBalance.value?.find { it.currency == toCurrency } ?: return@launch
            val sumOfNewBalance = toBalance.amount + buyAmount
            walletRepository.updateBalance(toCurrency, sumOfNewBalance)
        }
    }

    private fun dispatchSwapEvent() {
        viewModelScope.launch {
            walletRepository.registerSwap()
        }
    }

}

