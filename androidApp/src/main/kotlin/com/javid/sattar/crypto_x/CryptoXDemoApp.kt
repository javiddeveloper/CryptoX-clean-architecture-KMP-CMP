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
 * Minimal demo host wiring Market -> Detail with the fake data layer, so both
 * feature screens can be exercised end to end. The production app shell (nav
 * graph, bottom bar) is Agent 3's responsibility.
 */
@Composable
fun CryptoXDemoApp() {
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
