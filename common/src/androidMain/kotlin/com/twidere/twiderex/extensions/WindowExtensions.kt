/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.extensions

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets

fun Window.showControls() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        decorView.removeSystemUiVisibility(FLAG_SYSTEM_UI_HIDE_BARS)
    } else {
        insetsController?.show(WindowInsets.Type.systemBars())
    }
}

fun Window.hideControls() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        decorView.addSystemUiVisibility(FLAG_SYSTEM_UI_HIDE_BARS)
    } else {
        insetsController?.hide(WindowInsets.Type.systemBars())
    }
}

fun Window.setOnSystemBarsVisibilityChangeListener(callback: (visibility: Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        @Suppress("DEPRECATION")
        decorView.setOnSystemUiVisibilityChangeListener {
            callback.invoke(View.SYSTEM_UI_FLAG_FULLSCREEN !in it)
        }
    } else {
        decorView.setOnApplyWindowInsetsListener { _, insets ->
            callback.invoke(
                insets.isVisible(WindowInsets.Type.navigationBars()) ||
                    insets.isVisible(WindowInsets.Type.captionBar()) ||
                    insets.isVisible(WindowInsets.Type.statusBars())
            )
            insets
        }
    }
}

@Suppress("DEPRECATION")
const val FLAG_SYSTEM_UI_HIDE_BARS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
    View.SYSTEM_UI_FLAG_FULLSCREEN
