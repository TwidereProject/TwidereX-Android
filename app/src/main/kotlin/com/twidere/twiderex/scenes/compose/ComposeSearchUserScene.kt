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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.itemsPaging
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.viewModel
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.compose.ComposeSearchUserViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeSearchUserScene() {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = viewModel(account) {
        ComposeSearchUserViewModel(account = account)
    }
    val text by viewModel.text.observeAsState(initial = "")
    val sourceState by viewModel.sourceFlow.collectAsState(initial = null)
    val source = sourceState?.collectAsLazyPagingItems()
    TwidereXTheme {
        InAppNotificationScaffold {
            Column {
                AppBar(
                    title = {
                        Text(text = "@User")
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    elevation = 0.dp,
                )
                ListItem(
                    icon = {
                        Icon(imageVector = vectorResource(id = R.drawable.ic_search))
                    },
                    text = {
                        TextInput(
                            value = text,
                            onValueChange = {
                                viewModel.text.postValue(it)
                            },
                            maxLines = 1,
                            placeholder = {
                                Text(text = stringResource(id = R.string.scene_search_search_bar_placeholder))
                            },
                            onImeActionPerformed = { _, _ ->
                            },
                            autoFocus = true,
                            imeAction = ImeAction.Search,
                            alignment = Alignment.CenterStart,
                        )
                    }
                )
                Divider()
                Box(
                    modifier = Modifier.weight(1F)
                ) {
                    LazyColumn {
                        source?.let {
                            itemsPaging(source) {
                                it?.let { item ->
                                    ListItem(
                                        modifier = Modifier.clickable(
                                            onClick = {
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
                                                    AmbientContentAlpha provides ContentAlpha.medium
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
                                            Text(text = item.desc, maxLines = 1)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
