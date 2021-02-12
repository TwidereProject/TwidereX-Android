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
package com.twidere.twiderex.scenes.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel

class SearchItem : HomeNavigationItem() {
    override val name: String
        @Composable
        get() = stringResource(R.string.scene_search_title)
    override val route: String
        get() = "search"

    override val icon: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_search)
    override val withAppBar: Boolean
        get() = false

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun onCompose() {
        val account = LocalActiveAccount.current ?: return
        val viewModel =
            assistedViewModel<SearchInputViewModel.AssistedFactory, SearchInputViewModel>(
                account
            ) {
                it.create(account = account)
            }
        val source by viewModel.source.observeAsState(initial = emptyList())
        val navigator = LocalNavigator.current
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            Row(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        navigator.searchInput()
                                    },
                                    indication = null,
                                    interactionState = remember { InteractionState() }
                                )
                            ) {
                                Providers(
                                    LocalContentAlpha provides ContentAlpha.medium
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(1F)
                                            .align(Alignment.CenterVertically),
                                        text = stringResource(id = R.string.scene_search_search_bar_placeholder),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        navigator.searchInput()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_search),
                                        contentDescription = stringResource(
                                            id = R.string.scene_search_title
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) {
            LazyColumn {
                items(items = source) {
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                viewModel.addOrUpgrade(it.content)
                                navigator.search(it.content)
                            }
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = stringResource(
                                    id = R.string.accessibility_scene_search_history
                                )
                            )
                        },
                        trailing = {
                            IconButton(
                                onClick = {
                                    viewModel.remove(it)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_x),
                                    contentDescription = stringResource(
                                        id = R.string.common_controls_actions_remove
                                    )
                                )
                            }
                        },
                        text = {
                            Text(text = it.content)
                        },
                    )
                }
            }
        }
    }
}
