/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.data.fake

import com.cryptox.core.domain.model.ChartRange
import com.cryptox.core.domain.model.Coin
import com.cryptox.core.domain.model.CoinDetail
import com.cryptox.core.domain.model.PricePoint
import com.cryptox.core.domain.repository.CoinRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * In-memory fake backing both feature screens until the real data layer lands.
 *
 * Provides realistic hardcoded coins, a simulated network delay, and an optional
 * [failNextRefresh] flag so error states can be exercised in tests and previews.
 */
class FakeCoinRepository(
    private val networkDelayMs: Long = 700L,
) : CoinRepository {

    /** When true, the next [refreshMarket] / detail / chart call fails once. */
    var failNextRefresh: Boolean = false

    private val market = MutableStateFlow(seedCoins())

    override fun observeMarket(): Flow<List<Coin>> = market.asStateFlow()

    override suspend fun refreshMarket(): Result<Unit> {
        delay(networkDelayMs)
        if (consumeFailure()) {
            return Result.failure(IllegalStateException("اتصال به سرور برقرار نشد"))
        }
        // Nudge prices a little so a refresh visibly changes the list.
        market.value = market.value.map { coin ->
            val drift = coin.price * Random.nextDouble(-0.02, 0.02)
            coin.copy(
                price = (coin.price + drift).coerceAtLeast(0.0001),
                changePercent24h = coin.changePercent24h + Random.nextDouble(-0.5, 0.5),
            )
        }
        return Result.success(Unit)
    }

    override suspend fun getCoinDetail(id: String): Result<CoinDetail> {
        delay(networkDelayMs)
        if (consumeFailure()) {
            return Result.failure(IllegalStateException("خطا در دریافت اطلاعات ارز"))
        }
        val coin = market.value.firstOrNull { it.id == id }
            ?: return Result.failure(NoSuchElementException("ارز یافت نشد"))
        return Result.success(
            CoinDetail(
                coin = coin,
                marketCap = coin.price * 19_000_000,
                volume24h = coin.price * 850_000,
                high24h = coin.price * 1.06,
                low24h = coin.price * 0.94,
                description = descriptions[coin.id]
                    ?: "${coin.name} یک ارز دیجیتال است که در بازارهای جهانی معامله می‌شود.",
            ),
        )
    }

    override suspend fun getChart(id: String, range: ChartRange): Result<List<PricePoint>> {
        delay(networkDelayMs / 2)
        if (consumeFailure()) {
            return Result.failure(IllegalStateException("خطا در دریافت نمودار"))
        }
        val coin = market.value.firstOrNull { it.id == id }
            ?: return Result.failure(NoSuchElementException("ارز یافت نشد"))
        return Result.success(generateChart(coin.price, range))
    }

    override fun searchCoins(query: String): Flow<List<Coin>> {
        val q = query.trim()
        return market.map { coins ->
            if (q.isBlank()) {
                coins
            } else {
                coins.filter {
                    it.name.contains(q, ignoreCase = true) ||
                        it.symbol.contains(q, ignoreCase = true)
                }
            }
        }
    }

    private fun consumeFailure(): Boolean {
        if (failNextRefresh) {
            failNextRefresh = false
            return true
        }
        return false
    }

    private fun generateChart(basePrice: Double, range: ChartRange): List<PricePoint> {
        val points = when (range) {
            ChartRange.DAY -> 24
            ChartRange.WEEK -> 7 * 6
            ChartRange.MONTH -> 30
            ChartRange.YEAR -> 52
        }
        val stepMs = when (range) {
            ChartRange.DAY -> 3_600_000L
            ChartRange.WEEK -> 4 * 3_600_000L
            ChartRange.MONTH -> 24 * 3_600_000L
            ChartRange.YEAR -> 7 * 24 * 3_600_000L
        }
        val rng = Random(basePrice.toRawBits())
        val now = 1_700_000_000_000L
        return (0 until points).map { i ->
            val wave = sin(i / 4.0) * basePrice * 0.05
            val noise = rng.nextDouble(-0.03, 0.03) * basePrice
            PricePoint(
                timestamp = now - (points - i) * stepMs,
                price = (basePrice + wave + noise).coerceAtLeast(0.0001),
            )
        }
    }

    private fun seedCoins(): List<Coin> = rawSeed.map { (id, symbol, name, price, change) ->
        Coin(
            id = id,
            symbol = symbol,
            name = name,
            iconUrl = "https://assets.coincap.io/assets/icons/${symbol.lowercase()}@2x.png",
            price = price,
            changePercent24h = change,
            sparkline7d = sparkFor(price, change),
        )
    }

    private fun sparkFor(price: Double, change: Double): List<Double> {
        val rng = Random(price.toRawBits() xor change.toRawBits())
        val trend = change / 100.0
        return (0 until 24).map { i ->
            val t = i / 23.0
            val drift = price * trend * t
            val noise = price * rng.nextDouble(-0.015, 0.015)
            (price - price * trend + drift + noise).coerceAtLeast(0.0001)
        }
    }

    private companion object {
        // id, symbol, name, price, change24h%
        val rawSeed = listOf(
            Quint("bitcoin", "BTC", "Bitcoin", 67_842.15, 2.34),
            Quint("ethereum", "ETH", "Ethereum", 3_512.88, -1.12),
            Quint("tether", "USDT", "Tether", 1.0, 0.01),
            Quint("binancecoin", "BNB", "BNB", 585.42, 0.87),
            Quint("solana", "SOL", "Solana", 172.63, 5.21),
            Quint("ripple", "XRP", "XRP", 0.6234, -2.03),
            Quint("usd-coin", "USDC", "USD Coin", 1.0, -0.02),
            Quint("cardano", "ADA", "Cardano", 0.4521, 1.44),
            Quint("dogecoin", "DOGE", "Dogecoin", 0.1623, 8.76),
            Quint("avalanche", "AVAX", "Avalanche", 38.21, -3.42),
            Quint("shiba-inu", "SHIB", "Shiba Inu", 0.0000242, 4.10),
            Quint("polkadot", "DOT", "Polkadot", 7.34, -0.55),
            Quint("chainlink", "LINK", "Chainlink", 16.82, 3.19),
            Quint("tron", "TRX", "TRON", 0.1287, 0.42),
            Quint("polygon", "MATIC", "Polygon", 0.7213, -1.88),
            Quint("litecoin", "LTC", "Litecoin", 84.55, 1.03),
            Quint("uniswap", "UNI", "Uniswap", 10.92, 6.44),
            Quint("stellar", "XLM", "Stellar", 0.1123, -0.91),
            Quint("cosmos", "ATOM", "Cosmos", 8.71, 2.55),
            Quint("monero", "XMR", "Monero", 168.30, -1.27),
            Quint("aptos", "APT", "Aptos", 9.14, 7.82),
            Quint("near", "NEAR", "NEAR Protocol", 6.05, -2.66),
            Quint("filecoin", "FIL", "Filecoin", 5.48, 3.90),
        )

        val descriptions = mapOf(
            "bitcoin" to "بیت‌کوین اولین و بزرگ‌ترین ارز دیجیتال غیرمتمرکز جهان است که در سال ۲۰۰۹ معرفی شد.",
            "ethereum" to "اتریوم یک پلتفرم قراردادهای هوشمند است که زیرساخت بسیاری از برنامه‌های غیرمتمرکز را فراهم می‌کند.",
            "solana" to "سولانا یک بلاک‌چین پرسرعت با کارمزد پایین برای اپلیکیشن‌های غیرمتمرکز و مالی است.",
        )
    }

    private data class Quint(
        val id: String,
        val symbol: String,
        val name: String,
        val price: Double,
        val change: Double,
    )
}
