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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.MiscViewModel

@Composable
fun MiscScene() {
    val viewModel = assistedViewModel<MiscViewModel.AssistedFactory, MiscViewModel> {
        it.create()
    }

    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_misc_title))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                NitterPreference(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NitterPreference(viewModel: MiscViewModel) {
    val value by viewModel.nitter.observeAsState(initial = "")
    var showInformationDialog by remember {
        mutableStateOf(false)
    }
    var showUsageDialog by remember {
        mutableStateOf(false)
    }
    if (showUsageDialog) {
        NitterUsageDialog(
            onDismissRequest = {
                showUsageDialog = false
            }
        )
    }
    if (showInformationDialog) {
        NitterInformationDialog(
            onDismissRequest = {
                showInformationDialog = false
            }
        )
    }
    ItemHeader(
        trailing = {
            IconButton(
                onClick = {
                    showInformationDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info_circle),
                    contentDescription = null,
                )
            }
        }
    ) {
        Text(text = stringResource(id = R.string.scene_settings_misc_nitter_title))
    }
    ListItem(
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { viewModel.setNitterInstance(it) },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.scene_settings_misc_nitter_input_placeholder)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showUsageDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info_circle),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        secondaryText = {
            Text(text = stringResource(id = R.string.scene_settings_misc_nitter_input_description))
        }
    )
}

@Composable
fun NitterUsageDialog(
    onDismissRequest: () -> Unit,
) {
    val navigator = LocalNavigator.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.scene_settings_misc_nitter_dialog_usage_title))
        },
        text = {
            Text(text = stringResource(id = R.string.scene_settings_misc_nitter_dialog_usage_content))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.common_controls_actions_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navigator.openLink(
                        "https://github.com/zedeus/nitter",
                        deepLink = false
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.scene_settings_misc_nitter_dialog_usage_project_button))
            }
        }
    )
}

@Composable
fun NitterInformationDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = {
            Text(text = stringResource(id = R.string.scene_settings_misc_nitter_dialog_information_title))
        },
        text = {
            Text(text = stringResource(id = R.string.scene_settings_misc_nitter_dialog_information_content))
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = stringResource(id = R.string.common_controls_actions_ok))
            }
        }
    )
}
