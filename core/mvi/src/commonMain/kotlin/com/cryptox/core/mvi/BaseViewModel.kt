/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

/**
 * Unidirectional MVI base class shared by every CryptoX feature.
 *
 * The contract, mirrored from the reference project (TaminHamrahCMP):
 *  - The UI sends [INTENT]s via [sendIntent].
 *  - [handleIntent] turns each intent into a stream of [PARTIAL_STATE]s.
 *  - [reduceState] folds partial states into the immutable [STATE] exposed by [uiState].
 *  - One-off navigation / messages are emitted as [EVENT]s via [events].
 */
abstract class BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>(
    initialState: STATE,
) : ViewModel() {

    private val intentChannel = Channel<INTENT>(Channel.UNLIMITED)

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    private val eventChannel = Channel<EVENT>(Channel.BUFFERED)
    val events: Flow<EVENT> = eventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeIntents() {
        viewModelScope.launch {
            intentChannel.receiveAsFlow()
                .flatMapMerge { intent ->
                    handleIntent(intent)
                        .catch { error ->
                            emit(createErrorState(error.message ?: "Unknown error"))
                        }
                }
                .scan(uiState.value) { currentState, partialState ->
                    reduceState(currentState, partialState)
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    init {
        observeIntents()
    }

    fun sendIntent(intent: INTENT) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    protected fun sendEvent(event: EVENT) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    protected abstract fun handleIntent(intent: INTENT): Flow<PARTIAL_STATE>
    protected abstract fun reduceState(currentState: STATE, partialState: PARTIAL_STATE): STATE
    protected abstract fun createErrorState(message: String): PARTIAL_STATE
}
