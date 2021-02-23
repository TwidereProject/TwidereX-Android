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
package com.twidere.twiderex.scenes.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.itemsPaging
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.DisposeResult
import com.twidere.twiderex.extensions.setResult
import com.twidere.twiderex.extensions.viewModel
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.compose.ComposeSearchUserViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ComposeSearchUserScene() {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val viewModel = viewModel(account) {
        ComposeSearchUserViewModel(account = account)
    }
    val text by viewModel.text.observeAsState(initial = "")
    val sourceState by viewModel.sourceFlow.collectAsState(initial = null)
    val source = sourceState?.collectAsLazyPagingItems()
    TwidereXTheme {
        navController.DisposeResult(key = "user_name")
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            TextInput(
                                value = text,
                                onValueChange = {
                                    viewModel.text.value = it
                                },
                                maxLines = 1,
                                placeholder = {
                                    Text(text = stringResource(id = R.string.scene_search_search_bar_placeholder))
                                },
                                onImeActionPerformed = { _, _ ->
                                    navController.setResult("user_name", text)
                                    navController.popBackStack()
                                },
                                autoFocus = true,
                                imeAction = ImeAction.Done,
                                alignment = Alignment.CenterStart,
                            )
                        }
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.setResult("user_name", text)
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(
                                    id = R.string.accessibility_common_done
                                )
                            )
                        }
                    },
                )
            }
        ) {
            LazyColumn {
                source?.let {
                    itemsPaging(source) {
                        it?.let { item ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        navController.setResult("user_name", item.screenName)
                                        navController.popBackStack()
                                    }
                                ),
                                icon = {
                                    UserAvatar(user = item)
                                },
                                text = {
                                    Row {
                                        Text(
                                            text = item.name,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colors.primary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Providers(
                                            LocalContentAlpha provides ContentAlpha.medium
                                        ) {
                                            Text(
                                                text = "@${item.screenName}",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                },
                                secondaryText = {
                                    Text(text = item.rawDesc, maxLines = 1)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
