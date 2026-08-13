/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.LocalCryptoXColors

/**
 * Width reserved for the leading (back) and trailing (actions) slots.
 *
 * Both slots claim the same minimum width so the centred title stays optically centred
 * whether or not a back button or actions are present — a lambda parameter can't be
 * compared against "empty", so the layout has to be symmetric by construction.
 */
private val SlotWidth = 36.dp

@Composable
fun CryptoXTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val gradient = LocalCryptoXColors.current.heroGradient

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = gradient,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.sheet,
                    bottomEnd = CornerRadius.sheet
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                horizontal = CryptoXSpacing.pageHorizontal,
                vertical = CryptoXSpacing.md
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(SlotWidth)) {
                if (onBack != null) {
                    Box(
                        modifier = Modifier
                            .size(SlotWidth)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = CryptoXSpacing.sm),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.defaultMinSize(minWidth = SlotWidth),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)
            ) {
                actions()
            }
        }
    }
}
