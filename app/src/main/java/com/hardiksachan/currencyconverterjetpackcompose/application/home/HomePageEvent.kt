package com.hardiksachan.currencyconverterjetpackcompose.application.home

sealed class HomePageEvent {
    object EvaluatePressed : HomePageEvent()
    object SwitchCurrenciesPressed : HomePageEvent()
    object BaseCurrencyChangeRequested : HomePageEvent()
    object TargetCurrencyChangeRequested : HomePageEvent()
    data class BaseCurrencyDisplayTextChanged(val newText: String) : HomePageEvent()
}