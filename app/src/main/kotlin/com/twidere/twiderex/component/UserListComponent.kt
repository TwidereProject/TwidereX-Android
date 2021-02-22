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
package com.twidere.twiderex.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.LoadState
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.itemsPaging
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.viewmodel.UserListViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserListComponent(
    viewModel: UserListViewModel,
) {
    val source = viewModel.source.collectAsLazyPagingItems()
    val navigator = LocalNavigator.current
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        if (source.itemCount > 0) {
            LazyColumn {
                itemsPaging(source) {
                    it?.let {
                        ListItem(
                            modifier = Modifier.clickable {
                                navigator.user(it)
                            },
                            icon = {
                                UserAvatar(
                                    user = it,
                                )
                            },
                            text = {
                                Row {
                                    Text(
                                        text = it.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colors.primary,
                                    )
                                    Text(
                                        text = "@${it.screenName}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = mediumEmphasisContentContentColor,
                                    )
                                }
                            },
                            secondaryText = {
                                Text(
                                    text = stringResource(
                                        id = R.string.common_controls_profile_dashboard_followers,
                                    ) + " " + it.followersCount.toString()
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
