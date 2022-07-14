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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.ReorderableColumn
import com.twidere.twiderex.component.foundation.rememberReorderableColumnState
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.scenes.home.item
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.LayoutViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LayoutScene() {
    val account = LocalActiveAccount.current ?: return
    val viewModel: LayoutViewModel = getViewModel()
    val user by viewModel.user.observeAsState(initial = null)
    val menuOrder by account.preferences.homeMenuOrder.collectAsState(
        initial = HomeMenus.values().map { it to it.showDefault }
    )
    val menus =
        menuOrder.filter { it.first.supportedPlatformType.contains(account.type) }.groupBy {
            it.second
        }.map {
            listOf(
                it.key
            ) + it.value.map { it.first }
        }.flatten().let {
            if (it.firstOrNull() != true) {
                listOf(true) + it
            } else {
                it
            }
        }.let {
            if (!it.contains(false)) {
                it + false
            } else {
                it
            }
        } as List<Any>
    val menuState = rememberUpdatedState(newValue = menus)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_title))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ) {
                user?.let { user ->
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
                }
                ListItem(
                    text = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_desc_title))
                    },
                    secondaryText = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_desc_content))
                    }
                )
                ReorderableColumn(
                    data = menus,
                    state = rememberReorderableColumnState { oldIndex, newIndex ->
                        viewModel.updateHomeMenu(
                            oldIndex,
                            newIndex,
                            menuState.value,
                        )
                    },
                    dragingContent = {
                        Card {
                            LayoutItemContent(it = it, viewModel = viewModel, menus = menus)
                        }
                    }
                ) {
                    LayoutItemContent(it = it, viewModel = viewModel, menus = menus)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LayoutItemContent(
    it: Any,
    viewModel: LayoutViewModel,
    menus: List<Any>,
) {
    val current = menus.indexOf(it)
    val falseIndex = menus.indexOf(false)
    val visible = current < falseIndex
    when (it) {
        is Boolean -> {
            ItemHeader {
                Text(
                    text = if (it) {
                        stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_actions_tabbar)
                    } else {
                        stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_actions_drawer)
                    }
                )
            }
        }
        is HomeMenus -> {
            ListItem(
                text = {
                    Text(text = it.item.name())
                },
                icon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                if (visible) {
                                    viewModel.removeMenu(
                                        current,
                                        menus
                                    )
                                } else {
                                    viewModel.addMenu(
                                        current,
                                        menus
                                    )
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(
                                    res = if (visible) {
                                        com.twidere.twiderex.MR.files.ic_delete_colored
                                    } else {
                                        com.twidere.twiderex.MR.files.ic_add_colored
                                    }
                                ),
                                modifier = Modifier.size(24.dp),
                                contentDescription = null,
                            )
                        }
                        Icon(
                            it.item.icon(),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                },
                trailing = {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        }
    }
}
