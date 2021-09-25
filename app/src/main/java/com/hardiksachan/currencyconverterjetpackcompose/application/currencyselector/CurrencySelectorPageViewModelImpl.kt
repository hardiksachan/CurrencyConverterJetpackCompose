package com.hardiksachan.currencyconverterjetpackcompose.application.currencyselector

import com.hardiksachan.currencyconverterjetpackcompose.application.BaseLogic
import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider
import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.domain.repository.ICurrencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CurrencySelectorPageViewModelImpl(
    private val repository: ICurrencyRepository,
    private val effectHandler: ICurrencySelectorPageUI.EffectHandler,
    private val dispatcherProvider: DispatcherProvider
) :
    BaseLogic<CurrencySelectorPageEvent>(),
    ICurrencySelectorPageUI.State,
    CoroutineScope {

    private lateinit var currencyListCache: List<Currency>

    override val coroutineContext: CoroutineContext
        get() = jobTracker + dispatcherProvider.UI()

    init {
        jobTracker = Job()
    }

    override fun onEvent(event: CurrencySelectorPageEvent) {
        launch(dispatcherProvider.UI()) {
            when (event) {
                is CurrencySelectorPageEvent.CurrencySelected -> handleCurrencySelected(event.currency)
                CurrencySelectorPageEvent.PullToRefresh -> handlePullToRefresh()
                is CurrencySelectorPageEvent.SearchDisplayTextChanged -> handleSearchDisplayTextChanged(
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

    private fun handleCurrencySelected(currency: Currency) {
        TODO("Not yet implemented")
    }

    private suspend fun withLoading(f: suspend () -> Unit) {
        isLoading.emit(true)

        f.invoke()

        isLoading.emit(false)
    }

    // Regarding UI State

    override val currencyList = MutableStateFlow<List<Currency>>(listOf())
    override val searchDisplay = MutableStateFlow("")
    override val isLoading = MutableStateFlow(true)
    override val pullToRefreshVisible = MutableStateFlow(false)
    override val error = MutableStateFlow<String?>(null)
}