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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel
import org.koin.core.parameter.parametersOf

val fadeCreateTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    alpha = factor
}
val fadeDestroyTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    alpha = factor
}
val fadePauseTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    alpha = factor
}
val fadeResumeTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    alpha = factor
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchInputScene(initial: String? = null) {

    val viewModel: SearchInputViewModel = getViewModel {
        parametersOf(initial ?: "")
    }
    val source by viewModel.source.observeAsState(initial = emptyList())
    val textFieldValue by viewModel.searchInput.observeAsState(TextFieldValue())
    val navigator = LocalNavigator.current
    TwidereScene {
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
                                    viewModel.updateSearchInput(it)
                                },
                                maxLines = 1,
                                placeholder = {
                                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_search_search_bar_placeholder))
                                },
                                autoFocus = true,
                                alignment = Alignment.CenterStart,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search,
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (textFieldValue.text.isNotEmpty()) {
                                            viewModel.addOrUpgrade(textFieldValue.text)
                                            navigator.search(textFieldValue.text)
                                        }
                                    }
                                )
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
                                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_search),
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.scene_search_title
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
                                viewModel.updateSearchInput(TextFieldValue(it.content, TextRange(it.content.length)))
                                navigator.search(it.content)
                            }
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.accessibility_scene_search_history
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
                                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                                    contentDescription = stringResource(
                                        res = com.twidere.twiderex.MR.strings.common_controls_actions_remove
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
