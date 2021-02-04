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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.DraftViewModel

@Composable
fun DraftListScene() {
    val viewModel = navViewModel<DraftViewModel>()
    val source by viewModel.source.observeAsState(initial = emptyList())
    val navController = AmbientNavController.current
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_drafts_title))
                    }
                )
            }
        ) {
            LazyColumn {
                items(items = source) {
                    ListItem(
                        text = {
                            Text(text = it.content)
                        },
                        trailing = {
                            var expanded by remember { mutableStateOf(false) }
                            DropdownMenu(
                                toggle = {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = stringResource(
                                                id = R.string.accessibility_common_more
                                            )
                                        )
                                    }
                                },
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        navController.navigate(Route.Draft.Compose(it._id))
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.scene_drafts_actions_edit_draft))
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.delete(it)
                                    }
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
