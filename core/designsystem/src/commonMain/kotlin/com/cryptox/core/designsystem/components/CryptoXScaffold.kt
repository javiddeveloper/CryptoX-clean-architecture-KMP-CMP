/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

/**
 * Screen skeleton for every CryptoX screen.
 *
 * Sits on the shared [CryptoXAuroraBackground], so the Material [Scaffold] itself is
 * transparent and the ambient wash shows through the whole screen rather than being
 * clipped to individual surfaces.
 */
@Composable
fun CryptoXScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    CryptoXAuroraBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            containerColor = Color.Transparent,
            contentColor = LocalCryptoXColors.current.textPrimary,
            content = content
        )
    }
}
