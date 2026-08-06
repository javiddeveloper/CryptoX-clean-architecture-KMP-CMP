/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cryptox.core.designsystem.components.CryptoXCoinListItem
import com.cryptox.core.designsystem.components.CryptoXEmptyState
import com.cryptox.core.designsystem.components.CryptoXErrorState
import com.cryptox.core.designsystem.components.CryptoXLoadingShimmer
import com.cryptox.core.designsystem.components.CryptoXScaffold
import com.cryptox.core.designsystem.components.CryptoXSearchField
import com.cryptox.core.designsystem.components.CryptoXTopBar
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import com.cryptox.feature.market.ui.contract.MarketEffect
import com.cryptox.feature.market.ui.contract.MarketIntent
import com.cryptox.feature.market.ui.contract.MarketUiState
import cryptox.feature.market.generated.resources.Res
import cryptox.feature.market.generated.resources.market_coin_count
import cryptox.feature.market.generated.resources.market_empty_list_subtitle
import cryptox.feature.market.generated.resources.market_empty_list_title
import cryptox.feature.market.generated.resources.market_empty_search_subtitle
import cryptox.feature.market.generated.resources.market_empty_search_title
import cryptox.feature.market.generated.resources.market_search_placeholder
import cryptox.feature.market.generated.resources.market_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val CURRENCY_SYMBOL = "$"

/**
 * Entry point Agent 3 wires into navigation. Owns the ViewModel and translates
 * one-off [MarketEffect]s into navigation / snackbars.
 */
@Composable
fun MarketRoute(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MarketViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(MarketIntent.Load)
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collect { effect ->
            when (effect) {
                is MarketEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
                is MarketEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    MarketScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    state: MarketUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (MarketIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CryptoXScaffold(
        topBar = { CryptoXTopBar(title = stringResource(Res.string.market_title)) },
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                CryptoXSearchField(
                    query = state.query,
                    onQueryChange = { onIntent(MarketIntent.QueryChanged(it)) },
                    placeholder = stringResource(Res.string.market_search_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = CryptoXSpacing.pageHorizontal,
                            vertical = CryptoXSpacing.sm,
                        ),
                )

                when {
                    state.isLoading -> LoadingList()

                    state.error != null -> CryptoXErrorState(
                        message = state.error,
                        onRetry = { onIntent(MarketIntent.Load) },
                        modifier = Modifier.fillMaxSize(),
                    )

                    state.coins.isEmpty() && state.query.isNotBlank() -> CryptoXEmptyState(
                        title = stringResource(Res.string.market_empty_search_title),
                        subtitle = stringResource(Res.string.market_empty_search_subtitle, state.query),
                        modifier = Modifier.fillMaxSize(),
                    )

                    state.isEmpty -> CryptoXEmptyState(
                        title = stringResource(Res.string.market_empty_list_title),
                        subtitle = stringResource(Res.string.market_empty_list_subtitle),
                        modifier = Modifier.fillMaxSize(),
                    )

                    else -> PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onIntent(MarketIntent.Refresh) },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CoinList(state, onIntent)
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private val CoinRowHeight = 84.dp

@Composable
private fun CoinList(
    state: MarketUiState,
    onIntent: (MarketIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = CryptoXSpacing.pageHorizontal,
            vertical = CryptoXSpacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm),
    ) {
        item {
            Text(
                text = stringResource(Res.string.market_coin_count, state.coins.size),
                style = MaterialTheme.typography.labelLarge,
                color = LocalCryptoXColors.current.textMuted,
                modifier = Modifier.padding(
                    horizontal = CryptoXSpacing.xs,
                    vertical = CryptoXSpacing.xs,
                ),
            )
        }
        items(items = state.coins, key = { it.id }) { coin ->
            CryptoXCoinListItem(
                iconUrl = coin.iconUrl,
                name = coin.name,
                symbol = coin.symbol,
                price = coin.price,
                currencySymbol = CURRENCY_SYMBOL,
                changePercent = coin.changePercent24h,
                sparkline = coin.sparkline7d,
                onClick = { onIntent(MarketIntent.CoinClicked(coin.id)) },
            )
        }
    }
}

@Composable
private fun LoadingList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = CryptoXSpacing.pageHorizontal,
                vertical = CryptoXSpacing.sm,
            ),
        verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm),
    ) {
        repeat(8) {
            CryptoXLoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CoinRowHeight)
                    .clip(RoundedCornerShape(CornerRadius.card)),
            )
        }
    }
}
