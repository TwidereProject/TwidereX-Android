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
package com.twidere.twiderex.component.foundation

import androidx.annotation.IntRange
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
private class TopBarState(
    @IntRange(from = 0) private val initialOffset: Int = 0
) {
    private var _offset by mutableStateOf(initialOffset)
    var size = 0
    var offset: Int
        get() = _offset
        set(value) {
            _offset = value.coerceIn(minimumValue = -size, maximumValue = 0)
        }

    fun scroll(delta: Float) {
        offset += delta.toInt()
    }

    suspend fun fixOffset() {
        val show = offset > -size / 2
        animate(
            initialValue = offset.toFloat(),
            targetValue = if (show) 0f else -size.toFloat(),
            initialVelocity = 0f
        ) { v, _ ->
            offset = v.toInt()
        }
    }

    companion object {
        val Saver: Saver<TopBarState, *> = listSaver(
            save = {
                listOf(
                    it.offset,
                )
            },
            restore = {
                TopBarState(
                    initialOffset = it[0],
                )
            }
        )
    }
}

@Stable
private class BottomBarState(
    @IntRange(from = 0) private val initialOffset: Int = 0
) {
    private var _offset by mutableStateOf(initialOffset)
    var size = 0
    var offset: Int
        get() = _offset
        set(value) {
            _offset = value.coerceIn(maximumValue = size, minimumValue = 0)
        }

    fun scroll(delta: Float) {
        offset -= delta.toInt()
    }

    suspend fun fixOffset() {
        val show = offset < size / 2
        animate(
            initialValue = offset.toFloat(),
            targetValue = if (show) 0f else size.toFloat(),
            initialVelocity = 0f
        ) { v, _ ->
            offset = v.toInt()
        }
    }

    companion object {
        val Saver: Saver<BottomBarState, *> = listSaver(
            save = {
                listOf(
                    it.offset,
                )
            },
            restore = {
                BottomBarState(
                    initialOffset = it[0],
                )
            }
        )
    }
}

@Composable
fun NestedScrollScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    enableTopBarNestedScroll: Boolean = true,
    bottomBar: @Composable () -> Unit = {},
    enableBottomBarNestedScroll: Boolean = true,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable () -> Unit = {},
    enableFloatingActionButtonNestedScroll: Boolean = true,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit
) {
    val topBarState = rememberSaveable(saver = TopBarState.Saver) {
        TopBarState()
    }
    val bottomBarState = rememberSaveable(saver = BottomBarState.Saver) {
        BottomBarState()
    }
    val fabState = rememberSaveable(saver = BottomBarState.Saver) {
        BottomBarState()
    }
    val enableTopBarNestedScrollState = rememberUpdatedState(newValue = enableTopBarNestedScroll)
    val enableBottomBarNestedScrollState =
        rememberUpdatedState(newValue = enableBottomBarNestedScroll)
    val enableFloatingActionButtonNestedScrollState =
        rememberUpdatedState(newValue = enableFloatingActionButtonNestedScroll)

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (enableTopBarNestedScrollState.value) {
                    topBarState.scroll(delta)
                }
                if (enableBottomBarNestedScrollState.value) {
                    bottomBarState.scroll(delta)
                }
                if (enableFloatingActionButtonNestedScrollState.value) {
                    fabState.scroll(delta)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                coroutineScope {
                    if (enableTopBarNestedScrollState.value) {
                        launch {
                            topBarState.fixOffset()
                        }
                    }
                    if (enableBottomBarNestedScrollState.value) {
                        launch {
                            bottomBarState.fixOffset()
                        }
                    }
                    if (enableFloatingActionButtonNestedScrollState.value) {
                        launch {
                            fabState.fixOffset()
                        }
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        scaffoldState = scaffoldState,
        topBar = {
            Box(
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    topBarState.size = placeable.height
                    layout(
                        width = placeable.width,
                        height = placeable.height + topBarState.offset,
                    ) {
                        placeable.placeRelative(0, topBarState.offset)
                    }
                }
            ) {
                topBar.invoke()
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    bottomBarState.size = placeable.height
                    layout(
                        width = placeable.width,
                        height = placeable.height - bottomBarState.offset
                    ) {
                        placeable.placeRelative(0, bottomBarState.offset)
                    }
                }
            ) {
                bottomBar.invoke()
            }
        },
        snackbarHost = snackbarHost,
        floatingActionButton = {
            Box(
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    fabState.size = placeable.height
                    layout(
                        width = placeable.width,
                        height = placeable.height - fabState.offset
                    ) {
                        placeable.placeRelative(0, fabState.offset)
                    }
                }
            ) {
                floatingActionButton.invoke()
            }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerGesturesEnabled = drawerGesturesEnabled,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        drawerBackgroundColor = drawerBackgroundColor,
        drawerContentColor = drawerContentColor,
        drawerScrimColor = drawerScrimColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        content = content,
    )
}
