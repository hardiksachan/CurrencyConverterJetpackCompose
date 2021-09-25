package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

sealed class HomePageEffect {
    data class ShowToast(val message: String) : HomePageEffect()
    data class ReceiveBaseCurrency(val callback: suspend (Currency) -> Unit) : HomePageEffect()
    data class ReceiveTargetCurrency(val callback: suspend (Currency) -> Unit) : HomePageEffect()
}
