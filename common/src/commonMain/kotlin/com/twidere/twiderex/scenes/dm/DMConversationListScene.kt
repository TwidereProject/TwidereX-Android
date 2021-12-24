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
package com.twidere.twiderex.scenes.dm

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiDMConversationList
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.navigation.Root
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
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_messages_title))
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
            navController.navigate(Root.Messages.NewConversation)
        }
    ) {
        Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_add),
            contentDescription = stringResource(
                res = com.twidere.twiderex.MR.strings.scene_lists_icons_create
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
    if (!account.supportDirectMessage) return
    val viewModel: DMConversationViewModel = getViewModel()
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
                navController.navigate(Root.Messages.Conversation(it.conversation.conversationKey))
            }
        )
    }
}
