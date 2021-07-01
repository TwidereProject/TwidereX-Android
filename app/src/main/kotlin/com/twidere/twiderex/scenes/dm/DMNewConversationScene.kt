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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.ui.LazyUiUserList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.dm.DMNewConversationViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@Composable
fun DMNewConversationScene() {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val viewModel = assistedViewModel<DMNewConversationViewModel.AssistedFactory, DMNewConversationViewModel>(
        account
    ) {
        it.create(account)
    }
    val keyWord by viewModel.input.observeAsState("")
    val sourceState by viewModel.sourceFlow.collectAsState(initial = null)
    val searchSource = sourceState?.collectAsLazyPagingItems()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                Column {
                    AppBar(
                        navigationIcon = {
                            AppBarNavigationButton(
                                icon = Icons.Default.Close
                            )
                        },
                        // TODO DM Localize
                        title = {
                            Text(text = "Find People")
                        },
                        elevation = 0.dp
                    )
                    SearchInput(
                        modifier = Modifier.fillMaxWidth(),
                        input = keyWord,
                        onValueChanged = { viewModel.input.postValue(it) },
                    )
                    Divider()
                }
            },
        ) {
            searchSource?.let { source ->
                SearchResult(
                    source,
                    onItemClick = { user ->
                        viewModel.createNewConversation(
                            user,
                            onResult = { key ->
                                key?.let {
                                    navController.navigate(
                                        Route.Messages.Conversation(it),
                                        NavOptions(popUpTo = PopUpTo(Route.Messages.Home))
                                    )
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SearchInput(modifier: Modifier = Modifier, input: String, onValueChanged: (value: String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(SearchInputDefaults.ContentPadding)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(
                id = R.string.scene_search_title
            )
        )
        Spacer(modifier = Modifier.width(SearchInputDefaults.ContentSpacing))
        // TODO DM localize
        TextInput(
            value = input,
            onValueChange = onValueChanged, modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = "Search people")
            },
            maxLines = 1
        )
    }
}

private object SearchInputDefaults {
    val ContentPadding = PaddingValues(16.dp)
    val ContentSpacing = 16.dp
}

@Composable
fun SearchResult(source: LazyPagingItems<UiUser>, onItemClick: (user: UiUser) -> Unit) {
    LazyUiUserList(
        items = source,
        onItemClicked = onItemClick,
        modifier = Modifier.fillMaxSize()
    )
}
