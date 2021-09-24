package com.hardiksachan.currencyconverterjetpackcompose.application.home

import com.hardiksachan.currencyconverterjetpackcompose.R
import com.hardiksachan.currencyconverterjetpackcompose.application.base.BaseViewModel
import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider
import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.common.inrCurrency
import com.hardiksachan.currencyconverterjetpackcompose.common.usdCurrency
import com.hardiksachan.currencyconverterjetpackcompose.domain.repository.ICurrencyRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: ICurrencyRepository,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel<CurrencyConverterUI.Event, CurrencyConverterUI.State, CurrencyConverterUI.Effect>(
    dispatcherProvider
) {

    override fun handleEvent(event: CurrencyConverterUI.Event) {
        when (event) {
            CurrencyConverterUI.Event.BaseCurrencyChangeStarted -> setState {
                copy(
                    isLoading = true
                )
            }
            is CurrencyConverterUI.Event.BaseCurrencyChanged -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
                when (event.updatedCurrency) {
                    is ResultWrapper.Failure -> setEffect {
                        CurrencyConverterUI.Effect.ShowError(
                            message = event.updatedCurrency.error.localizedMessage
                                ?: R.string.generic_error_message.toString()
                        )
                    }
                    is ResultWrapper.Success -> setState {
                        copy(
                            baseCurrency = event.updatedCurrency.result
                        )
                    }
                }
            }
            is CurrencyConverterUI.Event.BaseCurrencyDisplayTextChanged -> setState {
                copy(
                    baseCurrencyDisplay = event.newText
                )
            }
            CurrencyConverterUI.Event.EvaluatePressed -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                launch {
                    withContext(dispatcherProvider.provideIOContext()) {
                        val conversionFactor = repository.getConversionFactor(
                            currentState.baseCurrency,
                            currentState.targetCurrency
                        )
                        when (conversionFactor) {
                            is ResultWrapper.Failure -> setEffect {
                                CurrencyConverterUI.Effect.ShowError(
                                    conversionFactor.error.localizedMessage
                                        ?: R.string.generic_error_message.toString()
                                )
                            }
                            is ResultWrapper.Success -> setState {
                                copy(
                                    targetCurrencyDisplay = String.format(
                                        "%.2f",
                                        baseCurrencyDisplay.toDouble() * conversionFactor.result.rate
                                    )
                                )
                            }
                        }
                    }
                }

                setState {
                    copy(
                        isLoading = false
                    )
                }


            }
            CurrencyConverterUI.Event.SwitchCurrenciesPressed -> TODO()
            CurrencyConverterUI.Event.TargetCurrencyChangeStarted -> TODO()
            is CurrencyConverterUI.Event.TargetCurrencyChanged -> TODO()
            CurrencyConverterUI.Event.UIStarted -> TODO()
        }
    }

    override fun createInitialState() = CurrencyConverterUI.State(
        baseCurrency = usdCurrency,
        targetCurrency = inrCurrency,
        baseCurrencyDisplay = "1.00",
        targetCurrencyDisplay = "",
        isLoading = false
    )
}