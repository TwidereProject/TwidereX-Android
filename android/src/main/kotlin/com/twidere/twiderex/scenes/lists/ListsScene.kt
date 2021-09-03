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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiListsList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsViewModel
import java.util.Locale

@Composable
fun ListsScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = com.twidere.common.R.string.scene_lists_title))
                    },
                )
            },
            floatingActionButton = {
                ListsSceneFab()
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {
            ListsSceneContent()
        }
    }
}

@Composable
fun ListsSceneFab() {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    FloatingActionButton(
        onClick = {
            when (account.type) {
                PlatformType.Twitter -> navController.navigate(RootRoute.Lists.TwitterCreate)
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> navController.navigate(RootRoute.Lists.MastodonCreateDialog)
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ListsSceneDefaults.Fab.ContentPadding)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = stringResource(
                    id = com.twidere.common.R.string.scene_lists_icons_create
                ),
                modifier = Modifier.padding(ListsSceneDefaults.Fab.IconPadding)
            )
            Text(
                text = stringResource(id = com.twidere.common.R.string.scene_lists_modify_create_title)
                    .uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Composable
fun ListsSceneContent() {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    // if list type is all , display title of each type
    val listsViewMode = assistedViewModel<ListsViewModel.AssistedFactory, ListsViewModel>(
        account,
    ) {
        it.create(account)
    }
    val ownerItems = listsViewMode.ownerSource.collectAsLazyPagingItems()
    val subscribeItems = listsViewMode.subscribedSource.collectAsLazyPagingItems()
    val sourceItems = listsViewMode.source.collectAsLazyPagingItems()
    SwipeToRefreshLayout(
        refreshingState = ownerItems.loadState.refresh is LoadState.Loading,
        onRefresh = { ownerItems.refresh() }
    ) {
        LazyUiListsList(
            listType = account.listType,
            source = sourceItems,
            ownerItems = ownerItems,
            subscribedItems = subscribeItems,
            onItemClicked = { navController.navigate(RootRoute.Lists.Timeline(it.listKey)) }
        )
    }
}

private object ListsSceneDefaults {
    object Fab {
        val ContentPadding = PaddingValues(horizontal = 22.dp)
        val IconPadding = PaddingValues(end = 17.dp)
    }
}
