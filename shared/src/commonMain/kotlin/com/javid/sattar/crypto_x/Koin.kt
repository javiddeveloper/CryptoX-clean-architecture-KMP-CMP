/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

import com.cryptox.core.data.di.dataModule
import com.cryptox.feature.detail.di.detailModule
import com.cryptox.feature.market.di.marketModule
import org.koin.core.context.startKoin

/**
 * Starts Koin with every module the demo app needs. Called once from each platform's
 * entry point (Android `Application`, iOS `MainViewController`) before the first
 * composable that resolves a ViewModel is shown.
 */
fun initKoin() {
    startKoin {
        modules(dataModule, marketModule, detailModule)
    }
}
