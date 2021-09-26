package com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter

import android.util.Log
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

typealias CurrencyConverterLogic = BaseLogic<CurrencyConverterEvent>

class CurrencyConverterViewModelImpl(
    private val repository: ICurrencyRepository,
    private val dispatcherProvider: DispatcherProvider
) : CurrencyConverterLogic(),
    HomePageState,
    CurrencySelectorPageState,
    CoroutineScope {

    private var baseDisplay: Double
        get() = baseCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                targetDisplay = 0.0
                baseCurrencyDisplay.emit(
                    if (value == 0.0) ""
                    else String.format("%.0f", value)
                )
            }
        }

    private var targetDisplay: Double
        get() = targetCurrencyDisplay.value.toDouble()
        set(value) {
            launch {
                targetCurrencyDisplay.emit(String.format("%.2f", value))
            }
        }

    private lateinit var onCurrencySelectedCallback: suspend (Currency) -> Unit

    private lateinit var currencyListCache: List<Currency>

    private val effectChannel: Channel<CurrencyConverterEffect> = Channel()

    override val coroutineContext: CoroutineContext
        get() = jobTracker + dispatcherProvider.UI()

    init {
        jobTracker = Job()
    }

    override fun onEvent(event: CurrencyConverterEvent) {
        Log.d("TAG", "onEvent: event = $event")
        launch {
            when (event) {
                CurrencyConverterEvent.BaseCurrencyChangeRequested -> handleBaseCurrencyChangeRequested()
                is CurrencyConverterEvent.BaseCurrencyDisplayTextChanged -> handleBaseCurrencyDisplayTextChanged(
                    event.newText
                )
                CurrencyConverterEvent.EvaluatePressed -> handleEvaluatePressed()
                CurrencyConverterEvent.SwitchCurrenciesPressed -> handleSwitchCurrenciesPressed()
                CurrencyConverterEvent.TargetCurrencyChangeRequested -> handleTargetCurrencyChangeRequested()
                is CurrencyConverterEvent.CurrencySelected -> handleCurrencySelected(event.currency)
                CurrencyConverterEvent.PullToRefresh -> handlePullToRefresh()
                is CurrencyConverterEvent.SearchDisplayTextChanged -> handleSearchDisplayTextChanged(
                    event.newText
                )
            }
        }
    }

    private suspend fun handleSearchDisplayTextChanged(newText: String) {
        withLoading {
            searchDisplay.emit(newText)
            currencyList.emit(
                currencyListCache.filter {
                    it.name.contains(newText) || it.code.contains(newText)
                }
            )
        }
    }

    private suspend fun handlePullToRefresh() {
        withLoading {
            val response = withContext(dispatcherProvider.IO()) {
                repository.getAllCurrencies()
            }

            when (response) {
                is ResultWrapper.Failure -> error.emit(response.error.message)
                is ResultWrapper.Success -> currencyListCache = response.result
            }
        }
    }

    private suspend fun handleCurrencySelected(currency: Currency) {
        onCurrencySelectedCallback.invoke(currency)
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

            Log.d("TAG", "handleEvaluatePressed: response = $response")
            Log.d("TAG", "handleEvaluatePressed: base = $baseDisplay , $baseCurrencyDisplay")
            Log.d("TAG", "handleEvaluatePressed: target = $targetDisplay , $targetCurrencyDisplay")

            when (response) {
                is ResultWrapper.Failure -> error.emit(response.error.message)
                is ResultWrapper.Success -> targetDisplay = baseDisplay * response.result.rate
            }
        }
    }


    private suspend fun handleBaseCurrencyDisplayTextChanged(text: String) {
        try {
            if (text.isEmpty()) baseDisplay = 0.0
            baseDisplay = text.toDouble()

        } catch (exp: NumberFormatException) {
            effectChannel.send(CurrencyConverterEffect.ShowToast("Invalid number entered"))
        }
    }

    private suspend fun handleBaseCurrencyChangeRequested() {
        onCurrencySelectedCallback = {
            baseCurrency.emit(it)
        }
    }

    private suspend fun handleTargetCurrencyChangeRequested() {
        onCurrencySelectedCallback = {
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
    override val currencyList = MutableStateFlow<List<Currency>>(listOf())
    override val searchDisplay = MutableStateFlow("")
    override val isLoading = MutableStateFlow(false)
    override val pullToRefreshVisible = MutableStateFlow(false)
    override val error: MutableStateFlow<String?> = MutableStateFlow(null)
    override val effectStream: Flow<CurrencyConverterEffect> = effectChannel.receiveAsFlow()
}