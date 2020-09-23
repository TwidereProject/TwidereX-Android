package com.twidere.twiderex.extensions

import android.graphics.Rect
import android.view.Window

val Window.displayFrame: Rect
    get() {
        val rectangle = Rect()
        decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle
    }

val Window.statusBarHeight: Int
    get() = displayFrame.top

