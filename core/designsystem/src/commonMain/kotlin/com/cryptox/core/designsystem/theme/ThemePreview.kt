/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun ColorSwatch(name: String, color: Color) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Text(
            text = name,
            modifier = Modifier.padding(start = 16.dp),
            color = LocalCryptoXColors.current.textPrimary
        )
    }
}

@Composable
private fun ThemePalettePreview() {
    val colors = LocalCryptoXColors.current
    Column(
        modifier = Modifier
            .background(colors.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Backgrounds",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ColorSwatch("Background", colors.background)
        ColorSwatch("Surface", colors.surface)
        ColorSwatch("Surface Elevated", colors.surfaceElevated)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Semantics",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ColorSwatch("Accent", colors.accent)
        ColorSwatch("Profit Green", colors.profitGreen)
        ColorSwatch("Loss Red", colors.lossRed)
        ColorSwatch("Neutral", colors.neutral)
    }
}

@Preview
@Composable
fun DarkThemePreview() {
    CryptoXTheme(darkTheme = true) {
        ThemePalettePreview()
    }
}

@Preview
@Composable
fun LightThemePreview() {
    CryptoXTheme(darkTheme = false) {
        ThemePalettePreview()
    }
}

@Preview
@Composable
fun TypographyPreview() {
    CryptoXTheme {
        Column(
            modifier = Modifier
                .background(LocalCryptoXColors.current.background)
                .padding(16.dp)
        ) {
            val colors = LocalCryptoXColors.current
            val typo = MaterialTheme.typography
            
            Text("Display Large", style = typo.displayLarge, color = colors.textPrimary)
            Text("Display Medium", style = typo.displayMedium, color = colors.textPrimary)
            Text("Display Small", style = typo.displaySmall, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Headline Large", style = typo.headlineLarge, color = colors.textPrimary)
            Text("Headline Medium", style = typo.headlineMedium, color = colors.textPrimary)
            Text("Headline Small", style = typo.headlineSmall, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Title Large", style = typo.titleLarge, color = colors.textPrimary)
            Text("Title Medium", style = typo.titleMedium, color = colors.textPrimary)
            Text("Title Small", style = typo.titleSmall, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Body Large", style = typo.bodyLarge, color = colors.textPrimary)
            Text("Body Medium", style = typo.bodyMedium, color = colors.textPrimary)
            Text("Body Small", style = typo.bodySmall, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Label Large", style = typo.labelLarge, color = colors.textPrimary)
            Text("Label Medium", style = typo.labelMedium, color = colors.textPrimary)
            Text("Label Small", style = typo.labelSmall, color = colors.textPrimary)
        }
    }
}
