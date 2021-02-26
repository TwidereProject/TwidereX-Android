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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.lazy.LazyPagingItems
import com.twidere.twiderex.component.lazy.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.lazy.statusesIndexed
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.standardPadding

@Composable
fun LazyUiStatusList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiStatus>,
    state: LazyListState = rememberLazyListState(),
    loadingBetween: List<MicroBlogKey> = emptyList(),
    onLoadBetweenClicked: (current: MicroBlogKey, next: MicroBlogKey) -> Unit = { _, _ -> },
    key: ((index: Int) -> Any) = { items[it]?.statusKey.hashCode() },
) {
    LazyColumn2(
        modifier = modifier,
        state = state
    ) {
        statusesIndexed(items, key = key) { index, item ->
            if (item == null) {
                TimelineStatusComponent(data = UiStatus.placeHolder())
            } else {
                Column {
                    TimelineStatusComponent(
                        item,
                    )
                    when {
                        loadingBetween.contains(item.statusKey) -> {
                            Divider()
                            LoadingProgress()
                            Divider()
                        }
                        item.isGap -> {
                            Divider()
                            LoadMoreButton(items, index, onLoadBetweenClicked, item)
                            Divider()
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

@Composable
private fun LoadMoreButton(
    items: LazyPagingItems<UiStatus>,
    index: Int,
    onLoadBetweenClicked: (current: MicroBlogKey, next: MicroBlogKey) -> Unit,
    item: UiStatus
) {
    TextButton(
        modifier = Modifier
            .defaultMinSize(
                minHeight = ButtonDefaults.MinHeight,
            )
            .padding(ButtonDefaults.ContentPadding)
            .fillMaxWidth(),
        onClick = {
            items[index + 1]?.let { next ->
                onLoadBetweenClicked(
                    item.statusKey,
                    next.statusKey,
                )
            }
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_refresh),
            contentDescription = stringResource(
                id = R.string.accessibility_scene_timeline_load_gap
            )
        )
        Box(modifier = Modifier.width(standardPadding))
        Text(text = stringResource(id = R.string.common_controls_timeline_load_more))
    }
}
