package com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter

sealed class CurrencyConverterEffect {
    data class ShowToast(val message: String) : CurrencyConverterEffect()
}
