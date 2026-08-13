/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui.contract

import androidx.compose.runtime.Immutable
import com.cryptox.core.domain.model.Candle
import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.model.CoinDetail
import com.cryptox.core.domain.model.PricePoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** How the price series is rendered. Purely a presentation choice. */
enum class ChartStyle { LINE, CANDLE }

@Immutable
data class DetailUiState(
    val detail: CoinDetail? = null,
    val chart: ImmutableList<PricePoint> = persistentListOf(),
    val candles: ImmutableList<Candle> = persistentListOf(),
    val chartStyle: ChartStyle = ChartStyle.CANDLE,
    val selectedRange: ChartRange = ChartRange.WEEK,
    val isLoading: Boolean = false,
    val isChartLoading: Boolean = false,
    val error: String? = null,
) {
    /** True while the series backing the *current* [chartStyle] has nothing to draw. */
    val isCurrentSeriesEmpty: Boolean
        get() = when (chartStyle) {
            ChartStyle.LINE -> chart.isEmpty()
            ChartStyle.CANDLE -> candles.isEmpty()
        }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class DetailLoaded(val detail: CoinDetail) : PartialState
        data class ChartLoading(val isChartLoading: Boolean) : PartialState
        data class ChartLoaded(val points: List<PricePoint>) : PartialState
        data class CandlesLoaded(val candles: List<Candle>) : PartialState
        data class RangeSelected(val range: ChartRange) : PartialState
        data class ChartStyleSelected(val style: ChartStyle) : PartialState
    }
}

sealed interface DetailIntent {
    data class Load(val coinId: String) : DetailIntent
    data class RangeChanged(val range: ChartRange) : DetailIntent
    data class ChartStyleChanged(val style: ChartStyle) : DetailIntent
    data object BackClicked : DetailIntent
    data object Retry : DetailIntent
}

sealed interface DetailEffect {
    data object NavigateBack : DetailEffect
    data class ShowError(val message: String) : DetailEffect
}
