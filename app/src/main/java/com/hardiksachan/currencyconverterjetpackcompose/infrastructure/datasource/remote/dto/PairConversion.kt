package com.hardiksachan.currencyconverterjetpackcompose.infrastructure.datasource.remote.dto

import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.ConversionFactor
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairConversion(
    @SerialName("base_code") val baseCode: String,
    @SerialName("conversion_rate") val conversionRate: Double,
    val documentation: String,
    val result: String,
    @SerialName("target_code") val targetCode: String,
    @SerialName("terms_of_use") val termsOfUse: String,
    @SerialName("time_last_update_unix") val timeLastUpdateUnix: Long,
    @SerialName("time_last_update_utc") val timeLastUpdateUtc: String,
    @SerialName("time_next_update_unix") val timeNextUpdateUnix: Long,
    @SerialName("time_next_update_utc") val timeNextUpdateUtc: String
)

fun PairConversion.toDomain(
    baseCurrency: Currency,
    targetCurrency: Currency
) = ConversionFactor(
    baseCurrency,
    targetCurrency,
    conversionRate,
    Instant.fromEpochMilliseconds(timeLastUpdateUnix)
)
