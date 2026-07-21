/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market.ui.contract

import androidx.compose.runtime.Immutable
import com.cryptox.core.domain.model.Coin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MarketUiState(
    val coins: ImmutableList<Coin> = persistentListOf(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val query: String = "",
    val error: String? = null,
) {
    /** True only when the initial load produced nothing (not while typing a search). */
    val isEmpty: Boolean get() = !isLoading && error == null && coins.isEmpty()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Refreshing(val isRefreshing: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Coins(val coins: List<Coin>) : PartialState
        data class Query(val query: String) : PartialState
    }
}

sealed interface MarketIntent {
    data object Load : MarketIntent
    data object Refresh : MarketIntent
    data class QueryChanged(val query: String) : MarketIntent
    data class CoinClicked(val id: String) : MarketIntent
}

sealed interface MarketEffect {
    data class NavigateToDetail(val id: String) : MarketEffect
    data class ShowError(val message: String) : MarketEffect
}
