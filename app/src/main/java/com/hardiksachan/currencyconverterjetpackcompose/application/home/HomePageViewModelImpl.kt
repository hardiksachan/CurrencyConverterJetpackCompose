package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.application.BaseLogic
import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider
import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.common.inrCurrency
import com.hardiksachan.currencyconverterjetpackcompose.common.usdCurrency
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.domain.repository.ICurrencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class HomePageViewModelImpl(
    private val repository: ICurrencyRepository,
    private val dispatcherProvider: DispatcherProvider
) : BaseLogic<HomePageEvent>(),
    HomePageState,
    CoroutineScope {

    private var baseDisplay: Double
        get() = baseCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                targetDisplay = 0.0
                baseCurrencyDisplay.emit(String.format("%.2f", value))
            }
        }

    private var targetDisplay: Double
        get() = targetCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                targetCurrencyDisplay.emit(String.format("%.2f", value))
            }
        }

    private val effectChannel: Channel<HomePageEffect> = Channel()

    override val coroutineContext: CoroutineContext
        get() = jobTracker + dispatcherProvider.UI()

    init {
        jobTracker = Job()
    }

    override fun onEvent(event: HomePageEvent) {
        launch {
            when (event) {
                HomePageEvent.BaseCurrencyChangeRequested -> handleBaseCurrencyChangeRequested()
                is HomePageEvent.BaseCurrencyDisplayTextChanged -> handleBaseCurrencyDisplayTextChanged(
                    event.newText
                )
                HomePageEvent.EvaluatePressed -> handleEvaluatePressed()
                HomePageEvent.SwitchCurrenciesPressed -> handleSwitchCurrenciesPressed()
                HomePageEvent.TargetCurrencyChangeRequested -> handleTargetCurrencyChangeRequested()
            }
        }
    }

    private suspend fun handleSwitchCurrenciesPressed() {
        withLoading {
            val oldBaseCurrency = baseCurrency.value
            val oldBaseDisplay = baseDisplay

            baseCurrency.emit(targetCurrency.value)
            baseDisplay = targetDisplay

            targetCurrency.emit(oldBaseCurrency)
            targetDisplay = oldBaseDisplay
        }
    }

    private suspend fun handleEvaluatePressed() {
        withLoading {
            val response = withContext(dispatcherProvider.IO()) {
                repository.getConversionFactor(
                    baseCurrency = baseCurrency.value,
                    targetCurrency = targetCurrency.value
                )
            }

            when (response) {
                is ResultWrapper.Failure -> error.emit(response.error.message)
                is ResultWrapper.Success -> targetDisplay = baseDisplay * response.result.rate
            }
        }
    }


    private suspend fun handleBaseCurrencyDisplayTextChanged(text: String) {
        try {
            baseDisplay = text.toDouble()

        } catch (exp: NumberFormatException) {
            effectChannel.send(HomePageEffect.ShowToast("Invalid number entered"))
        }
    }

    private suspend fun handleBaseCurrencyChangeRequested() {
        effectChannel.send(HomePageEffect.ReceiveBaseCurrency {
            baseCurrency.emit(it)
        })
    }

    private suspend fun handleTargetCurrencyChangeRequested() {
        effectChannel.send(HomePageEffect.ReceiveTargetCurrency {
            targetCurrency.emit(it)
        })
    }

    private suspend fun withLoading(f: suspend () -> Unit) {
        isLoading.emit(true)

        f.invoke()

        isLoading.emit(false)
    }


// Regarding UI State

    override val baseCurrency: MutableStateFlow<Currency> = MutableStateFlow(usdCurrency)
    override val targetCurrency: MutableStateFlow<Currency> = MutableStateFlow(inrCurrency)
    override val baseCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("0.00")
    override val targetCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("0.00")
    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val error: MutableStateFlow<String?> = MutableStateFlow(null)
    override val effectStream: Flow<HomePageEffect> = effectChannel.receiveAsFlow()
}