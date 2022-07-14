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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.ColoredSwitch
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.NotificationViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationScene() {
    val activeAccountViewModel = LocalActiveAccountViewModel.current
    val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
    val viewModel: NotificationViewModel = getViewModel()
    val notificationEnabled by viewModel.enabled.observeAsState(initial = true)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_title))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            Column {
                Surface(
                    color = MaterialTheme.colors.primary,
                ) {
                    ListItem(
                        modifier = Modifier.clickable {
                            viewModel.setEnabled(!notificationEnabled)
                        },
                        text = {
                            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_notification_switch))
                        },
                        trailing = {
                            ColoredSwitch(
                                checked = notificationEnabled,
                                onCheckedChange = {
                                    viewModel.setEnabled(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colors.onPrimary,
                                )
                            )
                        }
                    )
                }
                ItemHeader {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_accounts))
                }
                val navController = LocalNavController.current
                LazyColumn {
                    items(accounts) {
                        val user = it.toUi()
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Root.Settings.AccountNotification(it.accountKey))
                                },
                                enabled = notificationEnabled,
                            ),
                            icon = {
                                CompositionLocalProvider(
                                    *if (!notificationEnabled) {
                                        arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                                    } else {
                                        emptyArray()
                                    }
                                ) {
                                    UserAvatar(
                                        user = user,
                                        withPlatformIcon = true,
                                    )
                                }
                            },
                            text = {
                                CompositionLocalProvider(
                                    *if (!notificationEnabled) {
                                        arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                                    } else {
                                        emptyArray()
                                    }
                                ) {
                                    UserName(user = user)
                                }
                            },
                            secondaryText = {
                                CompositionLocalProvider(
                                    *if (!notificationEnabled) {
                                        arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                                    } else {
                                        emptyArray()
                                    }
                                ) {
                                    UserScreenName(user = user)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
