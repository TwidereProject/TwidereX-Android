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
package com.twidere.twiderex.component.lazy.ui

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
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.placeholder.UiStatusPlaceholder
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun LazyUiStatusList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiStatus>,
    state: LazyListState = rememberLazyListState(),
    loadingBetween: List<MicroBlogKey> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onLoadBetweenClicked: (current: MicroBlogKey, next: MicroBlogKey) -> Unit = { _, _ -> },
    // key: ((index: Int) -> Any) = { items.peekOrNull(it)?.hashCode() ?: it },
    header: LazyListScope.() -> Unit = {},
) {
    LazyUiList(
        items = items,
        empty = { EmptyStatusList() },
        loading = { LoadingStatusPlaceholder() }
    ) {
        LazyColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
        ) {
            header.invoke(this)
            itemsIndexed(
                items,
                // key = key
            ) { index, item ->
                if (item == null) {
                    UiStatusPlaceholder()
                    StatusDivider()
                } else {
                    Column {
                        TimelineStatusComponent(
                            item,
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
                id = R.string.common_controls_timeline_load_more
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
            painter = painterResource(id = R.drawable.ic_empty_status),
            contentDescription = stringResource(id = R.string.common_alerts_no_tweets_found_title)
        )
        Spacer(modifier = Modifier.height(EmptyStatusListDefaults.VerticalPadding))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(id = R.string.common_alerts_no_tweets_found_title),
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
