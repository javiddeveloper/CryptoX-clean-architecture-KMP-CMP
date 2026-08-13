/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXSpacing

/**
 * Loading placeholder for [CryptoXCoinListItem].
 *
 * Mirrors the real row's layout — avatar, name/symbol stack, sparkline, price and badge —
 * so the list doesn't visibly reflow when data arrives.
 */
@Composable
fun CryptoXCoinListItemSkeleton(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(CornerRadius.card)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .cryptoXGlassSurface(shape)
            .padding(horizontal = CryptoXSpacing.md, vertical = CryptoXSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CryptoXLoadingShimmer(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
        )

        Spacer(modifier = Modifier.width(CryptoXSpacing.md))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.xs),
        ) {
            CryptoXLoadingShimmer(modifier = Modifier.width(96.dp).height(14.dp))
            CryptoXLoadingShimmer(modifier = Modifier.width(48.dp).height(11.dp))
        }

        CryptoXLoadingShimmer(
            modifier = Modifier
                .padding(horizontal = CryptoXSpacing.sm)
                .width(60.dp)
                .height(24.dp),
        )

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(CryptoXSpacing.xs),
        ) {
            CryptoXLoadingShimmer(modifier = Modifier.width(72.dp).height(14.dp))
            CryptoXLoadingShimmer(
                modifier = Modifier.width(52.dp).height(18.dp),
                shape = RoundedCornerShape(CornerRadius.full),
            )
        }
    }
}
