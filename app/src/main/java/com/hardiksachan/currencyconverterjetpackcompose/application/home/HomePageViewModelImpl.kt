package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.application.BaseLogic
import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider
import com.hardiksachan.currencyconverterjetpackcompose.common.inrCurrency
import com.hardiksachan.currencyconverterjetpackcompose.common.usdCurrency
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomePageViewModelImpl(
    private val dispatcherProvider: DispatcherProvider
) : BaseLogic<HomePageEvent>(),
    IHomePageUI.State,
    CoroutineScope {

    var baseDisplay: Double
        get() = baseCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                baseCurrencyDisplay.emit(String.format("%.2f", value))
            }
        }

    var targetDisplay: Double
        get() = targetCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                targetCurrencyDisplay.emit(String.format("%.2f", value))
            }
        }

    override val coroutineContext: CoroutineContext
        get() = jobTracker + dispatcherProvider.provideUIContext()

    init {
        jobTracker = Job()
    }

    override fun onEvent(event: HomePageEvent) {
         launch(dispatcherProvider.provideIOContext()) {
             when (event) {
                 HomePageEvent.BaseCurrencyChangeStarted -> TODO()
                 is HomePageEvent.BaseCurrencyChanged -> TODO()
                 is HomePageEvent.BaseCurrencyDisplayTextChanged -> TODO()
                 HomePageEvent.EvaluatePressed -> TODO()
                 HomePageEvent.SwitchCurrenciesPressed -> TODO()
                 HomePageEvent.TargetCurrencyChangeStarted -> TODO()
                 is HomePageEvent.TargetCurrencyChanged -> TODO()
             }
        }
    }

    // Regarding UI State

    override val baseCurrency: MutableStateFlow<Currency> = MutableStateFlow(usdCurrency)
    override val targetCurrency: MutableStateFlow<Currency> = MutableStateFlow(inrCurrency)
    override val baseCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("0.00")
    override val targetCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("0.00")
    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val error: MutableStateFlow<String?> = MutableStateFlow(null)
}