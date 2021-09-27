package com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

sealed class CurrencyConverterEvent {
    object EvaluatePressed : CurrencyConverterEvent()
    object SwitchCurrenciesPressed : CurrencyConverterEvent()
    object BaseCurrencyChangeRequested : CurrencyConverterEvent()
    object TargetCurrencyChangeRequested : CurrencyConverterEvent()
    object PullToRefresh : CurrencyConverterEvent()
    object OnStop: CurrencyConverterEvent()
    data class CurrencySelected(val currency: Currency) : CurrencyConverterEvent()
    data class SearchDisplayTextChanged(val newText: String) : CurrencyConverterEvent()
    data class BaseCurrencyDisplayTextChanged(val newText: String) : CurrencyConverterEvent()
}