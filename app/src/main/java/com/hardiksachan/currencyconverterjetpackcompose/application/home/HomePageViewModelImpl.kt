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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class HomePageViewModelImpl(
    private val repository: ICurrencyRepository,
    private val effectHandler: IHomePageUI.EffectHandler,
    private val dispatcherProvider: DispatcherProvider
) : BaseLogic<HomePageEvent>(),
    IHomePageUI.State,
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


    private fun handleBaseCurrencyDisplayTextChanged(text: String) {
        try {
            baseDisplay = text.toDouble()

        } catch (exp: NumberFormatException) {
            effectHandler.showToast("Invalid number entered")
        }
    }

    private suspend fun handleBaseCurrencyChangeRequested() {
        effectHandler.receiveBaseCurrency {
            baseCurrency.emit(it)
        }
    }

    private fun handleTargetCurrencyChangeRequested() {
        effectHandler.receiveBaseCurrency {
            targetCurrency.emit(it)
        }
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
}