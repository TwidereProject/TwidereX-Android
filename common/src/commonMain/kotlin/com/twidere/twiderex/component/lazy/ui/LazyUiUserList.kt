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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.placeholder.UiUserPlaceholder
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.ui.UiUser

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiUserList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiUser>,
    state: LazyListState = rememberLazyListState(),
    key: ((item: UiUser) -> Any) = { it.userKey.hashCode() },
    onItemClicked: (UiUser) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
    action: @Composable (user: UiUser) -> Unit = {}
) {
    LazyUiList(items = items) {
        LazyColumn(
            modifier = modifier,
            state = state,
        ) {
            header.invoke(this)
            items(
                items,
                key = key
            ) {
                it?.let {
                    Row(
                        modifier = Modifier.clickable {
                            onItemClicked.invoke(it)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ListItem(
                            modifier = Modifier.weight(1f),
                            icon = {
                                UserAvatar(
                                    user = it,
                                )
                            },
                            text = {
                                Row {
                                    UserName(user = it)
                                    Spacer(modifier = Modifier.width(UiUserListDefaults.HorizontalPadding))
                                    UserScreenName(user = it)
                                }
                            },
                            secondaryText = {
                                Row {
                                    Text(
                                        text = stringResource(
                                            res = com.twidere.twiderex.MR.strings.common_controls_profile_dashboard_followers,
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(UiUserListDefaults.HorizontalPadding))
                                    Text(
                                        text = it.metrics.fans.toString()
                                    )
                                }
                            },
                        )
                        Box(modifier = Modifier.padding(end = UiUserListDefaults.TrailingRightPadding)) {
                            action.invoke(it)
                        }
                    }
                } ?: run {
                    LoadingUserPlaceholder()
                }
            }
            loadState(items.loadState.append) {
                items.retry()
            }
        }
    }
}

object UiUserListDefaults {
    val HorizontalPadding = 8.dp
    val TrailingRightPadding = 16.dp
}

@Composable
private fun LoadingUserPlaceholder() {
    Column(
        modifier = Modifier
            .wrapContentHeight(
                align = Alignment.Top,
                unbounded = true
            )
    ) {
        repeat(10) {
            UiUserPlaceholder(
                delayMillis = it * 50L
            )
        }
    }
}
