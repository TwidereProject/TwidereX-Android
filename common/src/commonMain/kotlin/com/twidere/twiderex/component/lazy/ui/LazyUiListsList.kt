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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.enums.ListType
import com.twidere.twiderex.model.ui.UiList
import moe.tlaster.placeholder.TextPlaceHolder
import java.util.Locale

/**
 *  LazyList for UiList
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiListsList(
    listType: ListType,
    modifier: Modifier = Modifier,
    source: LazyPagingItems<UiList>,
    ownerItems: LazyPagingItems<UiList>,
    subscribedItems: LazyPagingItems<UiList>,
    state: LazyListState = rememberLazyListState(),
    onItemClicked: (UiList) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
) {
    LazyUiList(
        items = source,
        empty = { EmptyList() },
        loading = { LoadingListsPlaceholder() }
    ) {
        LazyColumn(
            modifier = modifier,
            state = state,
        ) {
            header.invoke(this)
            // my lists title
            if (listType == ListType.All) {
                item {
                    LazyUiListTitleItem(
                        title = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_tabs_created).uppercase(
                            Locale.getDefault()
                        )
                    )
                }
            }
            if (listType == ListType.All || listType == ListType.Owned) {
                items(
                    ownerItems,
                    key = { it.listKey.hashCode() }
                ) {
                    if (it != null) {
                        LazyUiListItem(
                            uiList = it,
                            onItemClicked = onItemClicked
                        )
                    } else {
                        LazyUiListItemPlaceHolder()
                    }
                }
                loadState(ownerItems.loadState.append) {
                    ownerItems.retry()
                }
            }
            if (listType == ListType.All) {
                item {
                    LazyUiListTitleItem(
                        title = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_tabs_subscribed).uppercase(
                            Locale.getDefault()
                        ),
                        divider = true
                    )
                }
            }

            if (listType == ListType.All || listType == ListType.Subscribed) {
                items(
                    subscribedItems,
                    key = { it.listKey.hashCode() }
                ) {
                    if (it != null) {
                        LazyUiListItem(
                            uiList = it,
                            onItemClicked = onItemClicked
                        )
                    } else {
                        LazyUiListItemPlaceHolder()
                    }
                }
                loadState(subscribedItems.loadState.append) {
                    subscribedItems.retry()
                }
            }
        }
    }
}

@Composable
fun LoadingListsPlaceholder() {
    Column(
        modifier = Modifier
            .wrapContentHeight(
                align = Alignment.Top,
                unbounded = true
            )
    ) {
        repeat(10) {
            LazyUiListItemPlaceHolder(
                delayMillis = it * 50L
            )
            StatusDivider()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LazyUiListItemPlaceHolder(
    delayMillis: Long = 0,
) {
    DividerListItem {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            TextPlaceHolder(length = 10, delayMillis = delayMillis)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LazyUiListItem(uiList: UiList, onItemClicked: (UiList) -> Unit = {}) {
    DividerListItem(
        modifier = Modifier.clickable {
            onItemClicked.invoke(uiList)
        },
        trailing = {
            if (uiList.isPrivate) {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_lock),
                    contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_icons_private),
                    modifier = Modifier
                        .alpha(ContentAlpha.disabled)
                        .size(LazyUiListsItemDefaults.LockIconSize)
                )
            }
        }
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = uiList.title,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LazyUiListTitleItem(title: String, divider: Boolean = false) {
    DividerListItem(divider = divider) {
        Text(
            text = title,
            style = MaterialTheme.typography.button,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DividerListItem(
    modifier: Modifier = Modifier,
    divider: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    Column {
        if (divider) {
            Divider(
                Modifier.padding(start = LazyUiListsItemDefaults.HorizontalPadding),
                thickness = LazyUiListsItemDefaults.DividerThickness
            )
        }
        ListItem(
            modifier = modifier,
            text = text,
            trailing = trailing
        )
    }
}

@Composable
private fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_empty_list),
            contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.common_alerts_no_tweets_found_title)
        )
    }
}

private object LazyUiListsItemDefaults {
    val HorizontalPadding = 16.dp
    val LockIconSize = 16.dp
    val DividerThickness = 1.dp
}
