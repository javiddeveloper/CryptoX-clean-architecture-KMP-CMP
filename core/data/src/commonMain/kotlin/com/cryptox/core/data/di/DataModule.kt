/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.data.di

import com.cryptox.core.data.fake.FakeCoinRepository
import com.cryptox.core.domain.repository.CoinRepository
import org.koin.dsl.module

/** Provides the (currently fake) data layer. Swap the binding when real data lands. */
val dataModule = module {
    single<CoinRepository> { FakeCoinRepository() }
}
