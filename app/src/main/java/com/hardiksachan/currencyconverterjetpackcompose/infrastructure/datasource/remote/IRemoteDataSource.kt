package com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote

import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.dto.PairConversion
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.dto.SupportedCodes

interface IRemoteDataSource {
    suspend fun getAllCurrencies(): ResultWrapper<Exception, SupportedCodes>

    suspend fun getConversionFactor(
        baseCurrency: Currency,
        targetCurrency: Currency,
    ): ResultWrapper<Exception, PairConversion>
}