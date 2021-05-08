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

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.UserListComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsUserViewModel

@Composable
fun ListsMembersScene(
    listKey: MicroBlogKey,
    owned: Boolean,
) {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    listKey.toString()
    val viewModel = assistedViewModel<ListsUserViewModel.AssistedFactory, ListsUserViewModel>(
        account
    ) {
        it.create(account, listKey.id)
    }
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_lists_details_tabs_members))
                    }
                )
            },
            floatingActionButton = {
                if (owned) FloatingActionButton(
                    onClick = {
                        navController.navigate(Route.Lists.AddMembers(listKey = listKey))
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = stringResource(
                            id = R.string.scene_lists_details_add_members
                        )
                    )
                }
            }
        ) {
            UserListComponent(
                viewModel = viewModel,
                action = {
                    if (!owned) return@UserListComponent
                    var menuExpand by remember {
                        mutableStateOf(false)
                    }
                    IconButton(onClick = { menuExpand = !menuExpand }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(
                                id = R.string.scene_lists_users_menu_actions_remove
                            )
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpand,
                        onDismissRequest = { menuExpand = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                viewModel.removeMember(it)
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.scene_lists_users_menu_actions_remove
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}
