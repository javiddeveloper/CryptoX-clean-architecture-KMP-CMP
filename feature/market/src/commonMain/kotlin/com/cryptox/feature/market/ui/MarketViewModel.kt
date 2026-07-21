/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market.ui

import com.cryptox.core.domain.repository.CoinRepository
import com.cryptox.core.mvi.BaseViewModel
import com.cryptox.feature.market.ui.contract.MarketEffect
import com.cryptox.feature.market.ui.contract.MarketIntent
import com.cryptox.feature.market.ui.contract.MarketUiState
import com.cryptox.feature.market.ui.contract.MarketUiState.PartialState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class MarketViewModel(
    private val repository: CoinRepository,
) : BaseViewModel<MarketUiState, PartialState, MarketEffect, MarketIntent>(
    initialState = MarketUiState(),
) {

    /** Drives the reactive, debounced search stream started by [MarketIntent.Load]. */
    private val queryFlow = MutableStateFlow("")

    override fun handleIntent(intent: MarketIntent): Flow<PartialState> = when (intent) {
        is MarketIntent.Load -> handleLoad()
        is MarketIntent.Refresh -> handleRefresh()
        is MarketIntent.QueryChanged -> handleQueryChanged(intent.query)
        is MarketIntent.CoinClicked -> {
            sendEvent(MarketEffect.NavigateToDetail(intent.id))
            emptyFlow()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun handleLoad(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        var firstEmission = true
        queryFlow
            .debounce { query -> if (query.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
            .flatMapLatest { query -> repository.searchCoins(query) }
            .collect { coins ->
                if (firstEmission) {
                    emit(PartialState.Loading(false))
                    firstEmission = false
                }
                emit(PartialState.Coins(coins))
            }
    }

    private fun handleQueryChanged(query: String): Flow<PartialState> {
        queryFlow.value = query
        return flowOf(PartialState.Query(query))
    }

    private fun handleRefresh(): Flow<PartialState> = flow {
        emit(PartialState.Refreshing(true))
        repository.refreshMarket().onFailure { error ->
            sendEvent(MarketEffect.ShowError(error.message ?: "خطا در بروزرسانی"))
        }
        emit(PartialState.Refreshing(false))
    }

    override fun reduceState(
        currentState: MarketUiState,
        partialState: PartialState,
    ): MarketUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = if (partialState.isLoading) null else currentState.error,
        )

        is PartialState.Refreshing -> currentState.copy(isRefreshing = partialState.isRefreshing)

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isRefreshing = false,
            error = partialState.message,
        )

        is PartialState.Coins -> currentState.copy(
            isLoading = false,
            error = null,
            coins = partialState.coins.toImmutableList(),
        )

        is PartialState.Query -> currentState.copy(query = partialState.query)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
