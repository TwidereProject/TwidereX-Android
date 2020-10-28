package com.twidere.twiderex.component.lazy

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun LazyListScope.itemDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    item {
        Divider(
            modifier = modifier,
            thickness = thickness,
            startIndent = startIndent,
        )
    }
}
