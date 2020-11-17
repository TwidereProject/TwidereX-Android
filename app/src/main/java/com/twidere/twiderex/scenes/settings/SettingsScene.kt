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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

data class SettingItem(
    val name: String,
    val icon: VectorAsset,
    val route: String,
)
@Composable
fun SettingsScene() {
    val settings =
        mapOf(
            stringResource(id = R.string.group_title_general) to listOf(
                SettingItem(
                    stringResource(id = R.string.title_appearance),
                    vectorResource(id = R.drawable.ic_shirt),
                    route = Route.Settings.Appearance,
                ),
                SettingItem(
                    stringResource(id = R.string.title_display),
                    vectorResource(id = R.drawable.ic_template),
                    route = Route.Settings.Display,
                ),
                // TODO
//                SettingItem(
//                    "Layout",
//                    vectorResource(id = R.drawable.ic_layout_sidebar),
//                    route = "",
//                ),
//                SettingItem(
//                    "Web Browser",
//                    vectorResource(id = R.drawable.ic_browser),
//                    route = "",
//                ),
            ),
            stringResource(id = R.string.group_title_about) to listOf(
                SettingItem(
                    stringResource(id = R.string.title_about),
                    vectorResource(id = R.drawable.ic_info_circle),
                    route = Route.Settings.About,
                ),
            )
        )

    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.title_settings))
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
                        val navController = AmbientNavController.current
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    if (it.route.isNotEmpty()) {
                                        navController.navigate(it.route)
                                    }
                                }
                            ),
                            icon = {
                                Icon(asset = it.icon)
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
