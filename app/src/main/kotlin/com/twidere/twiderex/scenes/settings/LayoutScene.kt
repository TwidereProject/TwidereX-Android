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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.ReorderableColumn
import com.twidere.twiderex.component.foundation.rememberReorderableColumnState
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.scenes.home.HomeMenus
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.LayoutViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LayoutScene() {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<LayoutViewModel.AssistedFactory, LayoutViewModel>(
        account
    ) {
        it.create(
            account = account,
        )
    }
    val user = viewModel.user
    val menus = remember(account.type) {
        HomeMenus.values().filter { it.supportedPlatformType.contains(account.type) }.groupBy {
            it.showDefault
        }.map {
            listOf(
                if (it.key) {
                    "Tabbar actions"
                } else {
                    "Sidebar actions"
                }
            ) + it.value
        }.flatten()
    }
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Layout")
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ) {
                Surface(
                    color = MaterialTheme.colors.primary,
                ) {
                    ListItem(
                        text = {
                            Row {
                                UserName(user = user)
                                UserScreenName(user = user)
                            }
                        }
                    )
                }
                ListItem(
                    text = {
                        Text(text = "Custom Layout")
                    },
                    secondaryText = {
                        Text(text = "Choose and arrange up to 5 actions that will appear on the tabbar (The local and federal timelines will only be displayed in Mastodon.")
                    }
                )
                ReorderableColumn(
                    data = menus,
                    state = rememberReorderableColumnState { _, _ ->
                    }
                ) {
                    when (it) {
                        is String -> {
                            ItemHeader {
                                Text(text = it)
                            }
                        }
                        is HomeMenus -> {
                            ListItem(
                                text = {
                                    Text(text = it.item.name())
                                },
                                icon = {
                                    Icon(
                                        it.item.icon(),
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.primary,
                                    )
                                },
                                trailing = {
                                    Icon(Icons.Default.Menu, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}