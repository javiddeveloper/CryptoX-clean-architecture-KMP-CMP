/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.components.CryptoXLoadingShimmer
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import androidx.compose.foundation.Canvas
import cryptox.feature.detail.generated.resources.Res
import cryptox.feature.detail.generated.resources.detail_no_chart_data
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * A line chart drawn entirely with Compose [Canvas]. Supports touch scrubbing:
 * dragging (or tapping) reveals the price at the nearest sample as a floating label.
 */
@Composable
fun PriceChart(
    points: List<Double>,
    isPositive: Boolean,
    isLoading: Boolean,
    currencySymbol: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCryptoXColors.current
    val lineColor = if (isPositive) colors.profitGreen else colors.lossRed
    val density = LocalDensity.current

    Box(modifier = modifier) {
        when {
            isLoading -> CryptoXLoadingShimmer(modifier = Modifier.fillMaxSize())

            points.size < 2 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.detail_no_chart_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                )
            }

            else -> {
                // -1 => no active scrub.
                var scrubIndex by remember(points) { mutableStateOf(-1) }
                val min = points.min()
                val max = points.max()
                val span = (max - min).takeIf { it > 0.0 } ?: 1.0

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectDragGestures(
                                onDragEnd = { scrubIndex = -1 },
                                onDragCancel = { scrubIndex = -1 },
                            ) { change, _ ->
                                scrubIndex = indexForX(change.position.x, size.width, points.size)
                            }
                        }
                        .pointerInput(points) {
                            detectTapGestures(
                                onPress = { offset ->
                                    scrubIndex = indexForX(offset.x, size.width, points.size)
                                    tryAwaitRelease()
                                    scrubIndex = -1
                                },
                            )
                        },
                ) {
                    val stepX = size.width / (points.size - 1)
                    fun yFor(value: Double): Float =
                        (size.height * (1f - ((value - min) / span).toFloat())).coerceIn(0f, size.height)

                    val linePath = Path()
                    val fillPath = Path()
                    points.forEachIndexed { i, value ->
                        val x = stepX * i
                        val y = yFor(value)
                        if (i == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, size.height)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }
                    fillPath.lineTo(stepX * (points.size - 1), size.height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            listOf(lineColor.copy(alpha = 0.25f), lineColor.copy(alpha = 0f)),
                        ),
                    )
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = with(density) { 2.dp.toPx() }),
                    )

                    if (scrubIndex in points.indices) {
                        val x = stepX * scrubIndex
                        val y = yFor(points[scrubIndex])
                        drawScrubber(x, y, size.height, lineColor, colors.textMuted, density)
                    }
                }

                if (scrubIndex in points.indices) {
                    ScrubLabel(
                        price = points[scrubIndex],
                        currencySymbol = currencySymbol,
                        fraction = scrubIndex.toFloat() / (points.size - 1),
                    )
                }
            }
        }
    }
}

private fun indexForX(x: Float, width: Int, count: Int): Int {
    if (width <= 0 || count <= 1) return -1
    val fraction = (x / width).coerceIn(0f, 1f)
    return (fraction * (count - 1)).roundToInt().coerceIn(0, count - 1)
}

private fun DrawScope.drawScrubber(
    x: Float,
    y: Float,
    height: Float,
    lineColor: androidx.compose.ui.graphics.Color,
    guideColor: androidx.compose.ui.graphics.Color,
    density: androidx.compose.ui.unit.Density,
) {
    drawLine(
        color = guideColor.copy(alpha = 0.5f),
        start = Offset(x, 0f),
        end = Offset(x, height),
        strokeWidth = with(density) { 1.dp.toPx() },
    )
    drawCircle(
        color = lineColor,
        radius = with(density) { 5.dp.toPx() },
        center = Offset(x, y),
    )
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.ScrubLabel(
    price: Double,
    currencySymbol: String,
    fraction: Float,
) {
    val colors = LocalCryptoXColors.current
    val alignment = when {
        fraction < 0.33f -> Alignment.TopStart
        fraction > 0.66f -> Alignment.TopEnd
        else -> Alignment.TopCenter
    }
    Box(
        modifier = Modifier
            .align(alignment)
            .padding(CryptoXSpacing.xs)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .padding(horizontal = CryptoXSpacing.sm, vertical = CryptoXSpacing.xs),
    ) {
        Text(
            text = "$currencySymbol${formatPrice(price).removePrefix(currencySymbol)}",
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
        )
    }
}
