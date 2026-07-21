/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
// Raw palette â€” crypto dark-first
// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

// Background layers
val CryptoNavy950 = Color(0xFF070C18)   // deepest bg â€” list headers
val CryptoNavy900 = Color(0xFF0A0F1E)   // page bg (dark primary)
val CryptoNavy800 = Color(0xFF111827)   // surface
val CryptoNavy700 = Color(0xFF141B2E)   // surface elevated
val CryptoNavy600 = Color(0xFF1A2540)   // card / sheet
val CryptoNavy500 = Color(0xFF1E293F)   // outer border / subtle fill

// Accent â€” teal/cyan (hero gradient, accent actions)
val CryptoTeal400  = Color(0xFF22D3EE)
val CryptoTeal500  = Color(0xFF06B6D4)
val CryptoTeal600  = Color(0xFF0891B2)
val CryptoTeal700  = Color(0xFF0E7490)
val CryptoTeal900  = Color(0xFF164E63)
val CryptoTealBg   = Color(0x1A06B6D4)  // rgba(6,182,212,.10)

// Profit â€” green
val CryptoGreen400 = Color(0xFF4ADE80)
val CryptoGreen500 = Color(0xFF22C55E)
val CryptoGreen600 = Color(0xFF16A34A)
val CryptoGreenBg  = Color(0x1A22C55E)  // rgba(34,197,94,.10)

// Loss â€” red
val CryptoRed400   = Color(0xFFF87171)
val CryptoRed500   = Color(0xFFEF4444)
val CryptoRed600   = Color(0xFFDC2626)
val CryptoRedBg    = Color(0x1AEF4444)  // rgba(239,68,68,.10)

// Neutral
val CryptoNeutral400 = Color(0xFF94A3B8)
val CryptoNeutral500 = Color(0xFF64748B)
val CryptoNeutral600 = Color(0xFF475569)
val CryptoNeutralBg  = Color(0x1A94A3B8)

// Text
val CryptoDarkTextPrimary   = Color(0xFFEDF1F7)
val CryptoDarkTextSecondary = Color(0xFF8B96AC)
val CryptoDarkTextMuted     = Color(0xFF4A5567)

val CryptoLightTextPrimary   = Color(0xFF0F172A)
val CryptoLightTextSecondary = Color(0xFF475569)
val CryptoLightTextMuted     = Color(0xFF94A3B8)

// Light mode surfaces
val CryptoLightBgPage    = Color(0xFFF0F4F8)
val CryptoLightBgSurface = Color(0xFFFFFFFF)
val CryptoLightBorder    = Color(0xFFE2E8F0)
val CryptoLightDivider   = Color(0xFFF1F5F9)

// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
// Semantic color data class
// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Immutable
data class CryptoXColors(
    // Backgrounds / surfaces
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val divider: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,

    // Crypto semantics
    val profitGreen: Color,
    val profitGreenBg: Color,
    val lossRed: Color,
    val lossRedBg: Color,
    val neutral: Color,
    val neutralBg: Color,

    // Accent (teal)
    val accent: Color,
    val accentBg: Color,

    // Disabled
    val disabledAlpha: Float,

    // Gradients
    val heroGradient: Brush,
    val accentGradient: Brush,
)

val DarkCryptoXColors = CryptoXColors(
    background      = CryptoNavy900,
    surface         = CryptoNavy700,
    surfaceElevated = CryptoNavy600,
    border          = Color(0x17FFFFFF),   // rgba(255,255,255,.09)
    divider         = Color(0x0FFFFFFF),   // rgba(255,255,255,.06)
    textPrimary     = CryptoDarkTextPrimary,
    textSecondary   = CryptoDarkTextSecondary,
    textMuted       = CryptoDarkTextMuted,
    profitGreen     = CryptoGreen400,
    profitGreenBg   = CryptoGreenBg,
    lossRed         = CryptoRed400,
    lossRedBg       = CryptoRedBg,
    neutral         = CryptoNeutral400,
    neutralBg       = CryptoNeutralBg,
    accent          = CryptoTeal400,
    accentBg        = CryptoTealBg,
    disabledAlpha   = 0.38f,
    heroGradient    = Brush.linearGradient(listOf(CryptoTeal500, CryptoTeal700)),
    accentGradient  = Brush.linearGradient(listOf(CryptoTeal400, CryptoTeal600)),
)

val LightCryptoXColors = CryptoXColors(
    background      = CryptoLightBgPage,
    surface         = CryptoLightBgSurface,
    surfaceElevated = CryptoLightBgSurface,
    border          = CryptoLightBorder,
    divider         = CryptoLightDivider,
    textPrimary     = CryptoLightTextPrimary,
    textSecondary   = CryptoLightTextSecondary,
    textMuted       = CryptoLightTextMuted,
    profitGreen     = CryptoGreen600,
    profitGreenBg   = CryptoGreenBg,
    lossRed         = CryptoRed600,
    lossRedBg       = CryptoRedBg,
    neutral         = CryptoNeutral500,
    neutralBg       = CryptoNeutralBg,
    accent          = CryptoTeal600,
    accentBg        = CryptoTealBg,
    disabledAlpha   = 0.38f,
    heroGradient    = Brush.linearGradient(listOf(CryptoTeal500, CryptoTeal700)),
    accentGradient  = Brush.linearGradient(listOf(CryptoTeal500, CryptoTeal700)),
)

/** Access the current theme's extended crypto colors via CompositionLocal. */
val LocalCryptoXColors = staticCompositionLocalOf { DarkCryptoXColors }
