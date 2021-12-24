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
package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual fun Modifier.topInsetsPadding(): Modifier = this
actual fun Modifier.bottomInsetsPadding(): Modifier = this
actual fun Modifier.startInsetsPadding(): Modifier = this
actual fun Modifier.endInsetsPadding(): Modifier = this

actual fun Modifier.topInsetsHeight(): Modifier = this
actual fun Modifier.bottomInsetsHeight(): Modifier = this
actual fun Modifier.startInsetsWidth(): Modifier = this
actual fun Modifier.endInsetsWidth(): Modifier = this

@Composable
actual fun PlatformInsets(
    control: NativeInsetsControl,
    color: NativeInsetsColor,
    content: @Composable () -> Unit,
) {
    // TODO: implementation
    content.invoke()
}

@Composable
actual fun ImeVisibleWithInsets(
    filter: ((Boolean) -> Boolean)?,
    collectIme: ((Boolean) -> Unit)?
) {
}

@Composable
actual fun ImeHeightWithInsets(
    filter: ((Int) -> Boolean)?,
    collectIme: ((Int) -> Unit)?
) {
}

@Composable
actual fun ImeBottomInsets(): Dp {
    return 0.dp
}
