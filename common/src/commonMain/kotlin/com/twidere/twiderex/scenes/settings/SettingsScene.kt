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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene

data class SettingItem(
    val name: String,
    val icon: Painter,
    val route: String,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScene() {
    val settings =
        mapOf(
            stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_section_header_general) to listOf(
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_appearance_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_shirt),
                    route = Root.Settings.Appearance,
                ),
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_template),
                    route = Root.Settings.Display,
                ),
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_layout_sidebar),
                    route = Root.Settings.Layout,
                ),
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_settings_notification),
                    route = Root.Settings.Notification,
                ),
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_database),
                    route = Root.Settings.Storage,
                ),
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_triangle_square_circle),
                    route = Root.Settings.Misc,
                ),
            ),
            stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_section_header_about) to listOf(
                SettingItem(
                    stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_title),
                    painterResource(res = com.twidere.twiderex.MR.files.ic_info_circle),
                    route = Root.Settings.About,
                ),
            )
        )

    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_title))
                    }
                )
            }
        ) {
            LazyColumn(
                contentPadding = it
            ) {
                settings.forEach {
                    item {
                        ListItem(
                            text = {
                                ProvideTextStyle(value = MaterialTheme.typography.button) {
                                    Text(text = it.key)
                                }
                            },
                        )
                    }
                    items(it.value) {
                        val navController = LocalNavController.current
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    if (it.route.isNotEmpty()) {
                                        navController.navigate(it.route)
                                    }
                                }
                            ),
                            icon = {
                                Icon(painter = it.icon, contentDescription = it.name)
                            },
                            text = {
                                Text(text = it.name)
                            },
                        )
                    }
                }
            }
        }
    }
}
