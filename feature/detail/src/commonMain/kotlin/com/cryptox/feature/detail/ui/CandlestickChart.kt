/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import com.cryptox.core.designsystem.theme.CornerRadius as CxCornerRadius
import com.cryptox.core.domain.model.Candle
import cryptox.feature.detail.generated.resources.Res
import cryptox.feature.detail.generated.resources.detail_ohlc_close
import cryptox.feature.detail.generated.resources.detail_ohlc_high
import cryptox.feature.detail.generated.resources.detail_ohlc_low
import cryptox.feature.detail.generated.resources.detail_ohlc_open
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

/** No candle is highlighted. */
private const val NO_SCRUB = -1

/** Fraction of each horizontal slot taken by the candle body; the rest is breathing room. */
private const val BODY_WIDTH_RATIO = 0.62f

/** Headroom added above the highest wick and below the lowest, as a fraction of the range. */
private const val PRICE_PADDING_RATIO = 0.08

private const val GRID_LINE_COUNT = 5

/**
 * An OHLC candlestick chart drawn entirely with Compose [Canvas].
 *
 * Each bar renders a wick spanning [Candle.low]..[Candle.high] and a body spanning
 * open..close, coloured green when the interval closed up and red when it closed down.
 * Dragging or tapping scrubs the series, pinning a crosshair to the nearest candle and
 * revealing its full OHLC breakdown above the plot.
 */
@Composable
fun CandlestickChart(
    candles: List<Candle>,
    currencySymbol: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    var scrubIndex by remember(candles) { mutableIntStateOf(NO_SCRUB) }

    // Replays whenever the series changes, so switching range/style animates in.
    val reveal by animateFloatAsState(
        targetValue = if (candles.isEmpty()) 0f else 1f,
        animationSpec = tween(durationMillis = 550),
        label = "candleReveal",
    )

    val scale = remember(candles) { PriceScale.of(candles) }
    val labelStyle = TextStyle(fontSize = 10.sp, color = colors.textMuted)
    val gutter = with(density) { AxisGutter.toPx() }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(candles) {
                    detectDragGestures(
                        onDragEnd = { scrubIndex = NO_SCRUB },
                        onDragCancel = { scrubIndex = NO_SCRUB },
                    ) { change, _ ->
                        scrubIndex = candleIndexAt(change.position.x, size.width - gutter, candles.size)
                    }
                }
                .pointerInput(candles) {
                    detectTapGestures(
                        onPress = { offset ->
                            scrubIndex = candleIndexAt(offset.x, size.width - gutter, candles.size)
                            tryAwaitRelease()
                            scrubIndex = NO_SCRUB
                        },
                    )
                },
        ) {
            if (candles.isEmpty()) return@Canvas
            val plotWidth = size.width - gutter
            if (plotWidth <= 0f) return@Canvas

            drawPriceGrid(
                scale = scale,
                plotWidth = plotWidth,
                gutterStart = plotWidth,
                gridColor = colors.divider,
                textMeasurer = textMeasurer,
                labelStyle = labelStyle,
                currencySymbol = currencySymbol,
                density = density,
            )

            drawCandles(
                candles = candles,
                scale = scale,
                plotWidth = plotWidth,
                reveal = reveal,
                bullColor = colors.profitGreen,
                bearColor = colors.lossRed,
                density = density,
            )

            if (scrubIndex in candles.indices) {
                drawCrosshair(
                    candle = candles[scrubIndex],
                    index = scrubIndex,
                    total = candles.size,
                    scale = scale,
                    plotWidth = plotWidth,
                    color = colors.accent,
                    density = density,
                )
            }
        }

        if (scrubIndex in candles.indices) {
            OhlcReadout(
                candle = candles[scrubIndex],
                currencySymbol = currencySymbol,
                alignRight = scrubIndex < candles.size / 2,
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
    }
}

/** Right-hand gutter reserved for price labels. */
private val AxisGutter = 56.dp

/** Maps prices onto vertical canvas positions, with a little headroom at both ends. */
private data class PriceScale(val low: Double, val high: Double) {
    private val span: Double get() = (high - low).takeIf { it > 0.0 } ?: 1.0

    fun yFor(price: Double, canvasHeight: Float): Float =
        (canvasHeight * (1.0 - (price - low) / span)).toFloat().coerceIn(0f, canvasHeight)

    fun priceAt(fraction: Double): Double = low + span * fraction

    companion object {
        fun of(candles: List<Candle>): PriceScale {
            if (candles.isEmpty()) return PriceScale(0.0, 1.0)
            val min = candles.minOf { it.low }
            val max = candles.maxOf { it.high }
            val padding = ((max - min) * PRICE_PADDING_RATIO).takeIf { it > 0.0 } ?: (max * 0.05)
            return PriceScale(low = min - padding, high = max + padding)
        }
    }
}

private fun candleIndexAt(x: Float, plotWidth: Float, count: Int): Int {
    if (plotWidth <= 0f || count == 0) return NO_SCRUB
    val slot = plotWidth / count
    return (x / slot).toInt().coerceIn(0, count - 1)
}

