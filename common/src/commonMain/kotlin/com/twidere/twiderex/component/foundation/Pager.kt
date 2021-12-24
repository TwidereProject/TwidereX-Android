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
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.withSign

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */

@Composable
fun rememberPagerState(
    @IntRange(from = 0) pageCount: Int,
    @IntRange(from = 0) initialPage: Int = 0,
): PagerState {
    return rememberSaveable(
        saver = PagerState.Saver(),
    ) {
        PagerState(pageCount = pageCount, currentPage = initialPage)
    }.apply {
        this.pageCount = pageCount
    }
}

@Stable
class PagerState(
    @IntRange(from = 0) pageCount: Int,
    @IntRange(from = 0) currentPage: Int = 0,
) {
    private val velocityTracker = VelocityTracker()
    private var _pageCount by mutableStateOf(pageCount)
    private var _currentPage by mutableStateOf(currentPage)

    companion object {
        fun Saver(): Saver<PagerState, *> = listSaver(
            save = { listOf(it.pageCount, it.currentPage) },
            restore = {
                PagerState(
                    pageCount = it[0],
                    currentPage = it[1],
                )
            }
        )
    }

    internal inline val firstPageIndex: Int
        get() = 0

    internal inline val lastPageIndex: Int
        get() = (pageCount - 1).coerceAtLeast(0)

    @get:IntRange(from = 0)
    var pageCount: Int
        get() = _pageCount
        set(@IntRange(from = 0) value) {
            require(value >= 0) { "pageCount must be >= 0" }
            _pageCount = value
            currentPage = currentPage.coerceIn(firstPageIndex, lastPageIndex)
            // updateLayoutPages(currentPage)
        }

    private fun Int.floorMod(other: Int): Int {
        return when (other) {
            0 -> this
            else -> this - this.floorDiv(other) * other
        }
    }

    @get:IntRange(from = 0)
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.floorMod(pageCount)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    suspend inline fun <R> selectPage(block: PagerState.() -> R): R = try {
        selectionState = SelectionState.Undecided
        block.invoke(this)
    } finally {
        selectPage()
    }

    suspend fun selectPage() {
        currentPage -= currentPageOffset.roundToInt()
        snapToOffset(0f)
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset = Animatable(0f).apply {
        updateBounds(-1f, 1f)
    }
    val currentPageOffset: Float
        get() = _currentPageOffset.value

    suspend fun snapToOffset(offset: Float) {
        val max = if (currentPage == firstPageIndex) 0f else 1f
        val min = if (currentPage == lastPageIndex) 0f else -1f
        _currentPageOffset.snapTo(offset.coerceIn(min, max))
    }

    suspend fun fling(velocity: Float) {
        if (velocity < 0 && currentPage == lastPageIndex) return
        if (velocity > 0 && currentPage == firstPageIndex) return
        val currentOffset = _currentPageOffset.value
        when {
            currentOffset.sign == velocity.sign &&
                (
                    velocity.absoluteValue > 1.5f ||
                        currentOffset.absoluteValue > 0.5 && currentOffset.absoluteValue < 1f
                    ) -> {
                _currentPageOffset.animateTo(1f.withSign(velocity))
                selectPage()
            }
            else -> {
                _currentPageOffset.animateTo(0f)
                selectPage()
            }
        }
    }

    fun addPosition(uptimeMillis: Long, position: Offset) {
        velocityTracker.addPosition(timeMillis = uptimeMillis, position = position)
    }

    suspend fun dragEnd(pageSize: Int) {
        val velocity = velocityTracker.calculateVelocity()
        fling(velocity.x / pageSize)
    }
}

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
fun Pager(
    modifier: Modifier = Modifier,
    state: PagerState,
    offscreenLimit: Int = 2,
    dragEnabled: Boolean = true,
    content: @Composable PagerScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var pageSize by remember { mutableStateOf(0) }
    Layout(
        content = {
            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.firstPageIndex)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.lastPageIndex)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Box(contentAlignment = Alignment.Center, modifier = pageData) {
                        scope.content()
                    }
                }
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                if (dragEnabled) {
                    detectHorizontalDrag(
                        onHorizontalDrag = { change, dragAmount ->
                            with(state) {
                                selectionState = PagerState.SelectionState.Undecided
                                val pos = pageSize * currentPageOffset
                                val max =
                                    if (currentPage == firstPageIndex) 0 else pageSize * offscreenLimit
                                val min =
                                    if (currentPage == lastPageIndex) 0 else -pageSize * offscreenLimit
                                val newPos =
                                    (pos + dragAmount).coerceIn(min.toFloat(), max.toFloat())
                                if (newPos != 0f) {
                                    change.consumePositionChange()
                                    addPosition(change.uptimeMillis, change.position)
                                    coroutineScope.launch {
                                        snapToOffset(newPos / pageSize)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                state.dragEnd(pageSize)
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                state.dragEnd(pageSize)
                            }
                        },
                    )
                }
            }
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentPageOffset
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            measurables
                .map {
                    it.measure(childConstraints) to it.page
                }
                .forEach { (placeable, page) ->
                    // TODO: current this centers each page. We should investigate reading
                    //  gravity modifiers on the child, or maybe as a param to Pager.
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2

                    if (currentPage == page) {
                        pageSize = placeable.width
                    }

                    val xItemOffset = ((page + offset - currentPage) * placeable.width).roundToInt()

                    placeable.place(
                        x = xCenterOffset + xItemOffset,
                        y = yCenterOffset
                    )
                }
        }
    }
}

/**
 * Scope for [Pager] content.
 */
class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    /**
     * Returns the current selected page
     */
    val currentPage: Int
        get() = state.currentPage

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
        get() = state.currentPageOffset

    /**
     * Returns the current selection state
     */
    val selectionState: PagerState.SelectionState
        get() = state.selectionState
}

private suspend fun PointerInputScope.detectHorizontalDrag(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onHorizontalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            val drag = awaitHorizontalTouchSlopOrCancellation(down.id, onHorizontalDrag)
            if (drag != null) {
                onDragStart.invoke(drag.position)
                if (
                    horizontalDrag(drag.id) {
                        onHorizontalDrag(it, it.positionChange().x)
                    }
                ) {
                    onDragEnd()
                } else {
                    onDragCancel()
                }
            }
        }
    }
}
