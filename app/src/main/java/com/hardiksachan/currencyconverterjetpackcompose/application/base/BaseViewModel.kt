package com.hardiksachan.currencyconverterjetpackcompose.application.base

import com.hardiksachan.currencyconverterjetpackcompose.common.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface UIState
interface UIEvent
interface UIEffect

abstract class BaseViewModel<Event : UIEvent, State : UIState, Effect : UIEffect>(
    protected val dispatcherProvider: DispatcherProvider
) : CoroutineScope {

    private val jobTracker: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = jobTracker + dispatcherProvider.provideUIContext()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        launch {
            event.collect() {
                handleEvent(it)
            }
        }
    }

    abstract fun handleEvent(event: Event)


    private val initialState: State by lazy {
        createInitialState()
    }

    abstract fun createInitialState(): State

    val currentState: State
        get() = uiState.value


    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()


    fun setEvent(event: Event) {
        val newEvent = event
        launch { _event.emit(newEvent) }
    }

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        launch { _effect.send(effectValue) }
    }

}