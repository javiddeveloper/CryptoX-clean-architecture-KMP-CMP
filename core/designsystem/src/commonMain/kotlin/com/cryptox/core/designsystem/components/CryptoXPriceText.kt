/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import com.cryptox.core.designsystem.theme.cryptoXMonoTypography

@Composable
fun CryptoXPriceText(
    price: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null
) {
    val monoStyle = style ?: cryptoXMonoTypography().titleMedium
    val color = LocalCryptoXColors.current.textPrimary
    
    // Formatting doubles perfectly in multiplatform without external libraries
    // requires a bit of manual string building or expect/actual.
    // For now we do a simple string format approach.
    val formattedPrice = formatPrice(price)
    val text = "$currencySymbol$formattedPrice"
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
            text = text,
            style = monoStyle,
            color = color,
            modifier = modifier
        )
    }
}

// Basic formatting: 1234.56 -> 1,234.56
private fun formatPrice(price: Double): String {
    val parts = price.toString().split(".")
    val wholePart = parts[0]
    val fractionalPart = if (parts.size > 1) {
        val fraction = parts[1].take(2)
        if (fraction.length == 1) "${fraction}0" else fraction
    } else {
        "00"
    }
    
    val builder = StringBuilder()
    var count = 0
    for (i in wholePart.indices.reversed()) {
        builder.append(wholePart[i])
        count++
        if (count % 3 == 0 && i != 0 && wholePart[i - 1] != '-') {
            builder.append(",")
        }
    }
    
    return builder.reverse().toString() + "." + fractionalPart
}