private fun DrawScope.drawPriceGrid(
    scale: PriceScale,
    plotWidth: Float,
    gutterStart: Float,
    gridColor: Color,
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
    currencySymbol: String,
    density: Density,
) {
    val dash = PathEffect.dashPathEffect(
        floatArrayOf(with(density) { 4.dp.toPx() }, with(density) { 6.dp.toPx() }),
    )
    val labelPadding = with(density) { 8.dp.toPx() }

    repeat(GRID_LINE_COUNT) { i ->
        val fraction = i / (GRID_LINE_COUNT - 1).toDouble()
        val price = scale.priceAt(fraction)
        val y = scale.yFor(price, size.height)

        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(plotWidth, y),
            strokeWidth = with(density) { 1.dp.toPx() },
            pathEffect = dash,
        )

        val label = textMeasurer.measure(formatPrice(price, currencySymbol), labelStyle)
        drawText(
            textLayoutResult = label,
            topLeft = Offset(
                x = gutterStart + labelPadding,
                y = (y - label.size.height / 2f).coerceIn(0f, size.height - label.size.height),
            ),
        )
    }
}

private fun DrawScope.drawCandles(
    candles: List<Candle>,
    scale: PriceScale,
    plotWidth: Float,
    reveal: Float,
    bullColor: Color,
    bearColor: Color,
    density: Density,
) {
    val slot = plotWidth / candles.size
    val bodyWidth = (slot * BODY_WIDTH_RATIO).coerceAtLeast(with(density) { 1.5.dp.toPx() })
    val wickWidth = with(density) { 1.5.dp.toPx() }
    val minBodyHeight = with(density) { 1.5.dp.toPx() }
    val bodyRadius = CornerRadius(with(density) { 2.dp.toPx() })

    // Left-to-right reveal: the series streams in rather than popping.
    val visible = (candles.size * reveal).roundToInt().coerceIn(0, candles.size)

    for (i in 0 until visible) {
        val candle = candles[i]
        val color = if (candle.isBullish) bullColor else bearColor
        val centerX = slot * i + slot / 2f

        drawLine(
            color = color,
            start = Offset(centerX, scale.yFor(candle.high, size.height)),
            end = Offset(centerX, scale.yFor(candle.low, size.height)),
            strokeWidth = wickWidth,
        )

        val openY = scale.yFor(candle.open, size.height)
        val closeY = scale.yFor(candle.close, size.height)
        val top = minOf(openY, closeY)
        val height = abs(closeY - openY).coerceAtLeast(minBodyHeight)
        val topLeft = Offset(centerX - bodyWidth / 2f, top)
        val bodySize = Size(bodyWidth, height)

        // Soft outer glow, then the solid body on top.
        drawRoundRect(
            color = color.copy(alpha = 0.22f),
            topLeft = Offset(topLeft.x - wickWidth, top - wickWidth),
            size = Size(bodyWidth + wickWidth * 2, height + wickWidth * 2),
            cornerRadius = bodyRadius,
        )
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(color, color.copy(alpha = 0.72f)),
                startY = top,
                endY = top + height,
            ),
            topLeft = topLeft,
            size = bodySize,
            cornerRadius = bodyRadius,
        )
    }
}

private fun DrawScope.drawCrosshair(
    candle: Candle,
    index: Int,
    total: Int,
    scale: PriceScale,
    plotWidth: Float,
    color: Color,
    density: Density,
) {
    val slot = plotWidth / total
    val centerX = slot * index + slot / 2f
    val closeY = scale.yFor(candle.close, size.height)
    val dash = PathEffect.dashPathEffect(
        floatArrayOf(with(density) { 3.dp.toPx() }, with(density) { 4.dp.toPx() }),
    )
    val strokeWidth = with(density) { 1.dp.toPx() }

    drawLine(
        color = color.copy(alpha = 0.55f),
        start = Offset(centerX, 0f),
        end = Offset(centerX, size.height),
        strokeWidth = strokeWidth,
        pathEffect = dash,
    )
    drawLine(
        color = color.copy(alpha = 0.55f),
        start = Offset(0f, closeY),
        end = Offset(plotWidth, closeY),
        strokeWidth = strokeWidth,
        pathEffect = dash,
    )
    drawCircle(
        color = color,
        radius = with(density) { 4.dp.toPx() },
        center = Offset(centerX, closeY),
    )
}

/** Floating OHLC breakdown shown while the user scrubs the chart. */
@Composable
private fun OhlcReadout(
    candle: Candle,
    currencySymbol: String,
    alignRight: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    val valueColor = if (candle.isBullish) colors.profitGreen else colors.lossRed

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = if (alignRight) Alignment.TopEnd else Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier
                .padding(CryptoXSpacing.xs)
                .clip(RoundedCornerShape(CxCornerRadius.md))
                .background(colors.surfaceElevated.copy(alpha = 0.94f))
                .border(
                    width = 1.dp,
                    color = valueColor.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(CxCornerRadius.md),
                )
                .padding(horizontal = CryptoXSpacing.sm, vertical = CryptoXSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            OhlcRow(stringResource(Res.string.detail_ohlc_open), candle.open, currencySymbol, colors.textSecondary)
            OhlcRow(stringResource(Res.string.detail_ohlc_high), candle.high, currencySymbol, colors.textSecondary)
            OhlcRow(stringResource(Res.string.detail_ohlc_low), candle.low, currencySymbol, colors.textSecondary)
            OhlcRow(stringResource(Res.string.detail_ohlc_close), candle.close, currencySymbol, valueColor)
        }
    }
}

@Composable
private fun OhlcRow(
    label: String,
    value: Double,
    currencySymbol: String,
    valueColor: Color,
) {
    val colors = LocalCryptoXColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.xs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        Text(
            text = formatPrice(value, currencySymbol),
            style = MaterialTheme.typography.labelMedium,
            color = valueColor,
        )
    }
}
