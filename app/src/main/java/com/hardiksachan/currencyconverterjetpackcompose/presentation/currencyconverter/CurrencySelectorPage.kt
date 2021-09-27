package com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterLogic
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencySelectorPageState

@Composable
fun CurrencySelectorPage(
    state: CurrencySelectorPageState,
    logic: CurrencyConverterLogic,
    snackbarHostState: SnackbarHostState,
) {
    val searchDisplay = state.searchDisplay.collectAsState()
    val currencyList = state.currencyList.collectAsState()

    val error = state.error.collectAsState()
}