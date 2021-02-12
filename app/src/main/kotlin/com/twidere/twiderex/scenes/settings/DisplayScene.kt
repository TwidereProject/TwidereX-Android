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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.action.FakeStatusActions
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.lazy.itemHeader
import com.twidere.twiderex.component.navigation.FakeNavigator
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.settings.radioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.settings.DisplayViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayScene() {
    val viewModel = navViewModel<DisplayViewModel>()
    val display = LocalDisplayPreferences.current
    TwidereXTheme {
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
            LazyColumn {
                itemHeader {
                    Text(text = stringResource(id = R.string.scene_settings_display_section_header_preview))
                }
                item {
                    Providers(
                        LocalNavigator provides FakeNavigator,
                        LocalStatusActions provides FakeStatusActions,
                    ) {
                        TimelineStatusComponent(data = UiStatus.sample())
                    }
                }
                itemDivider()
                itemHeader {
                    Text(text = stringResource(id = R.string.scene_settings_display_section_header_text))
                }
                switchItem(
                    value = display.useSystemFontSize,
                    onChanged = {
                        viewModel.setUseSystemFontSize(it)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_display_text_use_the_system_font_size))
                    },
                )
                if (!display.useSystemFontSize) {
                    item {
                        ListItem(
                            icon = {
                                Icon(
                                    modifier = Modifier.size(12.dp),
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = stringResource(id = R.string.accessibility_scene_settings_display_font_size)
                                )
                            },
                            text = {
                                Slider(
                                    steps = ((1.4f - 0.8f) * 10).toInt(),
                                    value = display.fontScale,
                                    onValueChange = { viewModel.setFontScale(it) },
                                    valueRange = 0.8f..1.4f
                                )
                            },
                            trailing = {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = stringResource(id = R.string.accessibility_scene_settings_display_font_size)
                                )
                            }
                        )
                    }
                }
                itemDivider()
                radioItem(
                    options = listOf(
                        DisplayPreferences.AvatarStyle.Round,
                        DisplayPreferences.AvatarStyle.Square,
                    ),
                    value = display.avatarStyle,
                    onChanged = {
                        viewModel.setAvatarStyle(it)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_display_text_avatar_style))
                    },
                    itemContent = {
                        Text(
                            text = stringResource(
                                arrayOf(
                                    R.string.scene_settings_display_text_circle,
                                    R.string.scene_settings_display_text_rounded_square,
                                )[it.ordinal]
                            )
                        )
                    }
                )
                itemDivider()
                itemHeader {
                    Text(text = stringResource(id = R.string.scene_settings_display_section_header_media))
                }
                switchItem(
                    value = display.mediaPreview,
                    onChanged = {
                        viewModel.setMediaPreview(it)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_display_media_media_previews))
                    }
                )
                if (display.mediaPreview) {
                    radioItem(
                        options = listOf(
                            DisplayPreferences.AutoPlayback.Auto,
                            DisplayPreferences.AutoPlayback.Always,
                            DisplayPreferences.AutoPlayback.Off,
                        ),
                        value = display.autoPlayback,
                        onChanged = {
                            viewModel.setAutoPlayback(it)
                        },
                        title = {
                            Text(text = stringResource(id = R.string.scene_settings_display_media_auto_playback))
                        },
                        itemContent = {
                            Text(
                                text = stringResource(
                                    arrayOf(
                                        R.string.scene_settings_display_media_automatic,
                                        R.string.scene_settings_display_media_always,
                                        R.string.scene_settings_display_media_off,
                                    )[it.ordinal]
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
