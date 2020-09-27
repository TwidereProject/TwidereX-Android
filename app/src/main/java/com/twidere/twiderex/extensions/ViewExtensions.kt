package com.twidere.twiderex.extensions

import android.graphics.Insets
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.updateMargins


@RequiresApi(Build.VERSION_CODES.Q)
fun ViewGroup.MarginLayoutParams.updateMargins(insets: Insets) {
    updateMargins(
        left = insets.left,
        top = insets.top,
        bottom = insets.bottom,
        right = insets.right
    )
}
