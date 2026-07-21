/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.feature.market.di

import com.cryptox.feature.market.ui.MarketViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val marketModule = module {
    viewModelOf(::MarketViewModel)
}
