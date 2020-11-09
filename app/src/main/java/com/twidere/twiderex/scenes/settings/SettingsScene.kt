/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.navigation.compose.navigate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

data class SettingItem(
    val name: String,
    val icon: VectorAsset,
    val route: String,
)

private val settings by lazy {
    mapOf(
        "GENERAL" to listOf(
            SettingItem(
                "Appearance",
                Icons.Default.Home,
                route = "settings/appearance",
            ),
            SettingItem(
                "Display",
                Icons.Default.Home,
                route = "settings/display",
            ),
            SettingItem(
                "Layout",
                Icons.Default.Home,
                route = "",
            ),
            SettingItem(
                "Web Browser",
                Icons.Default.Home,
                route = "",
            ),
        ),
        "ABOUT" to listOf(
            SettingItem(
                "About",
                Icons.Default.Info,
                route = "",
            ),
        )
    )
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
fun SettingsScene() {
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Settings")
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
