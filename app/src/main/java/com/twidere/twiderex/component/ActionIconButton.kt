package com.twidere.twiderex.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonConstants
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ActionIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(ButtonConstants.DefaultMinWidth),
        icon = icon,
    )
}
