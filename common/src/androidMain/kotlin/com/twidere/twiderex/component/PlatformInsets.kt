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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

val LocalWindowInsetsController =
    staticCompositionLocalOf<WindowInsetsControllerCompat> { error("No WindowInsetsControllerCompat") }

actual fun Modifier.topInsetsPadding(): Modifier = this.statusBarsPadding()
actual fun Modifier.bottomInsetsPadding(): Modifier = this.navigationBarsPadding()
actual fun Modifier.startInsetsPadding(): Modifier = this
actual fun Modifier.endInsetsPadding(): Modifier = this

actual fun Modifier.topInsetsHeight(): Modifier = composed { this.windowInsetsTopHeight(WindowInsets.statusBars) }
actual fun Modifier.bottomInsetsHeight(): Modifier = composed { this.windowInsetsBottomHeight(WindowInsets.navigationBars) }
actual fun Modifier.startInsetsWidth(): Modifier = composed { this.windowInsetsStartWidth(WindowInsets.navigationBars) }
actual fun Modifier.endInsetsWidth(): Modifier = composed { this.windowInsetsEndWidth(WindowInsets.navigationBars) }

@Composable
actual fun PlatformInsets(
    control: NativeInsetsControl,
    color: NativeInsetsColor,
    content: @Composable () -> Unit,
) {
    val darkTheme = control.darkTheme
    val windowInsetsController = LocalWindowInsetsController.current
    LaunchedEffect(darkTheme) {
        windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }
    Box {
        val actual = provideSystemInsets(
            control.extendToBottom,
            control.extendToTop,
        )
        Box(
            modifier = Modifier
                .padding(actual.asPaddingValues())
                .align(Alignment.Center)
        ) {
            content()
        }
        if (!control.extendToTop) {
            Spacer(
                modifier = Modifier
                    .topInsetsHeight()
                    .zIndex(999F)
                    .fillMaxWidth()
                    .background(color.top)
                    .align(Alignment.TopCenter)
            )
        }
        if (!control.extendToStart) {
            Spacer(
                modifier = Modifier
                    .startInsetsWidth()
                    .zIndex(999F)
                    .fillMaxHeight()
                    .background(color.start)
                    .align(Alignment.CenterStart)
            )
        }
        if (!control.extendToEnd) {
            Spacer(
                modifier = Modifier
                    .endInsetsWidth()
                    .zIndex(999F)
                    .fillMaxHeight()
                    .background(color.end)
                    .align(Alignment.CenterEnd)
            )
        }
        if (!control.extendToBottom) {
            Spacer(
                modifier = Modifier
                    .bottomInsetsHeight()
                    .zIndex(999F)
                    .fillMaxWidth()
                    .background(color.bottom)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
actual fun ImeVisibleWithInsets(
    filter: ((Boolean) -> Boolean)?,
    collectIme: ((Boolean) -> Unit)?
) {
    val density = LocalDensity.current
    val ime = WindowInsets.ime
    val navigation = WindowInsets.navigationBars
    LaunchedEffect(ime) {
        snapshotFlow { ime.getBottom(density) > navigation.getBottom(density) }
            .distinctUntilChanged()
            .filter { filter?.invoke(it) ?: false }
            .collect {
                collectIme?.invoke(it)
            }
    }
}

@Composable
actual fun ImeHeightWithInsets(
    filter: ((Int) -> Boolean)?,
    collectIme: ((Int) -> Unit)?
) {
    val density = LocalDensity.current
    val ime = WindowInsets.ime
    LaunchedEffect(ime) {
        snapshotFlow { ime.getBottom(density) }
            .distinctUntilChanged()
            .filter { filter?.invoke(it) ?: false }
            .collect {
                collectIme?.invoke(it)
            }
    }
}

@Composable
actual fun ImeBottomInsets(): Dp {
    val density = LocalDensity.current
    val ime = WindowInsets.ime
    val navigation = WindowInsets.navigationBars
    ime.getBottom(density).coerceAtLeast(navigation.getBottom(density))
    return with(LocalDensity.current) {
        ime.getBottom(density).coerceAtLeast(navigation.getBottom(density)).toDp()
    }
}

@Composable
private fun provideSystemInsets(
    extendViewIntoNavigationBar: Boolean,
    extendViewIntoStatusBar: Boolean,
): WindowInsets {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val ime = WindowInsets.ime
    val navigation = WindowInsets.navigationBars
    val status = WindowInsets.statusBars
    return WindowInsets(
        left = if (extendViewIntoNavigationBar) {
            0
        } else {
            ime.getLeft(density, layoutDirection).coerceAtLeast(
                navigation.getLeft(density, layoutDirection)
            )
        },
        right = if (extendViewIntoNavigationBar) {
            0
        } else {
            ime.getRight(density, layoutDirection).coerceAtLeast(
                navigation.getRight(density, layoutDirection)
            )
        },
        bottom = if (extendViewIntoNavigationBar) {
            0
        } else {
            ime.getBottom(density).coerceAtLeast(
                navigation.getBottom(density)
            )
        },
        top = if (extendViewIntoNavigationBar) {
            0
        } else {
            ime.getTop(density).coerceAtLeast(
                navigation.getTop(density)
            )
        } + if (extendViewIntoStatusBar) {
            0
        } else {
            status.getTop(density)
        },
    )
}
