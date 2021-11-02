package com.twidere.twiderex.component.foundation

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

@Composable
actual fun getLocalView(): LocalView {
    return LocalView().apply {
        view = LocalView.current
    }
}

actual class LocalView actual constructor() {
    var view: View?= null
    actual fun performHapticFeedback() {
        view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
