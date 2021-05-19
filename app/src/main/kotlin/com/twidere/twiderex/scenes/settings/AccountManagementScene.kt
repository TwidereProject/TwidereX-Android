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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
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
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountManagementScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_manage_accounts_title))
                    },
                    actions = {
                        val navController = LocalNavController.current
                        IconButton(
                            onClick = {
                                navController.navigate(Route.SignIn.Default)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(
                                    id = R.string.accessibility_scene_manage_accounts_add
                                )
                            )
                        }
                    }
                )
            }
        ) {
            val activeAccountViewModel = LocalActiveAccountViewModel.current
            val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
            LazyColumn2 {
                items(items = accounts) { detail ->
                    detail.toUi().let {
                        ListItem(
                            icon = {
                                UserAvatar(
                                    user = it,
                                    withPlatformIcon = true,
                                )
                            },
                            text = {
                                UserName(user = it)
                            },
                            secondaryText = {
                                UserScreenName(user = it)
                            },
                            trailing = {
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(
                                        onClick = {
                                            expanded = true
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = stringResource(
                                                id = R.string.accessibility_common_more
                                            )
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
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
                            }
                        )
                    }
                }
            }
        }
    }
}
