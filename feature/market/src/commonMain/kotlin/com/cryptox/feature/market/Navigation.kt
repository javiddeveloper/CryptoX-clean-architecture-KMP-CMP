/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cryptox.feature.market.ui.MarketRoute
import kotlinx.serialization.Serializable

@Serializable
data object MarketDestination

/** Registers the market destination. Agent 3 owns the host graph. */
fun NavGraphBuilder.marketScreen(
    onNavigateToDetail: (String) -> Unit,
) {
    composable<MarketDestination> {
        MarketRoute(onNavigateToDetail = onNavigateToDetail)
    }
}
