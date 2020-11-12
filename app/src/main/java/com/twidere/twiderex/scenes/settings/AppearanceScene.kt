/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.settings.radioItem
import com.twidere.twiderex.extensions.isDarkTheme
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.preferences.AmbientAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.primaryColors
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.settings.AppearanceViewModel

@Composable
fun AppearanceScene() {
    var showPrimaryColorDialog by remember { mutableStateOf(false) }
    val appearance = AmbientAppearancePreferences.current
    val viewModel = navViewModel<AppearanceViewModel>()
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Appearance")
                    },
                )
            }
        ) {
            if (showPrimaryColorDialog) {
                primaryColorDialog(
                    viewModel = viewModel,
                    onDismiss = {
                        showPrimaryColorDialog = false
                    }
                )
            }
            LazyColumn {
                item {
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                showPrimaryColorDialog = true
                            }
                        ),
                        text = {
                            Text(text = "Highlight color")
                        },
                        trailing = {
                            Box(
                                modifier = Modifier
                                    .preferredHeight(24.dp)
                                    .preferredWidth(32.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .aspectRatio(1F)
                                    .background(MaterialTheme.colors.primary),
                            ) {
                            }
                        }
                    )
                }
                itemDivider()
                radioItem(
                    options = listOf(
                        AppearancePreferences.TabPosition.Top,
                        AppearancePreferences.TabPosition.Bottom,
                    ),
                    value = appearance.tapPosition,
                    onChanged = {
                        viewModel.setTabPosition(it)
                    },
                    title = {
                        Text(text = "Tab Position")
                    },
                    itemContent = {
                        Text(text = it.name)
                    }
                )
                itemDivider()
                radioItem(
                    options = listOf(
                        AppearancePreferences.Theme.Auto,
                        AppearancePreferences.Theme.Light,
                        AppearancePreferences.Theme.Dark,
                    ),
                    value = appearance.theme,
                    onChanged = {
                        viewModel.setTheme(it)
                    },
                    title = {
                        Text(text = "Theme")
                    },
                    itemContent = {
                        Text(text = it.name)
                    }
                )
            }
        }
    }
}


@Composable
fun primaryColorDialog(
    viewModel: AppearanceViewModel,
    onDismiss: () -> Unit,
) {
    val appearance = AmbientAppearancePreferences.current
    val colorIndex = appearance.primaryColorIndex
    val colors = if (isDarkTheme()) {
        primaryColors.map { it.second }
    } else {
        primaryColors.map { it.first }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Select color")
        },
        text = {
            LazyRowForIndexed(
                items = colors
            ) { index, it ->
                Box(
                    modifier = Modifier
                        .padding(end = standardPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .size(profileImageSize)
                            .clip(CircleShape)
                            .background(it)
                            .clickable(
                                onClick = {
                                    viewModel.setPrimaryColorIndex(index)
                                }
                            ),
                        alignment = Alignment.Center,
                    ) {
                        if (colorIndex == index) {
                            Checkbox(checked = true, onCheckedChange = {})
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}
