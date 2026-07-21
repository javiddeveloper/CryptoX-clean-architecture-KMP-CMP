/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = DarkCryptoXColors.accent,
    onPrimary = CryptoDarkTextPrimary,
    primaryContainer = DarkCryptoXColors.accentBg,
    onPrimaryContainer = DarkCryptoXColors.accent,
    secondary = DarkCryptoXColors.profitGreen,
    tertiary = DarkCryptoXColors.neutral,
    error = DarkCryptoXColors.lossRed,
    errorContainer = DarkCryptoXColors.lossRedBg,
    background = DarkCryptoXColors.background,
    onBackground = DarkCryptoXColors.textPrimary,
    surface = DarkCryptoXColors.surface,
    onSurface = DarkCryptoXColors.textPrimary,
    surfaceVariant = DarkCryptoXColors.divider,
    onSurfaceVariant = DarkCryptoXColors.textSecondary,
    outline = DarkCryptoXColors.border,
    outlineVariant = DarkCryptoXColors.border,
)

private val LightColorScheme = lightColorScheme(
    primary = LightCryptoXColors.accent,
    onPrimary = CryptoLightBgSurface,
    primaryContainer = LightCryptoXColors.accentBg,
    onPrimaryContainer = LightCryptoXColors.accent,
    secondary = LightCryptoXColors.profitGreen,
    tertiary = LightCryptoXColors.neutral,
    error = LightCryptoXColors.lossRed,
    errorContainer = LightCryptoXColors.lossRedBg,
    background = LightCryptoXColors.background,
    onBackground = LightCryptoXColors.textPrimary,
    surface = LightCryptoXColors.surface,
    onSurface = LightCryptoXColors.textPrimary,
    surfaceVariant = LightCryptoXColors.divider,
    onSurfaceVariant = LightCryptoXColors.textSecondary,
    outline = LightCryptoXColors.border,
    outlineVariant = LightCryptoXColors.border,
)

@Composable
fun CryptoXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val extendedColors = if (darkTheme) DarkCryptoXColors else LightCryptoXColors
    val materialColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalCryptoXColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = cryptoXTypography(),
            shapes = CryptoXShapes,
            content = content
        )
    }
}
