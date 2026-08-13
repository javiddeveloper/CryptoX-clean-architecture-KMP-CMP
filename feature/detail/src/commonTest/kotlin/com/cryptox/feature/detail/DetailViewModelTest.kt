/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail

import com.cryptox.core.data.fake.FakeCoinRepository
import com.cryptox.core.domain.model.ChartRange
import com.cryptox.feature.detail.ui.DetailViewModel
import com.cryptox.feature.detail.ui.contract.ChartStyle
import com.cryptox.feature.detail.ui.contract.DetailIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun repository() = FakeCoinRepository(networkDelayMs = 0)

    @Test
    fun load_populatesDetailAndCandles() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("bitcoin"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.detail)
        assertEquals("bitcoin", state.detail?.coin?.id)
        // CANDLE is the default style, so only that series is fetched up front.
        assertEquals(ChartStyle.CANDLE, state.chartStyle)
        assertTrue(state.candles.isNotEmpty(), "candles should be loaded after detail")
        assertTrue(state.chart.isEmpty(), "line series should stay unfetched until requested")
    }

    @Test
    fun generatedCandles_holdOhlcInvariants() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("bitcoin"))
        advanceUntilIdle()

        val candles = viewModel.uiState.value.candles
        assertTrue(candles.isNotEmpty())
        candles.forEach { candle ->
            assertTrue(
                candle.high >= maxOf(candle.open, candle.close),
                "high must cap the body: $candle",
            )
            assertTrue(
                candle.low <= minOf(candle.open, candle.close),
                "low must floor the body: $candle",
            )
        }
        // The walk is continuous: each candle opens where the previous one closed.
        candles.zipWithNext { previous, next ->
            assertEquals(previous.close, next.open, "candles must form a continuous series")
        }
    }

    @Test
    fun rangeChanged_reloadsSeriesForNewRange() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("ethereum"))
        advanceUntilIdle()

        viewModel.sendIntent(DetailIntent.RangeChanged(ChartRange.DAY))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ChartRange.DAY, state.selectedRange)
        // DAY range produces 24 hourly bars in the fake repository.
        assertEquals(24, state.candles.size)
    }

    @Test
    fun chartStyleChanged_fetchesLineSeriesAndKeepsCandlesCached() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("solana"))
        advanceUntilIdle()

        viewModel.sendIntent(DetailIntent.ChartStyleChanged(ChartStyle.LINE))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ChartStyle.LINE, state.chartStyle)
        assertTrue(state.chart.isNotEmpty(), "line series should load on first switch")
        assertTrue(state.candles.isNotEmpty(), "candles stay cached so toggling back is instant")
    }

    @Test
    fun rangeChanged_dropsStaleSeriesFromPreviousRange() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("bitcoin"))
        advanceUntilIdle()
        viewModel.sendIntent(DetailIntent.ChartStyleChanged(ChartStyle.LINE))
        advanceUntilIdle()

        // Both series are cached for WEEK at this point; moving to YEAR must invalidate
        // the one we are not actively showing rather than leaving it stale.
        viewModel.sendIntent(DetailIntent.RangeChanged(ChartRange.YEAR))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(52, state.chart.size, "line series reloads for the new range")
        assertTrue(state.candles.isEmpty(), "stale candles from the old range must be dropped")
    }
}
