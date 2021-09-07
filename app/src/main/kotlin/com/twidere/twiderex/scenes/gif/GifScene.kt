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
package com.twidere.twiderex.scenes.gif

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.ui.LazyUiGifList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiGif
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.gif.GifViewModel

@Composable
fun GifScene() {
    val viewModel = assistedViewModel<GifViewModel.AssistedFactory, GifViewModel> {
        it.create()
    }
    val enable by viewModel.enable.collectAsState(initial = false)
    val context = LocalContext.current
    val account = LocalActiveAccount.current
    val navController = LocalNavController.current
    val commitLoading by viewModel.commitLoading.collectAsState(initial = false)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "GIPHY")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.commit(
                                    context = context,
                                    platform = account?.type ?: PlatformType.Twitter,
                                    onSuccess = {
                                        navController.goBackWith(it)
                                    }
                                )
                            },
                            enabled = enable
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(id = R.string.common_controls_actions_yes),
                                tint = if (enable) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                            )
                        }
                    }
                )
            },
        ) {
            Box {
                GifContent(viewModel = viewModel)
                if (commitLoading) {
                    LoadingView()
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Dialog(onDismissRequest = { }) {
        LoadingProgress()
    }
}

@Composable
private fun GifContent(viewModel: GifViewModel) {
    val searchInput by viewModel.input.collectAsState()
    val searchState by viewModel.searchFlow.collectAsState(initial = null)
    val searchSource = searchState?.collectAsLazyPagingItems()
    val trendingSource = viewModel.trendSource.collectAsLazyPagingItems()
    val selectedItem by viewModel.selectedItem.collectAsState(initial = null)
    Column {
        SearchInput(
            input = searchInput,
            onValueChanged = {
                viewModel.input.value = it
            }
        )
        Divider()
        GifList(
            data = if (searchInput.isNotEmpty() && searchSource != null) searchSource else trendingSource,
            selectedItem = selectedItem,
            onItemSelected = { viewModel.selectedItem.value = it }
        )
    }
}

@Composable
private fun SearchInput(modifier: Modifier = Modifier, input: String, onValueChanged: (value: String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(
            SearchInputDefaults.ContentPadding
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(
                id = R.string.scene_search_title
            )
        )
        Spacer(modifier = Modifier.width(SearchInputDefaults.ContentSpacing))
        TextInput(
            value = input,
            onValueChange = onValueChanged, modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = "Search GIF")
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}

private object SearchInputDefaults {
    val ContentPadding = PaddingValues(16.dp)
    val ContentSpacing = 16.dp
}

@Composable
private fun GifList(data: LazyPagingItems<UiGif>, selectedItem: UiGif?, onItemSelected: (UiGif) -> Unit = {}) {
    LazyUiGifList(
        items = data,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        selectedItem = selectedItem,
        onItemSelected = onItemSelected
    )
}
