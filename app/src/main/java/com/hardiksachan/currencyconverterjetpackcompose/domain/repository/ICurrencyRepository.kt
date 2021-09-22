package com.hardiksachan.currencyconverterjetpackcompose.domain.repository

import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.ConversionFactor
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency

interface ICurrencyRepository {

    suspend fun getAllCurrencies(): ResultWrapper<Exception, List<Currency>>

    suspend fun getConversionFactor(
        baseCurrency: Currency,
        targetCurrency: Currency
    ): ResultWrapper<Exception, ConversionFactor>

}