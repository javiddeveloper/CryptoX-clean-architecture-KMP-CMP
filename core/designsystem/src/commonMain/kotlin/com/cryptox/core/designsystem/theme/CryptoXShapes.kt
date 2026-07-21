/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CryptoXShapes = Shapes(
    small = RoundedCornerShape(CornerRadius.sm),
    medium = RoundedCornerShape(CornerRadius.md),
    large = RoundedCornerShape(CornerRadius.lg),
)

object CornerRadius {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val full = 9999.dp

    val chip = 20.dp
    val card = 16.dp
    val sheet = 28.dp
    val button = 12.dp
}

object Elevation {
    val none = 0.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 8.dp
    val lg = 16.dp
}

object IconSize {
    val tiny = 12.dp
    val small = 16.dp
    val medium = 24.dp
    val large = 32.dp
    val xlarge = 48.dp
}
