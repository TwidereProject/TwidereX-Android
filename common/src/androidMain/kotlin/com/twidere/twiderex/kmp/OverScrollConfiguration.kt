package com.twidere.twiderex.kmp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.gestures.OverScrollConfiguration
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@ExperimentalFoundationApi
@Composable
actual fun ProvideOverScrollConfiguration(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalOverScrollConfiguration provides OverScrollConfiguration(
            glowColor = MaterialTheme.colors.primary,
        )
    ) {
        content.invoke()
    }
}