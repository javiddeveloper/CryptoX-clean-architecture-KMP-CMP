/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

// Material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import kotlin.math.abs
import kotlin.math.round

@Composable
fun CryptoXChangeBadge(
    changePercent: Double,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoXColors.current
    val isPositive = changePercent >= 0

    val containerColor = if (isPositive) colors.profitGreenBg else colors.lossRedBg
    val contentColor = if (isPositive) colors.profitGreen else colors.lossRed
    val icon = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    val sign = if (isPositive) "+" else "-"
    val text = "$sign${formatPercent(abs(changePercent))}%"

    Row(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(CornerRadius.full))
            .padding(horizontal = CryptoXSpacing.sm, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

// Rounds to 1 decimal place without String.format, which isn't available in commonMain.
private fun formatPercent(value: Double): String {
    val rounded = round(value * 10) / 10
    val whole = rounded.toInt()
    val decimal = round((rounded - whole) * 10).toInt()
    return "$whole.$decimal"
}
