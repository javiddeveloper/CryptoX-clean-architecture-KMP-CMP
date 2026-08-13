/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXTheme
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun ComponentsGallery() {
    val colors = LocalCryptoXColors.current
    Column(
        modifier = Modifier
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CryptoXTopBar(title = "CryptoX Gallery", onBack = {})
        
        CryptoXSearchField(query = "", onQueryChange = {}, placeholder = "Search coins...")
        
        CryptoXCard(modifier = Modifier.fillMaxWidth()) {
            Text("This is a CryptoXCard", color = colors.textPrimary)
        }
        
        CryptoXPrimaryButton(text = "Primary Button", onClick = {})

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CryptoXChangeBadge(changePercent = 2.53)
            CryptoXChangeBadge(changePercent = -1.2)
        }

        CryptoXLoadingShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            shape = RoundedCornerShape(CornerRadius.card)
        )

        CryptoXCoinListItemSkeleton()

        CryptoXCoinListItem(
            iconUrl = "",
            name = "Bitcoin",
            symbol = "BTC",
            price = 63241.50,
            currencySymbol = "$",
            changePercent = 2.5,
            sparkline = listOf(10.0, 12.0, 11.0, 15.0, 14.0, 18.0),
            onClick = {}
        )
        
        CryptoXCoinListItem(
            iconUrl = "",
            name = "Ethereum",
            symbol = "ETH",
            price = 3400.12,
            currencySymbol = "$",
            changePercent = -1.2,
            sparkline = listOf(18.0, 15.0, 16.0, 12.0, 10.0),
            onClick = {}
        )
        
        CryptoXEmptyState(
            title = "No Coins Found",
            subtitle = "Try searching for something else."
        )
        
        CryptoXErrorState(
            message = "Network connection failed.",
            onRetry = {}
        )
    }
}

@Preview
@Composable
fun ComponentsPreviewDark() {
    CryptoXTheme(darkTheme = true) {
        ComponentsGallery()
    }
}

@Preview
@Composable
fun ComponentsPreviewLight() {
    CryptoXTheme(darkTheme = false) {
        ComponentsGallery()
    }
}
