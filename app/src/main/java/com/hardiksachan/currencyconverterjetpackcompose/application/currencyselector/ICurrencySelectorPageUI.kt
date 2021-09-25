package com.hardiksachan.currencyconverterjetpackcompose.application.currencyselector

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import kotlinx.coroutines.flow.StateFlow

interface ICurrencySelectorPageUI {

    interface State {
        val currencyList: StateFlow<List<Currency>>
        val searchDisplay: StateFlow<String>

        val isLoading: StateFlow<Boolean>
        val pullToRefreshVisible: StateFlow<Boolean>
        val error: StateFlow<String?>
    }

    interface EffectHandler {
        fun showToast(message: String)
        fun sendCurrency(currency: Currency)
    }

}