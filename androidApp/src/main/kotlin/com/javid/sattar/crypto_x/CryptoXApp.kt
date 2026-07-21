/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

import android.app.Application
import com.cryptox.core.data.di.dataModule
import com.cryptox.feature.detail.di.detailModule
import com.cryptox.feature.market.di.marketModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CryptoXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CryptoXApp)
            modules(dataModule, marketModule, detailModule)
        }
    }
}
