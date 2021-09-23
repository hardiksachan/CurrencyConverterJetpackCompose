package com.hardiksachan.currencyconverterjetpackcompose.infrastructure.repository

import com.hardiksachan.currencyconverterjetpackcompose.common.ResultWrapper
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.ConversionFactor
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.domain.repository.ICurrencyRepository
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.IRemoteDataSource
import com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.dto.toDomain

class CurrencyRepositoryImpl(
    private val remoteDataSource: IRemoteDataSource
) : ICurrencyRepository {

    override suspend fun getAllCurrencies(): ResultWrapper<Exception, List<Currency>> =
        when (val response = remoteDataSource.getAllCurrencies()) {
            is ResultWrapper.Failure -> response
            is ResultWrapper.Success -> ResultWrapper.build { response.result.toDomain() }
        }

    override suspend fun getConversionFactor(
        baseCurrency: Currency,
        targetCurrency: Currency
    ): ResultWrapper<Exception, ConversionFactor> =
        when (val response = remoteDataSource.getConversionFactor(baseCurrency, targetCurrency)) {
            is ResultWrapper.Failure -> response
            is ResultWrapper.Success -> ResultWrapper.build {
                response.result.toDomain(baseCurrency, targetCurrency)
            }

        }
}