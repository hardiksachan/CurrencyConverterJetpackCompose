package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.application.base.BaseViewModel
import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider

class HomeViewModel(dispatcherProvider: DispatcherProvider) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>(dispatcherProvider) {
    override fun handleEvent(event: HomeContract.Event) {
        TODO("Not yet implemented")
    }

    override fun createInitialState(): HomeContract.State {
        TODO("Not yet implemented")
    }
}