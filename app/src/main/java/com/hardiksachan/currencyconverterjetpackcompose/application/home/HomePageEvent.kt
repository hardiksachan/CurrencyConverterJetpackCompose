package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

sealed class HomePageEvent {
    object EvaluatePressed : HomePageEvent()
    object SwitchCurrenciesPressed : HomePageEvent()

    object BaseCurrencyChangeStarted : HomePageEvent()
    data class BaseCurrencyChanged(val updatedCurrency: ResultWrapper<Exception, Currency>) :
        HomePageEvent()

    object TargetCurrencyChangeStarted : HomePageEvent()
    data class TargetCurrencyChanged(val updatedCurrency: ResultWrapper<Exception, Currency>) :
        HomePageEvent()

    data class BaseCurrencyDisplayTextChanged(val newText: String) : HomePageEvent()
}