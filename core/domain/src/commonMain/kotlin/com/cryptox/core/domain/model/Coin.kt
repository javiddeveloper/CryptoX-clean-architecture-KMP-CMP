/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.domain.model

/** A single tradable coin as shown in the market list. */
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: Double,
    val changePercent24h: Double,
    val sparkline7d: List<Double>,
)

/** Extended information for the coin detail screen. */
data class CoinDetail(
    val coin: Coin,
    val marketCap: Double,
    val volume24h: Double,
    val high24h: Double,
    val low24h: Double,
    val description: String,
)

/** A single price sample on the detail chart. */
data class PricePoint(
    val timestamp: Long,
    val price: Double,
)

/**
 * One OHLC bar of the candlestick chart, covering the interval that starts at
 * [timestamp]. [open] and [close] bound the body; [high] and [low] the wicks.
 */
data class Candle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
) {
    /** True when the interval closed at or above its open — drawn in the profit color. */
    val isBullish: Boolean get() = close >= open
}

/** Selectable ranges for the detail chart. */
enum class ChartRange { DAY, WEEK, MONTH, YEAR }
