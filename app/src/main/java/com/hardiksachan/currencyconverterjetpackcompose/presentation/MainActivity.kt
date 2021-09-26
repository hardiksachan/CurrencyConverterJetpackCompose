package com.hardiksachan.currencyconverterjetpackcompose.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.lifecycleScope
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterEffect
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterLogic
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.HomePageState
import com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter.HomePage
import com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter.di.viewModel
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.AppTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val snackbarHostState = SnackbarHostState()

        val state: HomePageState = viewModel
        val logic: CurrencyConverterLogic = viewModel

        setContent {
            AppTheme {
                HomePage(
                    state = state,
                    logic = logic,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        lifecycleScope.launch {
            state.effectStream.collect {
                when (it) {
                    is CurrencyConverterEffect.ShowToast -> snackbarHostState.showSnackbar(
                        it.message
                    )
                }
            }
        }
    }
}
