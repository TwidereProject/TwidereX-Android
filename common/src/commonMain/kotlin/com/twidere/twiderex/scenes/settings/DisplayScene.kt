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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.action.FakeStatusActions
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemDivider
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.navigation.FakeNavigator
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.settings.RadioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.DisplayViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayScene() {
    val viewModel: DisplayViewModel = getViewModel()
    val display = LocalDisplayPreferences.current
    var showLoadItemLimitSliderDialog by remember { mutableStateOf(false) }
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_title))
                    }
                )
            }
        ) {
            if (showLoadItemLimitSliderDialog) {
                LoadItemLimitSliderDialog(
                    defaultValue = display.loadItemLimit.toFloat(),
                    onEnter = { value ->
                        viewModel.setLoadItemLimit(value.toInt())
                    },
                    onDismiss = {
                        showLoadItemLimitSliderDialog = false
                    }
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                ItemHeader() {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_section_header_preview))
                }
                CompositionLocalProvider(
                    LocalNavigator provides FakeNavigator,
                    LocalStatusActions provides FakeStatusActions,
                ) {
                    TimelineStatusComponent(data = UiStatus.sample())
                }
                ItemDivider()
                ItemHeader() {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_section_header_text))
                }
                switchItem(
                    value = display.useSystemFontSize,
                    onChanged = {
                        viewModel.setUseSystemFontSize(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_text_use_the_system_font_size))
                    },
                )
                if (!display.useSystemFontSize) {
                    ListItem(
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp),
                                imageVector = Icons.Default.TextFields,
                                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_scene_settings_display_font_size)
                            )
                        },
                        text = {
                            var fontSize by remember {
                                mutableStateOf(display.fontScale)
                            }
                            Slider(
                                steps = ((1.4f - 0.8f) * 10).toInt(),
                                value = fontSize,
                                onValueChange = { fontSize = it },
                                valueRange = 0.8f..1.4f,
                                onValueChangeFinished = { viewModel.commitFontScale(fontSize) }
                            )
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_scene_settings_display_font_size)
                            )
                        }
                    )
                }
                ItemDivider()
                RadioItem(
                    options = listOf(
                        DisplayPreferences.AvatarStyle.Round,
                        DisplayPreferences.AvatarStyle.Square,
                    ),
                    value = display.avatarStyle,
                    onChanged = {
                        viewModel.setAvatarStyle(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_text_avatar_style))
                    },
                    itemContent = {
                        Text(
                            text = stringResource(
                                arrayOf(
                                    com.twidere.twiderex.MR.strings.scene_settings_display_text_circle,
                                    com.twidere.twiderex.MR.strings.scene_settings_display_text_rounded_square,
                                )[it.ordinal]
                            )
                        )
                    }
                )
                ItemDivider()
                ItemHeader() {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_section_header_media))
                }
                switchItem(
                    value = display.urlPreview,
                    onChanged = {
                        viewModel.setUrlPreview(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_url_preview))
                    }
                )
                switchItem(
                    value = display.mediaPreview,
                    onChanged = {
                        viewModel.setMediaPreview(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_media_media_previews))
                    }
                )
                switchItem(
                    value = display.muteByDefault,
                    onChanged = {
                        viewModel.setMuteByDefault(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_media_mute_by_default))
                    }
                )
                if (display.mediaPreview && currentPlatform != Platform.JVM) {
                    RadioItem(
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
                            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_media_auto_playback))
                        },
                        itemContent = {
                            Text(
                                text = stringResource(
                                    arrayOf(
                                        com.twidere.twiderex.MR.strings.scene_settings_display_media_automatic,
                                        com.twidere.twiderex.MR.strings.scene_settings_display_media_always,
                                        com.twidere.twiderex.MR.strings.scene_settings_display_media_off,
                                    )[it.ordinal]
                                )
                            )
                        }
                    )
                }
                ItemDivider()
                ItemHeader() {
                    Text(text = stringResource(com.twidere.twiderex.MR.strings.common_controls_status_actions_share))
                }
                switchItem(
                    value = display.shareWithContent,
                    onChanged = {
                        viewModel.setShareWithContent(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_share_with_content))
                    }
                )
                ItemDivider()
                ItemHeader() {
                    Text(stringResource(com.twidere.twiderex.MR.strings.scene_settings_display_section_header_content))
                }
                ListItem(
                    modifier = Modifier.clickable(
                        onClick = {
                            showLoadItemLimitSliderDialog = true
                        }
                    ),
                    text = {
                        Text(stringResource(com.twidere.twiderex.MR.strings.scene_settings_display_content_load_item_limit))
                    },
                    trailing = {
                        Text(display.loadItemLimit.toString())
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadItemLimitSliderDialog(
    defaultValue: Float,
    onEnter: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(defaultValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(com.twidere.twiderex.MR.strings.scene_settings_display_content_load_item_limit))
        },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = sliderPosition,
                    valueRange = 10f..100f,
                    onValueChange = { sliderPosition = it },
                    steps = 17, // (100 - 10) / 5 - 1,
                    onValueChangeFinished = {
                        onEnter(sliderPosition)
                    },
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(LoadItemLimitSliderDialog.ItemsSpacing))
                Text(
                    text = sliderPosition.toInt().toString(),
                    modifier = Modifier.width(25.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_cancel))
            }
        }
    )
}

object LoadItemLimitSliderDialog {
    val ItemsSpacing = 8.dp
}
