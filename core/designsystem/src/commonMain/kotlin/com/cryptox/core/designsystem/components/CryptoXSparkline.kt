/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

@Composable
fun CryptoXSparkline(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoXColors.current
    val strokeColor = if (isPositive) colors.profitGreen else colors.lossRed

    if (points.isEmpty()) {
        Canvas(modifier = modifier.width(60.dp).height(24.dp)) {}
        return
    }

    val maxPoint = points.maxOrNull() ?: 1.0
    val minPoint = points.minOrNull() ?: 0.0
    val range = (maxPoint - minPoint).takeIf { it > 0 } ?: 1.0

    Canvas(modifier = modifier.width(60.dp).height(24.dp)) {
        val width = size.width
        val height = size.height
        val stepX = if (points.size > 1) width / (points.size - 1) else width

        val path = Path()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            // Y is inverted because 0 is at the top of the canvas
            val normalizedY = ((value - minPoint) / range).toFloat()
            val y = height - (normalizedY * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
