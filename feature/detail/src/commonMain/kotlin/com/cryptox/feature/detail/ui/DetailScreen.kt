/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cryptox.core.designsystem.components.CryptoXCard
import com.cryptox.core.designsystem.components.CryptoXChangeBadge
import com.cryptox.core.designsystem.components.CryptoXErrorState
import com.cryptox.core.designsystem.components.CryptoXLoadingShimmer
import com.cryptox.core.designsystem.components.CryptoXPriceText
import com.cryptox.core.designsystem.components.CryptoXScaffold
import com.cryptox.core.designsystem.components.CryptoXTopBar
import com.cryptox.core.designsystem.theme.CryptoXShapes
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.model.CoinDetail
import com.cryptox.feature.detail.ui.contract.DetailEffect
import com.cryptox.feature.detail.ui.contract.DetailIntent
import com.cryptox.feature.detail.ui.contract.DetailUiState
import cryptox.feature.detail.generated.resources.Res
import cryptox.feature.detail.generated.resources.detail_about
import cryptox.feature.detail.generated.resources.detail_no_chart_data
import cryptox.feature.detail.generated.resources.detail_range_day
import cryptox.feature.detail.generated.resources.detail_range_month
import cryptox.feature.detail.generated.resources.detail_range_week
import cryptox.feature.detail.generated.resources.detail_range_year
import cryptox.feature.detail.generated.resources.detail_stat_high_24h
import cryptox.feature.detail.generated.resources.detail_stat_low_24h
import cryptox.feature.detail.generated.resources.detail_stat_market_cap
import cryptox.feature.detail.generated.resources.detail_stat_volume_24h
import cryptox.feature.detail.generated.resources.detail_title_fallback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val CURRENCY_SYMBOL = "$"

/** Range chips shown above the chart, mapped to domain [ChartRange]. */
private val RANGE_LABEL_RES = listOf(
    ChartRange.DAY to Res.string.detail_range_day,
    ChartRange.WEEK to Res.string.detail_range_week,
    ChartRange.MONTH to Res.string.detail_range_month,
    ChartRange.YEAR to Res.string.detail_range_year,
)

@Composable
fun DetailRoute(
    coinId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(coinId) {
        viewModel.sendIntent(DetailIntent.Load(coinId))
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collect { effect ->
            when (effect) {
                is DetailEffect.NavigateBack -> onBack()
                is DetailEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    DetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        modifier = modifier,
    )
}

@Composable
fun DetailScreen(
    state: DetailUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (DetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CryptoXScaffold(
        topBar = {
            CryptoXTopBar(
                title = state.detail?.coin?.name ?: stringResource(Res.string.detail_title_fallback),
                onBack = { onIntent(DetailIntent.BackClicked) },
            )
        },
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading && state.detail == null -> DetailLoading()

                state.error != null && state.detail == null -> CryptoXErrorState(
                    message = state.error,
                    onRetry = { onIntent(DetailIntent.Retry) },
                    modifier = Modifier.fillMaxSize(),
                )

                state.detail != null -> DetailContent(state, onIntent)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun DetailContent(
    state: DetailUiState,
    onIntent: (DetailIntent) -> Unit,
) {
    val detail = state.detail ?: return
    val colors = LocalCryptoXColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = CryptoXSpacing.pageHorizontal),
        verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.lg),
    ) {
        Spacer(Modifier.height(CryptoXSpacing.sm))

        // ── Header: icon + name/symbol + price + change ────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = detail.coin.symbol.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
            }
            Spacer(Modifier.width(CryptoXSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.coin.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
                Text(
                    text = detail.coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                CryptoXPriceText(
                    price = detail.coin.price,
                    currencySymbol = CURRENCY_SYMBOL,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(CryptoXSpacing.xs))
                CryptoXChangeBadge(changePercent = detail.coin.changePercent24h)
            }
        }

        // ── Range selector chips ───────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
            RANGE_LABEL_RES.forEach { (range, labelRes) ->
                RangeChip(
                    label = stringResource(labelRes),
                    selected = state.selectedRange == range,
                    onClick = { onIntent(DetailIntent.RangeChanged(range)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Interactive line chart ─────────────────────────────────────────
        PriceChart(
            points = state.chart.map { it.price },
            isPositive = detail.coin.changePercent24h >= 0,
            isLoading = state.isChartLoading && state.chart.isEmpty(),
            currencySymbol = CURRENCY_SYMBOL,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )

        // ── Stats grid ─────────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
                StatCard(stringResource(Res.string.detail_stat_market_cap), formatCompact(detail.marketCap), Modifier.weight(1f))
                StatCard(stringResource(Res.string.detail_stat_volume_24h), formatCompact(detail.volume24h), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
                StatCard(stringResource(Res.string.detail_stat_high_24h), formatPrice(detail.high24h), Modifier.weight(1f))
                StatCard(stringResource(Res.string.detail_stat_low_24h), formatPrice(detail.low24h), Modifier.weight(1f))
            }
        }

        // ── Description ─────────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
            Text(
                text = stringResource(Res.string.detail_about, detail.coin.name),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Text(
                text = detail.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
        }

        Spacer(Modifier.height(CryptoXSpacing.xl))
    }
}

@Composable
private fun RangeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    Box(
        modifier = modifier
            .clip(CryptoXShapes.small)
            .background(if (selected) colors.accentBg else colors.surface)
            .clickable(onClick = onClick)
            .padding(vertical = CryptoXSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.accent else colors.textSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    CryptoXCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Spacer(Modifier.height(CryptoXSpacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun DetailLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(CryptoXSpacing.pageHorizontal),
        verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.md),
    ) {
        repeat(6) {
            CryptoXLoadingShimmer(modifier = Modifier.fillMaxWidth())
        }
    }
}
