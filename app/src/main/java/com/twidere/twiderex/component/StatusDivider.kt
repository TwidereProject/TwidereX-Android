package com.twidere.twiderex.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun StatusDivider() {
    Divider(
        modifier = Modifier.padding(
            start = profileImageSize + standardPadding * 3,
            end = standardPadding * 2
        )
    )
}