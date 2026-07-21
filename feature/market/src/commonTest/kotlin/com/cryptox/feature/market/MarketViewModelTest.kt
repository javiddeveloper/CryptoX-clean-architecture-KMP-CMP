/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market

import app.cash.turbine.test
import com.cryptox.core.data.fake.FakeCoinRepository
import com.cryptox.feature.market.ui.MarketViewModel
import com.cryptox.feature.market.ui.contract.MarketEffect
import com.cryptox.feature.market.ui.contract.MarketIntent
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MarketViewModelTest {

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
    fun load_success_populatesCoinsAndClearsLoading() = runTest(dispatcher) {
        val viewModel = MarketViewModel(repository())

        viewModel.sendIntent(MarketIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.coins.isNotEmpty(), "expected seeded coins")
        assertEquals(null, state.error)
    }

    @Test
    fun queryChanged_filtersCoins() = runTest(dispatcher) {
        val viewModel = MarketViewModel(repository())

        viewModel.sendIntent(MarketIntent.Load)
        advanceUntilIdle()

        viewModel.sendIntent(MarketIntent.QueryChanged("bit"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("bit", state.query)
        assertTrue(state.coins.isNotEmpty())
        assertTrue(
            state.coins.all {
                it.name.contains("bit", ignoreCase = true) ||
                    it.symbol.contains("bit", ignoreCase = true)
            },
            "all results should match the query",
        )
    }

    @Test
    fun refresh_failure_emitsShowErrorEffect() = runTest(dispatcher) {
        val repository = repository().apply { failNextRefresh = true }
        val viewModel = MarketViewModel(repository)

        viewModel.sendIntent(MarketIntent.Load)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(MarketIntent.Refresh)
            advanceUntilIdle()
            assertIs<MarketEffect.ShowError>(awaitItem())
        }
        assertFalse(viewModel.uiState.value.isRefreshing)
    }
}
