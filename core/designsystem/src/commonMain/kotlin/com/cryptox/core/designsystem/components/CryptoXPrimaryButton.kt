/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

@Composable
fun CryptoXPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = LocalCryptoXColors.current
    val gradient = colors.accentGradient
    
    val baseModifier = modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(CornerRadius.button))
        
    val stateModifier = if (enabled) {
        baseModifier
            .background(gradient)
            .clickable(onClick = onClick)
    } else {
        baseModifier
            .background(colors.surfaceElevated)
            .alpha(colors.disabledAlpha)
    }
    
    Row(
        modifier = stateModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) Color.White else colors.textMuted
        )
    }
}
