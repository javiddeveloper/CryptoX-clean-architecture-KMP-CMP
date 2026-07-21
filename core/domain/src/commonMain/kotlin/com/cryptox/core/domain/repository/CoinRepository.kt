/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.domain.repository

import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.model.Coin
import com.cryptox.core.domain.model.CoinDetail
import com.cryptox.core.domain.model.PricePoint
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for market data. Feature modules depend only on this
 * interface; the real network/database implementation is wired later.
 */
interface CoinRepository {
    fun observeMarket(): Flow<List<Coin>>
    suspend fun refreshMarket(): Result<Unit>
    suspend fun getCoinDetail(id: String): Result<CoinDetail>
    suspend fun getChart(id: String, range: ChartRange): Result<List<PricePoint>>
    fun searchCoins(query: String): Flow<List<Coin>>
}
