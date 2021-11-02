package com.twidere.twiderex.component.foundation

import androidx.compose.runtime.Composable

@Composable
expect fun getLocalView(): LocalView

expect class LocalView() {
    fun performHapticFeedback()
}