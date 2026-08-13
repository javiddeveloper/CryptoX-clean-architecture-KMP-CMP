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
import com.cryptox.feature.detail.ui.contract.ChartStyle
import com.cryptox.feature.detail.ui.contract.DetailUiState
import com.cryptox.feature.detail.ui.contract.DetailUiState.PartialState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import cryptox.feature.detail.generated.resources.Res
import cryptox.feature.detail.generated.resources.detail_chart_load_failed
import org.jetbrains.compose.resources.getString

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

        is DetailIntent.ChartStyleChanged -> handleChartStyleChanged(intent.style)

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
                emitAll(loadSeries(id, currentRange(), currentStyle()))
            },
            onFailure = { error ->
                emit(PartialState.Error(error.message))
            },
        )
    }

    private fun handleRangeChanged(range: ChartRange): Flow<PartialState> = flow {
        // RangeSelected also drops both cached series, so a stale range never renders.
        emit(PartialState.RangeSelected(range))
        val id = coinId ?: return@flow
        emitAll(loadSeries(id, range, currentStyle()))
    }

    /**
     * Switching style only costs a fetch the first time that series is needed for the
     * current range; afterwards both are cached and toggling is instant.
     */
    private fun handleChartStyleChanged(style: ChartStyle): Flow<PartialState> = flow {
        emit(PartialState.ChartStyleSelected(style))
        if (!isSeriesEmpty(style)) return@flow
        val id = coinId ?: return@flow
        emitAll(loadSeries(id, currentRange(), style))
    }

    private fun loadSeries(id: String, range: ChartRange, style: ChartStyle): Flow<PartialState> = flow {
        emit(PartialState.ChartLoading(true))
        when (style) {
            ChartStyle.LINE -> repository.getChart(id, range).fold(
                onSuccess = { points -> emit(PartialState.ChartLoaded(points)) },
                onFailure = { error -> notifyChartFailure(error) },
            )

            ChartStyle.CANDLE -> repository.getCandles(id, range).fold(
                onSuccess = { candles -> emit(PartialState.CandlesLoaded(candles)) },
                onFailure = { error -> notifyChartFailure(error) },
            )
        }
        emit(PartialState.ChartLoading(false))
    }

    private suspend fun notifyChartFailure(error: Throwable) {
        sendEvent(
            DetailEffect.ShowError(
                error.message ?: getString(Res.string.detail_chart_load_failed),
            ),
        )
    }

    private fun currentRange(): ChartRange = uiState.value.selectedRange

    private fun currentStyle(): ChartStyle = uiState.value.chartStyle

    private fun isSeriesEmpty(style: ChartStyle): Boolean = when (style) {
        ChartStyle.LINE -> uiState.value.chart.isEmpty()
        ChartStyle.CANDLE -> uiState.value.candles.isEmpty()
    }

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

        is PartialState.CandlesLoaded -> currentState.copy(
            candles = partialState.candles.toImmutableList(),
        )

        // A new range invalidates both cached series.
        is PartialState.RangeSelected -> currentState.copy(
            selectedRange = partialState.range,
            chart = persistentListOf(),
            candles = persistentListOf(),
        )

        is PartialState.ChartStyleSelected -> currentState.copy(chartStyle = partialState.style)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
