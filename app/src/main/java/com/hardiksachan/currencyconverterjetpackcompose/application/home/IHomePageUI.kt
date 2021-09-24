package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import kotlinx.coroutines.flow.StateFlow

interface IHomePageUI {

    interface State {
        val baseCurrency: StateFlow<Currency>
        val targetCurrency: StateFlow<Currency>

        val baseCurrencyDisplay: StateFlow<String>
        val targetCurrencyDisplay: StateFlow<String>

        val isLoading: StateFlow<Boolean>
        val error: StateFlow<String?>
    }

    interface Effect {
        fun showToast(message: String)
        fun receiveBaseCurrency(callback: suspend (Currency) -> Unit)
        fun receiveTargetCurrency(callback: suspend (Currency) -> Unit)
    }

}