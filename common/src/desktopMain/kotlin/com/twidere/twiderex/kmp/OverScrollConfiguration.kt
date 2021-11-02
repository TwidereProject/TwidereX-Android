package com.twidere.twiderex.kmp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable

@ExperimentalFoundationApi
@Composable
actual fun ProvideOverScrollConfiguration(
    content: @Composable () -> Unit
) {
    content.invoke()
}