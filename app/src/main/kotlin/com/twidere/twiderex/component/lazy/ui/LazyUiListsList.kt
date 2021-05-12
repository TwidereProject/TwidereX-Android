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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.lazy.LazyPagingItems
import com.twidere.twiderex.component.lazy.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.model.ListType
import com.twidere.twiderex.model.ui.UiList
import java.util.Locale

/**
 *  LazyList for UiList
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiListList(
    listType: ListType,
    modifier: Modifier = Modifier,
    source: LazyPagingItems<UiList>,
    ownerItems: LazyPagingItems<UiList>,
    subscribedItems: LazyPagingItems<UiList>,
    state: LazyListState = rememberLazyListState(),
    onItemClicked: (UiList) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
) {
    LazyUiList(items = source, empty = { EmptyList() }) {
        LazyColumn2(
            modifier = modifier,
            state = state,
        ) {
            header.invoke(this)
            // my lists title
            if (listType == ListType.All) {
                item {
                    LazyUiListTitleItem(title = stringResource(id = R.string.scene_lists_tabs_created).toUpperCase(
                        Locale.getDefault()))
                }
            }
            if (listType == ListType.All || listType == ListType.Owned) {
                items(ownerItems, key = { ownerItems.peekOrNull(it)?.listKey?.hashCode() ?: it }) {
                    LazyUiListItem(
                        uiList = (it ?: UiList.placeHolder()),
                        onItemClicked = onItemClicked
                    )
                }
                loadState(ownerItems.loadState.append) {
                    ownerItems.retry()
                }
            }
            if (listType == ListType.All) {
                item {
                    LazyUiListTitleItem(title = stringResource(id = R.string.scene_lists_tabs_subscribed).toUpperCase(
                        Locale.getDefault()))
                }
            }

            if (listType == ListType.All || listType == ListType.Subscribed) {
                items(
                    subscribedItems,
                    key = { subscribedItems.peekOrNull(it)?.listKey?.hashCode() ?: it }
                ) {
                    LazyUiListItem(
                        uiList = (it ?: UiList.placeHolder()),
                        onItemClicked = onItemClicked
                    )
                }
                loadState(subscribedItems.loadState.append) {
                    subscribedItems.retry()
                }
            }
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
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = stringResource(id = R.string.scene_lists_icons_private),
                    modifier = Modifier
                        .alpha(ContentAlpha.disabled)
                        .size(LazyUiListItemDefaults.LockIconSize)
                )
            }
        }
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(text = uiList.title, style = MaterialTheme.typography.body1)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LazyUiListTitleItem(title: String) {
    DividerListItem {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(text = title, style = MaterialTheme.typography.overline)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DividerListItem(
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    Column {
        ListItem(
            modifier = modifier,
            text = text,
            trailing = trailing
        )
        Divider(
            Modifier.padding(start = LazyUiListItemDefaults.HorizontalPadding),
            thickness = LazyUiListItemDefaults.DividerThickness
        )
    }
}

@Preview
@Composable
private fun PreviewLazyListItem() {
    LazyUiListItem(uiList = UiList.sample())
}

@Composable
private fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_empty_list),
            contentDescription = stringResource(id = R.string.common_alerts_no_tweets_found_title)
        )
    }
}

object LazyUiListItemDefaults {
    val HorizontalPadding = 16.dp
    val LockIconSize = 16.dp
    val DividerThickness = 1.dp
}
