/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

@Composable
fun CryptoXCoinListItem(
    iconUrl: String,
    name: String,
    symbol: String,
    price: Double,
    currencySymbol: String,
    changePercent: Double,
    sparkline: List<Double>,
    onClick: () -> Unit
) {
    val colors = LocalCryptoXColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = CryptoXSpacing.pageHorizontal, vertical = CryptoXSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Placeholder - TODO: Replace with Coil AsyncImage when dependency is added
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.width(CryptoXSpacing.md))

        // Name and Symbol
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1
            )
            Text(
                text = symbol.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(CryptoXSpacing.sm))

        // Sparkline
        CryptoXSparkline(
            points = sparkline,
            isPositive = changePercent >= 0,
            modifier = Modifier.padding(horizontal = CryptoXSpacing.sm)
        )

        Spacer(modifier = Modifier.width(CryptoXSpacing.sm))

        // Price and Change
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            CryptoXPriceText(
                price = price,
                currencySymbol = currencySymbol
            )
            Spacer(modifier = Modifier.height(CryptoXSpacing.xs))
            CryptoXChangeBadge(changePercent = changePercent)
        }
    }
}

// Extension to avoid importing androidx.compose.foundation.layout.height directly 
// which could conflict if done wrong, though standard compose height is fine.
@Composable
private fun SpacerHeight(height: androidx.compose.ui.unit.Dp) {
    Spacer(modifier = Modifier.height(height))
}
// wait, fixing spacer above.
