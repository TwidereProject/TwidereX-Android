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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.di.assisted.assistedViewModel
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
                        Text(text = stringResource(id = R.string.scene_settings_display_title))
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
    ListItem(
        text = {
            ProvideTextStyle(value = MaterialTheme.typography.caption) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = "Third-party Twitter data provider")
                }
            }
        },
        trailing = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info_circle),
                    contentDescription = null,
                )
            }
        }
    )
    ListItem(
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { viewModel.setNitterInstance(it) },
                placeholder = {
                    Text(
                        text = "Nitter Instance"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info_circle),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        secondaryText = {
            Text(text = "Alternative Twitter front-end focused on privacy.")
        }
    )
}
