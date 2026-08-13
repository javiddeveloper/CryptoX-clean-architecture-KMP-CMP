/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cryptox.core.designsystem.theme.CryptoXTheme
import com.cryptox.feature.detail.DetailDestination
import com.cryptox.feature.detail.detailScreen
import com.cryptox.feature.market.MarketDestination
import com.cryptox.feature.market.marketScreen

/**
 * Cross-platform demo host wiring Market -> Detail with the fake data layer, so both
 * feature screens can be exercised end to end on Android and iOS from the same
 * composable. The production app shell (nav graph, bottom bar, Portfolio/Settings) is
 * a separate, later effort.
 */
@Composable
fun App() {
    CryptoXTheme(darkTheme = true) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MarketDestination,
        ) {
            marketScreen(
                onNavigateToDetail = { coinId ->
                    navController.navigate(DetailDestination(coinId))
                },
            )
            detailScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
