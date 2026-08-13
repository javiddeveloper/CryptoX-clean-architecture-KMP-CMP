/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

private const val SHIMMER_DURATION_MS = 1200

/** Width of the travelling highlight, as a fraction of the element it sweeps across. */
private const val SHIMMER_BAND_RATIO = 0.55f

/**
 * Paints an animated loading shimmer behind the element.
 *
 * The highlight band is sized relative to the element it is drawn on and travels from
 * fully off the leading edge to fully past the trailing edge, so the sweep reads the
 * same on a 60dp sparkline placeholder and a full-width card.
 */
@Composable
fun Modifier.cryptoXShimmer(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )

    val colors = LocalCryptoXColors.current
    val base = colors.surfaceElevated
    val highlight = colors.glassHighlight

    return drawBehind {
        drawRect(color = base)

        val band = size.width * SHIMMER_BAND_RATIO
        val start = -band + (size.width + band) * progress
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, highlight, Color.Transparent),
                start = Offset(start, 0f),
                end = Offset(start + band, 0f),
            ),
        )
    }
}

/**
 * A shimmering placeholder block.
 *
 * Rounds its corners by default so callers can't accidentally render a hard-edged
 * rectangle; pass [shape] to override. Give it a size via [modifier] — a bare
 * `fillMaxWidth()` collapses to zero height.
 */
@Composable
fun CryptoXLoadingShimmer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CornerRadius.sm),
) {
    Box(modifier = modifier.clip(shape).cryptoXShimmer())
}
