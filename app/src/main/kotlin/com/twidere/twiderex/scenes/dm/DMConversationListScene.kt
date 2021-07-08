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
package com.twidere.twiderex.scenes.dm

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiDMConversationList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.dm.DMConversationViewModel

@Composable
fun DMConversationListScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_messages_title))
                    },
                )
            },
            floatingActionButton = {
                DMConversationListSceneFab()
            },
        ) {
            DMConversationListSceneContent()
        }
    }
}

@Composable
fun DMConversationListSceneFab() {
    val navController = LocalNavController.current
    FloatingActionButton(
        onClick = {
            navController.navigate(Route.Messages.NewConversation)
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = stringResource(
                id = R.string.scene_lists_icons_create
            ),
        )
    }
}

@Composable
fun DMConversationListSceneContent(
    lazyListController: LazyListController? = null
) {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val viewModel =
        assistedViewModel<DMConversationViewModel.AssistedFactory, DMConversationViewModel>(
            account,
        ) {
            it.create(account)
        }
    val source = viewModel.source.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    LaunchedEffect(lazyListController) {
        lazyListController?.listState = listState
    }
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = { source.refresh() }
    ) {
        LazyUiDMConversationList(
            items = source,
            state = listState,
            onItemClicked = {
                navController.navigate(Route.Messages.Conversation(it.conversation.conversationKey))
            }
        )
    }
}
