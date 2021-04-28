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
package com.twidere.twiderex.scenes.lists

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsModifyViewModel
import com.twidere.twiderex.viewmodel.lists.ListsTimelineViewModel

// Done title and lock icon
// Done status timeline
// Done refactor ListsModifyViewModel to get a source in order to update source
// Todo  Empty tweets
// Todo  DropDownMenus, include:members, subscribers, add members, editlist
// Todo  update this page when source has updated
@Composable
fun ListTimeLineScene(
    listKey: MicroBlogKey
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<ListsModifyViewModel.AssistedFactory, ListsModifyViewModel>(
        account,
    ) {
        it.create(account, listKey)
    }
    val source by viewModel.source.observeAsState()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = source?.title ?: stringResource(id = R.string.scene_lists_details_title))
                            if (source?.isPrivate == true)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = stringResource(id = R.string.scene_lists_icons_private),
                                    modifier = Modifier
                                        .alpha(ContentAlpha.disabled)
                                        .padding(start = ListTimelineSceneDefaults.LockIconPadding)
                                        .size(ListTimelineSceneDefaults.LockIconSize)
                                )
                        }
                    },
                    actions = {
                        var menuExpand by remember {
                            mutableStateOf(false)
                        }
                        IconButton(onClick = { menuExpand = !menuExpand }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(
                                    id = R.string.scene_lists_details_menu_actions_edit_list
                                )
                            )
                        }
                        source?.let { uiList ->
                            DropdownMenu(
                                expanded = menuExpand,
                                onDismissRequest = { menuExpand = false }
                            ) {
                                if (!uiList.isOwner(account.user.userId)) {
                                    val following = uiList.isFollowed
                                    DropdownMenuItem(
                                        onClick = {
                                            /*TODO follow/unfollow*/
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(
                                                id = if (following)
                                                    R.string.scene_lists_details_menu_actions_unfollow
                                                else
                                                    R.string.scene_lists_details_menu_actions_follow
                                            )
                                        )
                                    }
                                }

                                DropdownMenuItem(onClick = { /*TODO view members*/ }) {
                                    Text(text = stringResource(id = R.string.scene_lists_details_tabs_members))
                                }

                                if (uiList.allowToSubscribe) {
                                    DropdownMenuItem(onClick = { /*TODO view subscribers*/ }) {
                                        Text(text = stringResource(id = R.string.scene_lists_details_tabs_subscriber))
                                    }
                                }

                                if (uiList.isOwner(account.user.userId)) {
                                    DropdownMenuItem(onClick = { /*TODO edit list*/ }) {
                                        Text(text = stringResource(id = R.string.scene_lists_details_menu_actions_edit_list))
                                    }
                                }
                            }
                        }
                    },
                )
            },
        ) {
            ListTimelineComponent(account, listKey)
        }
    }
}

@Composable
fun ListTimelineComponent(account: AccountDetails, listKey: MicroBlogKey) {
    val viewModel = assistedViewModel<ListsTimelineViewModel.AssistedFactory, ListsTimelineViewModel>(
        account, listKey
    ) {
        it.create(account, listKey)
    }
    val timelineSource = viewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    SwipeToRefreshLayout(
        refreshingState = timelineSource.loadState.refresh is LoadState.Loading,
        onRefresh = { timelineSource.refresh() }
    ) {
        LazyUiStatusList(
            items = timelineSource,
        )
    }
}

object ListTimelineSceneDefaults {
    val LockIconSize = 24.dp
    val LockIconPadding = 8.dp
}
