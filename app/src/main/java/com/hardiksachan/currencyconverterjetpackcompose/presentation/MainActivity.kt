package com.hardiksachan.currencyconverterjetpackcompose.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter.HomePage
import com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter.di.viewModel
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                HomePage(state = viewModel, logic = viewModel)
            }
        }
    }
}
