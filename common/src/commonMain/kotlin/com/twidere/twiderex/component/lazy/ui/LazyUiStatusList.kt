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
package com.twidere.twiderex.component.lazy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.placeholder.UiStatusPlaceholder
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusThreadStyle
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Stable
class LazyUiStatusListState(
    initialStatusKey: MicroBlogKey? = null,
    initialShowCursor: Boolean = false,
) {
    private var _statusKey by mutableStateOf(initialStatusKey)
    private var _showCursor by mutableStateOf(initialShowCursor)
    val showCursor get() = _showCursor
    val statusKey get() = _statusKey
    fun update(newKey: MicroBlogKey) {
        if (!showCursor) {
            _showCursor = statusKey != null && statusKey != newKey
            _statusKey = newKey
        }
    }

    fun hide() {
        _showCursor = false
    }

    companion object {
        val Saver: Saver<LazyUiStatusListState, *> = listSaver(
            save = { listOfNotNull<Any>(it.showCursor, it.statusKey?.toString()) },
            restore = {
                LazyUiStatusListState(
                    initialShowCursor = it[0] as Boolean,
                    initialStatusKey = it.getOrNull(1)?.let { MicroBlogKey.valueOf(it.toString()) }
                )
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LazyUiStatusList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiStatus>,
    state: LazyListState = rememberLazyListState(),
    loadingBetween: List<MicroBlogKey> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onLoadBetweenClicked: (current: MicroBlogKey, next: MicroBlogKey) -> Unit = { _, _ -> },
    key: ((index: Int, item: UiStatus) -> Any) = { _, item -> item.statusKey.hashCode() },
    header: LazyListScope.() -> Unit = {},
) {
    val listState = rememberSaveable(saver = LazyUiStatusListState.Saver) {
        LazyUiStatusListState()
    }
    LaunchedEffect(Unit) {
        snapshotFlow { items.itemCount }
            .filter { it > 0 }
            .mapNotNull { items.peek(0)?.statusKey }
            .distinctUntilChanged()
            .collect {
                listState.update(it)
            }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect {
                if (it == 0) {
                    listState.hide()
                }
            }
    }
    LazyUiList(
        items = items,
        empty = { EmptyStatusList() },
        loading = { LoadingStatusPlaceholder() }
    ) {
        Box {
            LazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
            ) {
                header.invoke(this)
                itemsIndexed(
                    items,
                    key = key
                ) { index, item ->
                    if (item == null) {
                        UiStatusPlaceholder()
                        StatusDivider()
                    } else {
                        Column {
                            TimelineStatusComponent(
                                item,
                                threadStyle = StatusThreadStyle.WITH_AVATAR,
                                lineUp = index > 0 && items.peek(index - 1)?.statusId == item.inReplyToStatusId,
                                lineDown = index < items.itemCount - 1 && items.peek(index + 1)?.inReplyToStatusId == item.statusId,
                            )
                            when {
                                loadingBetween.contains(item.statusKey) -> {
                                    Divider()
                                    LoadingProgress(
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Divider()
                                }
                                item.isGap -> {
                                    LoadMoreButton(items, index, onLoadBetweenClicked, item)
                                }
                                else -> {
                                    StatusDivider()
                                }
                            }
                        }
                    }
                }
                loadState(items.loadState.append) {
                    items.retry()
                }
            }
            Box(
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                AnimatedVisibility(
                    visible = listState.showCursor,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
                    exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut(),
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.small,
                            contentColor = MaterialTheme.colors.background,
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                text = state.firstVisibleItemIndex.toString(),
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadMoreButton(
    items: LazyPagingItems<UiStatus>,
    index: Int,
    onLoadBetweenClicked: (current: MicroBlogKey, next: MicroBlogKey) -> Unit,
    item: UiStatus
) {
    Box(
        modifier = Modifier
            .background(LocalContentColor.current.copy(alpha = 0.04f))
            .clickable {
                items
                    .peek(index + 1)
                    ?.let { next ->
                        onLoadBetweenClicked(
                            item.statusKey,
                            next.statusKey,
                        )
                    }
            }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(
                res = com.twidere.twiderex.MR.strings.common_controls_timeline_load_more
            ),
            color = MaterialTheme.colors.primary,
        )
    }
}

@Composable
private fun EmptyStatusList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_empty_status),
            contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.common_alerts_no_tweets_found_title)
        )
        Spacer(modifier = Modifier.height(EmptyStatusListDefaults.VerticalPadding))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(res = com.twidere.twiderex.MR.strings.common_alerts_no_tweets_found_title),
                style = MaterialTheme.typography.h6
            )
        }
    }
}

private object EmptyStatusListDefaults {
    val VerticalPadding = 48.dp
}

@Composable
private fun LoadingStatusPlaceholder() {
    Column(
        modifier = Modifier
            .wrapContentHeight(
                align = Alignment.Top,
                unbounded = true
            )
    ) {
        repeat(10) {
            UiStatusPlaceholder(
                delayMillis = it * 50L
            )
            StatusDivider()
        }
    }
}
