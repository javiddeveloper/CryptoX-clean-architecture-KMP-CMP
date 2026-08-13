/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

import androidx.compose.ui.window.ComposeUIViewController

private var koinStarted = false

fun MainViewController() = ComposeUIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    App()
}