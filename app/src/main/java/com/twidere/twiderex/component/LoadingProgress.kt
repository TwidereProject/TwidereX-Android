package com.twidere.twiderex.component

import androidx.compose.foundation.layout.ColumnScope.Companion.align
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonConstants
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
 fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .defaultMinSizeConstraints(
                minHeight = ButtonConstants.DefaultMinHeight,
            )
            .padding(ButtonConstants.DefaultContentPadding)
            .align(Alignment.CenterHorizontally),
    )
}
