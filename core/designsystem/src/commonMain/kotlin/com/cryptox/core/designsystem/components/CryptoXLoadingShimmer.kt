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
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

private const val SHIMMER_DURATION_MS = 1200

@Composable
fun Modifier.CryptoXShimmer(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslateX",
    )

    val colors = LocalCryptoXColors.current
    val colorBase = colors.surfaceElevated
    val colorHighlight = colors.border

    val brush = Brush.linearGradient(
        colors = listOf(colorBase, colorHighlight, colorBase),
        start = Offset(translateX * 1000f, 0f),
        end = Offset(translateX * 1000f + 1000f, 0f),
    )

    return this.background(brush)
}

@Composable
fun CryptoXLoadingShimmer(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier.CryptoXShimmer()
    )
}
