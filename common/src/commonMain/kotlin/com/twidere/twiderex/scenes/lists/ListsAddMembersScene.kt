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
package com.twidere.twiderex.scenes.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarDefaults
import com.twidere.twiderex.component.foundation.Dialog
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.ui.LazyUiUserList
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsAddMemberViewModel
import com.twidere.twiderex.viewmodel.lists.ListsSearchUserViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ListsAddMembersScene(
    listKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel: ListsAddMemberViewModel = getViewModel {
        parametersOf(listKey.id)
    }

    val loading by viewModel.loading.observeAsState(initial = false)

    val onlySearchFollowing = when (account.type) {
        PlatformType.Mastodon -> true
        else -> false
    }

    val searchViewModel: ListsSearchUserViewModel = getViewModel {
        parametersOf(onlySearchFollowing)
    }

    val keyword by searchViewModel.text.observeAsState(initial = "")
    val searchSource = searchViewModel.source.collectAsLazyPagingItems()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                Surface(elevation = AppBarDefaults.TopAppBarElevation) {
                    Column {
                        AppBar(
                            navigationIcon = {
                                val navController = LocalNavController.current
                                IconButton(
                                    onClick = {
                                        navController.goBackWith(viewModel.pendingMap.values.toList())
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_back)
                                    )
                                }
                            },
                            title = {
                                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_users_add_title))
                            },
                            elevation = 0.dp,
                        )
                        Row(
                            modifier = Modifier.padding(ListsAddMembersSceneDefaults.SearchInput.ContentPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_search),
                                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.scene_search_title),
                                modifier = Modifier.padding(ListsAddMembersSceneDefaults.SearchInput.Icon.Padding)
                            )
                            TextInput(
                                value = keyword,
                                placeholder = {
                                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_users_add_search))
                                },
                                onValueChange = {
                                    searchViewModel.text.value = it
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // search result
                SearchResultsContent(
                    source = searchSource,
                    onAction = {
                        viewModel.addToOrRemove(it)
                    },
                    statusChecker = { viewModel.isInPendingList(it) }
                )

                if (loading) {
                    Dialog(onDismissRequest = { }) {
                        LoadingProgress()
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsContent(source: LazyPagingItems<UiUser>, onAction: (user: UiUser) -> Unit, statusChecker: (user: UiUser) -> Boolean) {
    val navigator = LocalNavigator.current
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        LazyUiUserList(
            items = source,
            onItemClicked = { navigator.user(it) },
            action = {
                TextButton(
                    onClick = {
                        onAction(it)
                    }
                ) {
                    val pending = statusChecker(it)
                    if (pending) {
                        Text(
                            text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_users_menu_actions_remove),
                            style = MaterialTheme.typography.button,
                            color = Color(0xFFFF3B30),
                        )
                    } else {
                        Text(
                            text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_users_menu_actions_add),
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        )
    }
}

private object ListsAddMembersSceneDefaults {
    object SearchInput {
        val ContentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        )
        object Icon {
            val Padding = PaddingValues(end = 23.dp)
        }
    }
}
