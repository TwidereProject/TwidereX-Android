/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.ui

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.ViewAmbient

val AmbientWindowPadding = ambientOf<PaddingValues>()

@Composable
fun ProvideWindowPadding(
    content: @Composable () -> Unit,
) {
    val density = DensityAmbient.current
    var ignoreWindowInsets by remember { mutableStateOf(false) }
    var windowPadding by remember { mutableStateOf(PaddingValues()) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val view = ViewAmbient.current
        DisposableEffect(subject = view) {
            view.setOnApplyWindowInsetsListener { _, insets ->
                if (!ignoreWindowInsets) {
                    val systemInsets =
                        insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
                    with(density) {
                        windowPadding = PaddingValues(
                            start = systemInsets.left.toDp(),
                            top = systemInsets.top.toDp(),
                            end = systemInsets.right.toDp(),
                            bottom = systemInsets.bottom.toDp(),
                        )
                    }
                }
                insets
            }

            view.setWindowInsetsAnimationCallback(object :
                    WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                    override fun onPrepare(animation: WindowInsetsAnimation) {
                        super.onPrepare(animation)
                        ignoreWindowInsets = true
                    }

                    override fun onEnd(animation: WindowInsetsAnimation) {
                        super.onEnd(animation)
                        ignoreWindowInsets = false
                    }

                    override fun onProgress(
                        insets: WindowInsets,
                        animations: MutableList<WindowInsetsAnimation>
                    ): WindowInsets {
                        val systemInsets =
                            insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
                        with(density) {
                            windowPadding = PaddingValues(
                                start = systemInsets.left.toDp(),
                                top = systemInsets.top.toDp(),
                                end = systemInsets.right.toDp(),
                                bottom = systemInsets.bottom.toDp(),
                            )
                        }
                        return insets
                    }
                })

            val attachListener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) = v.requestApplyInsets()
                override fun onViewDetachedFromWindow(v: View) = Unit
            }
            view.addOnAttachStateChangeListener(attachListener)

            if (view.isAttachedToWindow) {
                view.requestApplyInsets()
            }

            onDispose {
                view.removeOnAttachStateChangeListener(attachListener)
            }
        }
    }
    Providers(
        AmbientWindowPadding provides windowPadding
    ) {
        content.invoke()
    }
}
