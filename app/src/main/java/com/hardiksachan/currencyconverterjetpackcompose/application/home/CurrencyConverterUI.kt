package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.application.base.UIEffect
import com.hardiksachan.currencyconverterjetpackcompose.application.base.UIEvent
import com.hardiksachan.currencyconverterjetpackcompose.application.base.UIState
import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

class CurrencyConverterUI {

    sealed class Event : UIEvent {
        object UIStarted : Event()
        object EvaluatePressed : Event()
        object SwitchCurrenciesPressed : Event()

        object BaseCurrencyChangeStarted : Event()
        data class BaseCurrencyChanged(val updatedCurrency: ResultWrapper<Exception, Currency>) :
            Event()

        object TargetCurrencyChangeStarted : Event()
        data class TargetCurrencyChanged(val updatedCurrency: ResultWrapper<Exception, Currency>) :
            Event()


        data class BaseCurrencyDisplayTextChanged(val newText: String) : Event()
    }

    data class State(
        val baseCurrency: Currency,
        val targetCurrency: Currency,
        val baseCurrencyDisplay: String,
        val targetCurrencyDisplay: String,

        val isLoading: Boolean
    ) : UIState

    sealed class Effect : UIEffect {
        data class ShowError(val message: String) : Effect()
    }

}