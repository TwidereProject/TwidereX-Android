package com.twidere.twiderex.component.foundation

import androidx.compose.runtime.Composable

@Composable
actual fun getLocalView(): LocalView {
    return LocalView()
}

actual class LocalView actual constructor() {
    actual fun performHapticFeedback() {}
}