package com.hardiksachan.currencyconverterjetpackcompose.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object ProductionDispatcherProvider : DispatcherProvider {
    override fun UI(): CoroutineContext {
        return Dispatchers.Main
    }

    override fun IO(): CoroutineContext {
        return Dispatchers.IO
    }

}