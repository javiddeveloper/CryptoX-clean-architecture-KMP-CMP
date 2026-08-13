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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
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
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXShapes
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.model.CoinDetail
import com.cryptox.feature.detail.ui.contract.ChartStyle
import com.cryptox.feature.detail.ui.contract.DetailEffect
import com.cryptox.feature.detail.ui.contract.DetailIntent
import com.cryptox.feature.detail.ui.contract.DetailUiState
import cryptox.feature.detail.generated.resources.Res
import cryptox.feature.detail.generated.resources.detail_about
import cryptox.feature.detail.generated.resources.detail_chart_style_candle
import cryptox.feature.detail.generated.resources.detail_chart_style_line
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

        // ── Chart: style toggle + the series itself ─────────────────────────
        ChartStyleToggle(
            selected = state.chartStyle,
            onSelect = { onIntent(DetailIntent.ChartStyleChanged(it)) },
            modifier = Modifier.align(Alignment.End),
        )
        ChartSurface(
            state = state,
            isPositive = detail.coin.changePercent24h >= 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
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

/**
 * Hosts whichever series the current [DetailUiState.chartStyle] selects, and owns the
 * loading / empty states so the chart composables stay pure renderers.
 */
@Composable
private fun ChartSurface(
    state: DetailUiState,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(colors.surface.copy(alpha = 0.45f))
            .padding(vertical = CryptoXSpacing.sm, horizontal = CryptoXSpacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isChartLoading && state.isCurrentSeriesEmpty ->
                CryptoXLoadingShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(CornerRadius.md)),
                )

            state.isCurrentSeriesEmpty -> Text(
                text = stringResource(Res.string.detail_no_chart_data),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
            )

            state.chartStyle == ChartStyle.LINE -> PriceChart(
                points = state.chart.map { it.price },
                isPositive = isPositive,
                currencySymbol = CURRENCY_SYMBOL,
                modifier = Modifier.fillMaxSize(),
            )

            else -> CandlestickChart(
                candles = state.candles,
                currencySymbol = CURRENCY_SYMBOL,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Compact two-state pill switching the chart between a line and OHLC candles. */
@Composable
private fun ChartStyleToggle(
    selected: ChartStyle,
    onSelect: (ChartStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(colors.surface)
            .padding(CryptoXSpacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.xxs),
    ) {
        ChartStyleOption(
            label = stringResource(Res.string.detail_chart_style_line),
            selected = selected == ChartStyle.LINE,
            onClick = { onSelect(ChartStyle.LINE) },
        )
        ChartStyleOption(
            label = stringResource(Res.string.detail_chart_style_candle),
            selected = selected == ChartStyle.CANDLE,
            onClick = { onSelect(ChartStyle.CANDLE) },
        )
    }
}

@Composable
private fun ChartStyleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalCryptoXColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(if (selected) colors.accent else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = CryptoXSpacing.md, vertical = CryptoXSpacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.background else colors.textSecondary,
        )
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

/**
 * Loading placeholder mirroring [DetailContent]'s layout — header, range chips, chart and
 * stats grid — so the screen doesn't reflow when the real data lands.
 */
@Composable
private fun DetailLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = CryptoXSpacing.pageHorizontal),
        verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.lg),
    ) {
        Spacer(Modifier.height(CryptoXSpacing.sm))

        // Header: avatar + name/symbol, price + change badge.
        Row(verticalAlignment = Alignment.CenterVertically) {
            CryptoXLoadingShimmer(modifier = Modifier.size(48.dp), shape = CircleShape)
            Spacer(Modifier.width(CryptoXSpacing.md))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.xs),
            ) {
                CryptoXLoadingShimmer(modifier = Modifier.width(120.dp).height(18.dp))
                CryptoXLoadingShimmer(modifier = Modifier.width(56.dp).height(12.dp))
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.xs),
            ) {
                CryptoXLoadingShimmer(modifier = Modifier.width(104.dp).height(18.dp))
                CryptoXLoadingShimmer(
                    modifier = Modifier.width(64.dp).height(20.dp),
                    shape = RoundedCornerShape(CornerRadius.full),
                )
            }
        }

        // Range chips.
        Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
            repeat(RANGE_LABEL_RES.size) {
                CryptoXLoadingShimmer(
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = CryptoXShapes.small,
                )
            }
        }

        // Chart.
        CryptoXLoadingShimmer(
            modifier = Modifier.fillMaxWidth().height(240.dp),
            shape = RoundedCornerShape(CornerRadius.card),
        )

        // Stats grid.
        Column(verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)) {
                    repeat(2) {
                        CryptoXLoadingShimmer(
                            modifier = Modifier.weight(1f).height(76.dp),
                            shape = RoundedCornerShape(CornerRadius.card),
                        )
                    }
                }
            }
        }
    }
}
