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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

data class NativeInsetsControl(
    val extendToTop: Boolean = false,
    val extendToBottom: Boolean = false,
    val extendToStart: Boolean = false,
    val extendToEnd: Boolean = false,
    val darkTheme: Boolean = false,
)

data class NativeInsetsColor(
    val top: Color = Color.Transparent,
    val bottom: Color = Color.Transparent,
    val start: Color = Color.Transparent,
    val end: Color = Color.Transparent,
)

expect fun Modifier.topInsetsPadding(): Modifier
expect fun Modifier.bottomInsetsPadding(): Modifier
expect fun Modifier.startInsetsPadding(): Modifier
expect fun Modifier.endInsetsPadding(): Modifier

expect fun Modifier.topInsetsHeight(): Modifier
expect fun Modifier.bottomInsetsHeight(): Modifier
expect fun Modifier.startInsetsWidth(): Modifier
expect fun Modifier.endInsetsWidth(): Modifier

@Composable
internal expect fun PlatformInsets(
    control: NativeInsetsControl = NativeInsetsControl(),
    color: NativeInsetsColor = NativeInsetsColor(),
    content: @Composable () -> Unit,
)

@Composable
expect fun ImeVisibleWithInsets(
    filter: ((Boolean) -> Boolean)? = null,
    collectIme: ((Boolean) -> Unit)? = null
)

@Composable
expect fun ImeHeightWithInsets(
    filter: ((Int) -> Boolean)? = null,
    collectIme: ((Int) -> Unit)? = null
)

@Composable
expect fun ImeBottomInsets(): Dp
