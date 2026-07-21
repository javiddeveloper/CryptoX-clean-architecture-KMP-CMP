/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui

import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.repository.CoinRepository
import com.cryptox.core.mvi.BaseViewModel
import com.cryptox.feature.detail.ui.contract.DetailEffect
import com.cryptox.feature.detail.ui.contract.DetailIntent
import com.cryptox.feature.detail.ui.contract.DetailUiState
import com.cryptox.feature.detail.ui.contract.DetailUiState.PartialState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class DetailViewModel(
    private val repository: CoinRepository,
) : BaseViewModel<DetailUiState, PartialState, DetailEffect, DetailIntent>(
    initialState = DetailUiState(),
) {

    private var coinId: String? = null

    override fun handleIntent(intent: DetailIntent): Flow<PartialState> = when (intent) {
        is DetailIntent.Load -> {
            coinId = intent.coinId
            handleLoad(intent.coinId)
        }

        is DetailIntent.RangeChanged -> handleRangeChanged(intent.range)

        is DetailIntent.Retry -> coinId?.let(::handleLoad) ?: emptyFlow()

        is DetailIntent.BackClicked -> {
            sendEvent(DetailEffect.NavigateBack)
            emptyFlow()
        }
    }

    private fun handleLoad(id: String): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        val detailResult = repository.getCoinDetail(id)
        detailResult.fold(
            onSuccess = { detail ->
                emit(PartialState.DetailLoaded(detail))
                emit(PartialState.Loading(false))
                emitAll(loadChart(id, currentRange()))
            },
            onFailure = { error ->
                emit(PartialState.Error(error.message))
            },
        )
    }

    private fun handleRangeChanged(range: ChartRange): Flow<PartialState> = flow {
        emit(PartialState.RangeSelected(range))
        val id = coinId ?: return@flow
        emitAll(loadChart(id, range))
    }

    private fun loadChart(id: String, range: ChartRange): Flow<PartialState> = flow {
        emit(PartialState.ChartLoading(true))
        repository.getChart(id, range).fold(
            onSuccess = { points -> emit(PartialState.ChartLoaded(points)) },
            onFailure = { error ->
                sendEvent(DetailEffect.ShowError(error.message ?: "خطا در دریافت نمودار"))
            },
        )
        emit(PartialState.ChartLoading(false))
    }

    private fun currentRange(): ChartRange = uiState.value.selectedRange

    override fun reduceState(
        currentState: DetailUiState,
        partialState: PartialState,
    ): DetailUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = if (partialState.isLoading) null else currentState.error,
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isChartLoading = false,
            error = partialState.message,
        )

        is PartialState.DetailLoaded -> currentState.copy(
            detail = partialState.detail,
            error = null,
        )

        is PartialState.ChartLoading -> currentState.copy(isChartLoading = partialState.isChartLoading)

        is PartialState.ChartLoaded -> currentState.copy(
            chart = partialState.points.toImmutableList(),
        )

        is PartialState.RangeSelected -> currentState.copy(selectedRange = partialState.range)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
