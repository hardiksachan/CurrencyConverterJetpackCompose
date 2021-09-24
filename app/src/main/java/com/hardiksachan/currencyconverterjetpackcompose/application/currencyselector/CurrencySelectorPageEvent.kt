package com.hardiksachan.currencyconverterjetpackcompose.application.currencyselector

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

sealed class CurrencySelectorPageEvent {
    object PullToRefresh : CurrencySelectorPageEvent()
    data class CurrencySelected(val currency: Currency) : CurrencySelectorPageEvent()
    data class SearchDisplayTextChanged(val newText: String) : CurrencySelectorPageEvent()
}