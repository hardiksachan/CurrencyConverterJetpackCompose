package com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter.di

import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterViewModelImpl
import com.hardiksachan.currencyconverterjetpackcompose.common.ProductionDispatcherProvider
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.RemoteDataSourceImpl
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.repository.CurrencyRepositoryImpl

val viewModel = CurrencyConverterViewModelImpl(
    CurrencyRepositoryImpl(
        RemoteDataSourceImpl(
            baseUrl = "https://v6.exchangerate-api.com/v6/f26039575e84dab9918243b2",
            dispatcherProvider = ProductionDispatcherProvider
        )
    ),
    ProductionDispatcherProvider
)