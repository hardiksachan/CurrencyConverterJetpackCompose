package com.hardiksachan.currencyconverterjetpackcompose.common

sealed class ResultWrapper<out E, out V> {

    data class Success<out V>(val result: V) : ResultWrapper<Nothing, V>()
    data class Failure<out E>(val error: E) : ResultWrapper<E, Nothing>()

    companion object Factory {

        inline fun <V> build(function: () -> V) = try {
            Success(function.invoke())
        } catch (e: Exception) {
            Failure(e)
        }

    }

}