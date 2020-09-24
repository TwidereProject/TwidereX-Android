package com.twidere.twiderex.extensions

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color


@ColorInt
fun Color.toColorInt() = argb(alpha, red, green, blue)

@ColorInt
private fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
    return (alpha * 255.0f + 0.5f).toInt() shl 24 or
            ((red * 255.0f + 0.5f).toInt() shl 16) or
            ((green * 255.0f + 0.5f).toInt() shl 8) or
            (blue * 255.0f + 0.5f).toInt()
}