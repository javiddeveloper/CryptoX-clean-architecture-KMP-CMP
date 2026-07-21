/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail.ui

import kotlin.math.abs
import kotlin.math.roundToLong

/** Formats a large number as a compact string, e.g. 1.23B / 45.6M / 12.3K. */
internal fun formatCompact(value: Double): String {
    val sign = if (value < 0) "-" else ""
    val abs = abs(value)
    return when {
        abs >= 1_000_000_000 -> "$sign\$${twoDecimals(abs / 1_000_000_000)}B"
        abs >= 1_000_000 -> "$sign\$${twoDecimals(abs / 1_000_000)}M"
        abs >= 1_000 -> "$sign\$${twoDecimals(abs / 1_000)}K"
        else -> "$sign\$${twoDecimals(abs)}"
    }
}

/** Formats a price with sensible precision for small vs large values. */
internal fun formatPrice(value: Double): String {
    val decimals = when {
        value >= 1_000 -> 2
        value >= 1 -> 2
        value >= 0.01 -> 4
        else -> 8
    }
    return "\$${roundTo(value, decimals)}"
}

private fun twoDecimals(value: Double): String = roundTo(value, 2)

private fun roundTo(value: Double, decimals: Int): String {
    var factor = 1.0
    repeat(decimals) { factor *= 10 }
    val rounded = (value * factor).roundToLong().toDouble() / factor
    val text = rounded.toString()
    // Ensure trailing zeros so 1.2 -> 1.20 for 2 decimals.
    val dot = text.indexOf('.')
    if (dot == -1) return if (decimals == 0) text else text + "." + "0".repeat(decimals)
    val currentDecimals = text.length - dot - 1
    return if (currentDecimals >= decimals) {
        text.substring(0, dot + 1 + decimals)
    } else {
        text + "0".repeat(decimals - currentDecimals)
    }
}
