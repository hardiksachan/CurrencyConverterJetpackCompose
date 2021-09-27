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

    private lateinit var onCurrencySelectedCallback: suspend (Currency) -> Unit

    private var currencyListCache: List<Currency> = listOf()

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
                CurrencyConverterEvent.OnStop -> jobTracker.cancel()
            }
        }
    }

    private suspend fun handleSearchDisplayTextChanged(newText: String) {
        withLoading {
            searchDisplay.emit(newText)
            currencyList.emit(
                currencyListCache.filter {
                    it.name.contains(newText, ignoreCase = true) || it.code.contains(
                        newText,
                        ignoreCase = true
                    )
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
            val oldBaseCurrencyDisplay = baseCurrencyDisplay.value

            baseCurrency.emit(targetCurrency.value)
            baseCurrencyDisplay.emit(targetCurrencyDisplay.value)

            targetCurrency.emit(oldBaseCurrency)
            targetCurrencyDisplay.emit(oldBaseCurrencyDisplay)
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
                is ResultWrapper.Success -> {
                    when (val baseAmount = baseCurrencyDisplay.value.getDouble()) {
                        is ResultWrapper.Failure -> {
                            targetCurrencyDisplay.emit("")
                            effectChannel.send(
                                CurrencyConverterEffect.ShowToast(
                                    "Invalid base currency amount"
                                )
                            )
                        }
                        is ResultWrapper.Success -> targetCurrencyDisplay.emit(
                            String.format("%.2f", baseAmount.result * response.result.rate)
                        )
                    }
                }
            }
        }
    }


    private suspend fun handleBaseCurrencyDisplayTextChanged(text: String) {
        baseCurrencyDisplay.emit(text)
        targetCurrencyDisplay.emit("")
    }

    private suspend fun handleBaseCurrencyChangeRequested() {
        refreshCurrencyCache()
        resetSearch()
        onCurrencySelectedCallback = {
            baseCurrency.emit(it)
        }
    }

    private suspend fun handleTargetCurrencyChangeRequested() {
        refreshCurrencyCache()
        resetSearch()
        onCurrencySelectedCallback = {
            targetCurrency.emit(it)
        }
    }

    private suspend fun refreshCurrencyCache() {
        withContext(dispatcherProvider.IO()) {
            when (val response = repository.getAllCurrencies()) {
                is ResultWrapper.Failure -> effectChannel.send(
                    CurrencyConverterEffect.ShowToast(
                        response.error.localizedMessage ?: "Unknown error occurred"
                    )
                )
                is ResultWrapper.Success -> {
                    currencyListCache = response.result
                }
            }
        }
    }

    private suspend fun resetSearch() {
        searchDisplay.emit("")
        currencyList.emit(currencyListCache)
    }

    private suspend fun withLoading(f: suspend () -> Unit) {
        isLoading.emit(true)

        f.invoke()

        isLoading.emit(false)
    }

    private fun String.getDouble(): ResultWrapper<NumberFormatException, Double> {
        val str = this.trim()

        return if (str.isEmpty()) {
            ResultWrapper.Success(0.0)
        } else {
            try {
                ResultWrapper.Success(str.toDouble())
            } catch (exp: NumberFormatException) {
                ResultWrapper.Failure(exp)
            }
        }
    }

// Regarding UI State

    override val baseCurrency: MutableStateFlow<Currency> = MutableStateFlow(usdCurrency)
    override val targetCurrency: MutableStateFlow<Currency> = MutableStateFlow(inrCurrency)
    override val baseCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("")
    override val targetCurrencyDisplay: MutableStateFlow<String> = MutableStateFlow("")
    override val currencyList = MutableStateFlow<List<Currency>>(listOf())
    override val searchDisplay = MutableStateFlow("")
    override val isLoading = MutableStateFlow(false)
    override val pullToRefreshVisible = MutableStateFlow(false)
    override val error: MutableStateFlow<String?> = MutableStateFlow(null)
    override val effectStream: Flow<CurrencyConverterEffect> = effectChannel.receiveAsFlow()
}