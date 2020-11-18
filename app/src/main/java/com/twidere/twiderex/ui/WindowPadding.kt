/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.ui

import android.os.Build
import android.view.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.ViewAmbient
import com.twidere.twiderex.utils.RootViewDeferringInsetsCallback

val AmbientWindowPadding = ambientOf<PaddingValues>()

@Composable
fun ProvideWindowPadding(
    content: @Composable () -> Unit,
) {
    var windowPadding by remember { mutableStateOf(PaddingValues()) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val view = ViewAmbient.current
        val density = DensityAmbient.current
        val deferringInsetsListener = remember {
            RootViewDeferringInsetsCallback(
                persistentInsetTypes = WindowInsets.Type.systemBars(),
                deferredInsetTypes = WindowInsets.Type.ime()
            ) { left, top, right, bottom ->
                with(density) {
                    windowPadding = PaddingValues(
                        start = left.toDp(),
                        top = top.toDp(),
                        end = right.toDp(),
                        bottom = bottom.toDp(),
                    )
                }
            }
        }
        onCommit(view) {
            view.setWindowInsetsAnimationCallback(deferringInsetsListener)
            view.setOnApplyWindowInsetsListener(deferringInsetsListener)
        }
    }
    Providers(
        AmbientWindowPadding provides windowPadding
    ) {
        content.invoke()
    }
}
