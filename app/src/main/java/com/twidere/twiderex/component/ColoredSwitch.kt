package com.twidere.twiderex.component

import androidx.compose.foundation.InteractionState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ColoredSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionState: InteractionState = remember { InteractionState() },
    colors: SwitchColors = SwitchConstants.defaultColors(
        checkedThumbColor = MaterialTheme.colors.primaryVariant,
    )
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionState = interactionState,
        colors = colors,
    )
}