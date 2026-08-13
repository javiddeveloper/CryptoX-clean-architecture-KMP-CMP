/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cryptox.core.designsystem.generated.resources.Res
import cryptox.core.designsystem.generated.resources.quicksand_variable
import org.jetbrains.compose.resources.Font

/**
 * Quicksand, a soft/rounded Google Font, loaded from its variable-weight file
 * with [FontVariation] settings per weight.
 */
@Composable
private fun quicksandFontFamily(): FontFamily = FontFamily(
    Font(Res.font.quicksand_variable, weight = FontWeight.Light, variationSettings = FontVariation.Settings(FontVariation.weight(300))),
    Font(Res.font.quicksand_variable, weight = FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(Res.font.quicksand_variable, weight = FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(Res.font.quicksand_variable, weight = FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(Res.font.quicksand_variable, weight = FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

@Composable
fun cryptoXTypography(): Typography {
    val fontFamily = quicksandFontFamily()
    val defaultTypography = Typography()

    return Typography(
        // Display
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(800),
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = fontFamily),
        // H1
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(800),
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.3).sp
        ),
        // H2
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 24.sp,
            lineHeight = 31.sp,
            letterSpacing = (-0.2).sp
        ),
        // H3
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 20.sp,
            lineHeight = 27.sp,
            letterSpacing = 0.sp
        ),
        // H4
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 17.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = fontFamily),
        // Body Large
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 16.sp,
            lineHeight = 27.sp,
            letterSpacing = 0.sp
        ),
        // Body Medium
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        // Body Small
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 13.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.sp
        ),
        // Label
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 12.5.sp,
            lineHeight = 17.5.sp,
            letterSpacing = 0.2.sp
        ),
        // Caption
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = fontFamily)
    )
}

/** 
 * Returns a typography specifically for prices, balances and other numeric data 
 * that requires tabular/monospaced fonts.
 */
@Composable
fun cryptoXMonoTypography(): Typography {
    val monoFont = FontFamily.Monospace
    val base = cryptoXTypography()
    
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = monoFont),
        displayMedium = base.displayMedium.copy(fontFamily = monoFont),
        displaySmall = base.displaySmall.copy(fontFamily = monoFont),
        headlineLarge = base.headlineLarge.copy(fontFamily = monoFont),
        headlineMedium = base.headlineMedium.copy(fontFamily = monoFont),
        headlineSmall = base.headlineSmall.copy(fontFamily = monoFont),
        titleLarge = base.titleLarge.copy(fontFamily = monoFont),
        titleMedium = base.titleMedium.copy(fontFamily = monoFont),
        titleSmall = base.titleSmall.copy(fontFamily = monoFont),
        bodyLarge = base.bodyLarge.copy(fontFamily = monoFont),
        bodyMedium = base.bodyMedium.copy(fontFamily = monoFont),
        bodySmall = base.bodySmall.copy(fontFamily = monoFont),
        labelLarge = base.labelLarge.copy(fontFamily = monoFont),
        labelMedium = base.labelMedium.copy(fontFamily = monoFont),
        labelSmall = base.labelSmall.copy(fontFamily = monoFont)
    )
}
