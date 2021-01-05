/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.utils

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
class RootViewDeferringInsetsCallback(
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int,
    private val applyPadding: (left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) : WindowInsetsAnimation.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    View.OnApplyWindowInsetsListener {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                " same WindowInsets.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsets? = null
    private var deferredInsets = false

    override fun onApplyWindowInsets(v: View, windowInsets: WindowInsets): WindowInsets {
        view = v
        lastWindowInsets = windowInsets

        val types = when {
            deferredInsets -> persistentInsetTypes
            else -> persistentInsetTypes or deferredInsetTypes
        }

        val typeInsets = windowInsets.getInsets(types)
        applyPadding.invoke(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return WindowInsets.CONSUMED
    }

    override fun onPrepare(animation: WindowInsetsAnimation) {
        if (animation.typeMask and deferredInsetTypes != 0) {
            deferredInsets = true
        }
    }

    override fun onProgress(
        insets: WindowInsets,
        runningAnims: List<WindowInsetsAnimation>
    ): WindowInsets {
        val typeInsets = insets.getInsets(persistentInsetTypes or deferredInsetTypes)
        applyPadding.invoke(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        if (deferredInsets && (animation.typeMask and deferredInsetTypes) != 0) {
            deferredInsets = false
            if (lastWindowInsets != null) {
                view?.dispatchApplyWindowInsets(lastWindowInsets!!)
            }
        }
    }
}
