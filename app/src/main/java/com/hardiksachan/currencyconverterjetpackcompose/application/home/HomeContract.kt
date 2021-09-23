package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.application.base.UiEffect
import com.hardiksachan.currencyconverterjetpackcompose.application.base.UiEvent
import com.hardiksachan.currencyconverterjetpackcompose.application.base.UiState

class HomeContract {

    sealed class Event : UiEvent {

    }

    data class State(
        val s: String
    ) : UiState

    sealed class Effect : UiEffect {
        object ShowError : Effect()
    }

}