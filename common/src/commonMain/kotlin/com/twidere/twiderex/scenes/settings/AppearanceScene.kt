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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemDivider
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.settings.RadioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.status.UserAvatarDefaults
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.ui.isDarkTheme
import com.twidere.twiderex.ui.primaryColors
import com.twidere.twiderex.viewmodel.settings.AppearanceViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppearanceScene() {
    var showPrimaryColorDialog by remember { mutableStateOf(false) }
    val appearance = LocalAppearancePreferences.current
    val viewModel: AppearanceViewModel = getViewModel()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_title))
                    },
                )
            }
        ) {
            if (showPrimaryColorDialog) {
                PrimaryColorDialog(
                    viewModel = viewModel,
                    onDismiss = {
                        showPrimaryColorDialog = false
                    }
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                ListItem(
                    modifier = Modifier.clickable(
                        onClick = {
                            showPrimaryColorDialog = true
                        }
                    ),
                    text = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_highlight_color))
                    },
                    trailing = {
                        Box(
                            modifier = Modifier
                                .height(24.dp)
                                .width(32.dp)
                                .clip(MaterialTheme.shapes.small)
                                .aspectRatio(1F)
                                .background(MaterialTheme.colors.primary),
                        ) {
                        }
                    }
                )
                ItemDivider()
                RadioItem(
                    options = listOf(
                        AppearancePreferences.TabPosition.Top,
                        AppearancePreferences.TabPosition.Bottom,
                    ),
                    value = appearance.tabPosition,
                    onChanged = {
                        viewModel.setTabPosition(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_section_header_tab_position))
                    },
                    itemContent = {
                        Text(
                            text = stringResource(
                                arrayOf(
                                    com.twidere.twiderex.MR.strings.scene_settings_appearance_tab_position_top,
                                    com.twidere.twiderex.MR.strings.scene_settings_appearance_tab_position_bottom
                                )[it.ordinal]
                            )
                        )
                    }
                )
                ItemDivider()
                // Scrolling Timeline
                ItemHeader() {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_section_header_scrolling_timeline))
                }
                switchItem(
                    value = appearance.hideTabBarWhenScroll,
                    onChanged = {
                        viewModel.setHideTabBarWhenScrolling(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_scrolling_timeline_tab_bar))
                    },
                )
                switchItem(
                    value = appearance.hideAppBarWhenScroll,
                    onChanged = {
                        viewModel.setHideAppBarWhenScrolling(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_scrolling_timeline_app_bar))
                    },
                )
                switchItem(
                    value = appearance.hideFabWhenScroll,
                    onChanged = {
                        viewModel.setHideFabWhenScrolling(it)
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_scrolling_timeline_fab))
                    },
                )
                ItemDivider()
                RadioItem(
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
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_section_header_theme))
                    },
                    itemContent = {
                        Text(
                            text = stringResource(
                                arrayOf(
                                    com.twidere.twiderex.MR.strings.scene_settings_appearance_theme_auto,
                                    com.twidere.twiderex.MR.strings.scene_settings_appearance_theme_light,
                                    com.twidere.twiderex.MR.strings.scene_settings_appearance_theme_dark,
                                )[it.ordinal]
                            )
                        )
                    }
                )
                val isLightTheme = appearance.theme == AppearancePreferences.Theme.Light
                AnimatedVisibility(visible = !isLightTheme) {
                    switchItem(
                        value = appearance.isDarkModePureBlack,
                        onChanged = {
                            viewModel.setIsDarkModePureBlack(it)
                        },
                    ) {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_AMOLED_optimized_mode))
                    }
                }
            }
        }
    }
}

@Composable
fun PrimaryColorDialog(
    viewModel: AppearanceViewModel,
    onDismiss: () -> Unit,
) {
    val appearance = LocalAppearancePreferences.current
    val colorIndex = appearance.primaryColorIndex
    val colors = if (isDarkTheme()) {
        primaryColors.map { it.second }
    } else {
        primaryColors.map { it.first }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_pick_color))
        },
        text = {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                colors.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier
                            .padding(end = PrimaryColorDialog.ItemsSpacing)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(UserAvatarDefaults.AvatarSize)
                                .clip(CircleShape)
                                .background(it)
                                .clickable(
                                    onClick = {
                                        viewModel.setPrimaryColorIndex(index)
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (colorIndex == index) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {},
                                    colors = CheckboxDefaults.colors(checkedColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        }
    )
}

object PrimaryColorDialog {
    val ItemsSpacing = 8.dp
}
