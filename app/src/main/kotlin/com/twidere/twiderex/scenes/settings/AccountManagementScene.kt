/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientActiveAccountViewModel
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

@Composable
fun AccountManagementScene() {
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_manage_accounts_title))
                    },
                    actions = {
                        val navController = AmbientNavController.current
                        IconButton(
                            onClick = {
                                navController.navigate(Route.SignIn.Default)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add)
                        }
                    }
                )
            }
        ) {
            val activeAccountViewModel = AmbientActiveAccountViewModel.current
            val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
            LazyColumn {
                items(items = accounts) { detail ->
                    detail.user.toUi().let {
                        ListItem(
                            icon = {
                                UserAvatar(
                                    user = it,
                                )
                            },
                            text = {
                                Text(
                                    text = it.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            secondaryText = {
                                Text(
                                    text = "@${it.screenName}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            trailing = {
                                var expanded by remember { mutableStateOf(false) }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    toggle = {
                                        IconButton(
                                            onClick = {
                                                expanded = true
                                            },
                                        ) {
                                            Icon(imageVector = Icons.Default.MoreVert)
                                        }
                                    },
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            activeAccountViewModel.deleteAccount(detail)
                                        },
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.common_controls_actions_remove),
                                            color = Color.Red,
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
