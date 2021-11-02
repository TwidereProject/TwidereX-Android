package com.twidere.twiderex.kmp

import androidx.compose.runtime.Composable

@Composable
expect fun ProvideOverScrollConfiguration(
    content: @Composable () -> Unit
)