/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cryptox.feature.detail.ui.DetailRoute
import kotlinx.serialization.Serializable

@Serializable
data class DetailDestination(val coinId: String)

/** Registers the coin detail destination. Agent 3 owns the host graph. */
fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
) {
    composable<DetailDestination> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailDestination>()
        DetailRoute(coinId = route.coinId, onBack = onBack)
    }
}
