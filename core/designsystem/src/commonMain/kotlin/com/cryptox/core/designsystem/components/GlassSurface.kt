/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

/**
 * The "glass" treatment shared by every raised surface in CryptoX.
 *
 * A translucent fill lets the [CryptoXAuroraBackground] bleed through, and a one-pixel
 * border fades from a lit top edge down to the neutral base border — the cue that reads
 * as a pane of glass catching light from above.
 *
 * Defined once here so cards, list rows, and sheets can't drift apart visually.
 */
@Composable
fun Modifier.cryptoXGlassSurface(shape: Shape): Modifier {
    val colors = LocalCryptoXColors.current
    return this
        .clip(shape)
        .background(colors.glassSurface)
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(listOf(colors.glassHighlight, colors.border)),
            shape = shape,
        )
}
