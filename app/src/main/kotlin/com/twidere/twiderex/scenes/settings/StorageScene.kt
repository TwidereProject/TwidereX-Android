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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.EdgePadding
import com.twidere.twiderex.component.foundation.EdgeToEdgeBox
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.StorageViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StorageScene() {
    val viewModel = assistedViewModel<StorageViewModel.AssistedFactory, StorageViewModel> {
        it.create()
    }
    val loading by viewModel.loading.observeAsState(initial = false)

    if (loading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ) {
            CircularProgressIndicator()
        }
    }

    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_storage_title))
                    }
                )
            }
        ) {
            EdgeToEdgeBox(edgePadding = EdgePadding(top = false)) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                viewModel.clearSearchHistory()
                            },
                    ) {
                        Text(text = stringResource(id = R.string.scene_settings_storage_search_title))
                    }
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                viewModel.clearImageCache()
                            },
                        text = {
                            Text(text = stringResource(id = R.string.scene_settings_storage_media_title))
                        },
                        secondaryText = {
                            Text(text = stringResource(id = R.string.scene_settings_storage_media_sub_title))
                        },
                    )
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                viewModel.clearDatabaseCache()
                            },
                        text = {
                            Text(
                                text = stringResource(id = R.string.scene_settings_storage_all_title),
                                color = Color.Red
                            )
                        },
                        secondaryText = {
                            Text(text = stringResource(id = R.string.scene_settings_storage_all_sub_title))
                        },
                    )
                }
            }
        }
    }
}
