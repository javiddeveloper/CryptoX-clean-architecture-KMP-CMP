/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.cryptox.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.cryptox.core.designsystem.theme.CornerRadius
import com.cryptox.core.designsystem.theme.CryptoXSpacing
import com.cryptox.core.designsystem.theme.IconSize
import com.cryptox.core.designsystem.theme.LocalCryptoXColors
import cryptox.core.designsystem.generated.resources.Res
import cryptox.core.designsystem.generated.resources.cx_clear_content_description
import cryptox.core.designsystem.generated.resources.cx_search_content_description
import org.jetbrains.compose.resources.stringResource

// Material Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search

@Composable
fun CryptoXSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoXColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(CornerRadius.chip)
    val borderColor = if (isFocused) colors.accent else colors.border

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, shape)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = CryptoXSpacing.md, vertical = CryptoXSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CryptoXSpacing.sm)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(Res.string.cx_search_content_description),
            tint = if (isFocused) colors.accent else colors.textMuted,
            modifier = Modifier.size(IconSize.medium)
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.accent),
            interactionSource = interactionSource,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (query.isNotBlank()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(IconSize.large)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.cx_clear_content_description),
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        }
    }
}
