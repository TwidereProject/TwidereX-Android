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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchInputScene(initial: String? = null) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel =
        assistedViewModel<SearchInputViewModel.AssistedFactory, SearchInputViewModel>(
            account
        ) {
            it.create(account = account)
        }
    val source by viewModel.source.observeAsState(initial = emptyList())
    val initialText = initial ?: ""
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(initialText.length),
            )
        )
    }
    val navigator = AmbientNavigator.current
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            TextInput(
                                value = textFieldValue,
                                onValueChange = {
                                    textFieldValue = it
                                },
                                maxLines = 1,
                                placeholder = {
                                    Text(text = stringResource(id = R.string.scene_search_search_bar_placeholder))
                                },
                                onImeActionPerformed = { _, _ ->
                                    if (textFieldValue.text.isNotEmpty()) {
                                        viewModel.addOrUpgrade(textFieldValue.text)
                                        navigator.search(textFieldValue.text)
                                    }
                                },
                                autoFocus = true,
                                imeAction = ImeAction.Search,
                                alignment = Alignment.CenterStart,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (textFieldValue.text.isNotEmpty()) {
                                    viewModel.addOrUpgrade(textFieldValue.text)
                                    navigator.search(textFieldValue.text)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = vectorResource(id = R.drawable.ic_search),
                                contentDescription = stringResource(
                                    id = R.string.scene_search_title
                                )
                            )
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
                                    imageVector = vectorResource(id = R.drawable.ic_x),
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
