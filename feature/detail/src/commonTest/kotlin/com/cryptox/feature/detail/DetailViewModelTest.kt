/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail

import com.cryptox.core.data.fake.FakeCoinRepository
import com.cryptox.core.domain.model.ChartRange
import com.cryptox.feature.detail.ui.DetailViewModel
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
    fun load_populatesDetailAndChart() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("bitcoin"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.detail)
        assertEquals("bitcoin", state.detail?.coin?.id)
        assertTrue(state.chart.isNotEmpty(), "chart should be loaded after detail")
    }

    @Test
    fun rangeChanged_reloadsChartForNewRange() = runTest(dispatcher) {
        val viewModel = DetailViewModel(repository())

        viewModel.sendIntent(DetailIntent.Load("ethereum"))
        advanceUntilIdle()

        viewModel.sendIntent(DetailIntent.RangeChanged(ChartRange.DAY))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ChartRange.DAY, state.selectedRange)
        // DAY range produces 24 hourly samples in the fake repository.
        assertEquals(24, state.chart.size)
    }
}
