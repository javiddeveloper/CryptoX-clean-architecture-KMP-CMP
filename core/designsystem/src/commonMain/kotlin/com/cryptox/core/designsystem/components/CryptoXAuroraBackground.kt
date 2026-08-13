/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

/** Full drift cycle of the ambient light sources. Deliberately slow — this should never draw attention. */
private const val DRIFT_DURATION_MS = 14_000

/**
 * The ambient backdrop every CryptoX screen sits on.
 *
 * Two large, very low-alpha radial "light sources" (teal and violet) bleed into the
 * flat page color and drift on a long, offset cycle, so a scrolling screen never looks
 * like flat navy. The motion is far too slow to read as animation — it just keeps the
 * background from feeling static.
 *
 * Purely decorative: it draws behind [content] and never intercepts input.
 */
@Composable
fun CryptoXAuroraBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalCryptoXColors.current
    val drift = rememberAuroraDrift(animated)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = colors.background)

            // Top-trailing teal bloom.
            drawAuroraBloom(
                color = colors.auroraPrimary,
                center = Offset(
                    x = size.width * (0.86f + 0.06f * drift),
                    y = size.height * (0.06f + 0.04f * drift),
                ),
                radius = size.maxDimension * 0.62f,
            )

            // Lower-leading violet bloom, drifting against the teal.
            drawAuroraBloom(
                color = colors.auroraSecondary,
                center = Offset(
                    x = size.width * (0.12f - 0.06f * drift),
                    y = size.height * (0.72f - 0.05f * drift),
                ),
                radius = size.maxDimension * 0.70f,
            )
        }
        content()
    }
}

/** Returns a 0..1 value that eases back and forth, or a fixed midpoint when [animated] is false. */
@Composable
private fun rememberAuroraDrift(animated: Boolean): Float {
    if (!animated) return 0.5f
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DRIFT_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "auroraDrift",
    )
    return drift
}

private fun DrawScope.drawAuroraBloom(
    color: Color,
    center: Offset,
    radius: Float,
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}
