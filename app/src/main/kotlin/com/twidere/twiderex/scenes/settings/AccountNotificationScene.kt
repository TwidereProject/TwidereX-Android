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

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.ColoredSwitch
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.AccountNotificationViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountNotificationScene(
    accountKey: MicroBlogKey,
) {
    val viewModel =
        assistedViewModel<AccountNotificationViewModel.AssistedFactory, AccountNotificationViewModel>(
            accountKey
        ) {
            it.create(accountKey)
        }
    val account by viewModel.account.observeAsState(initial = null)
    val enabled by viewModel.isNotificationEnabled.observeAsState(initial = true)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = R.string.scene_settings_notification_title))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                )
            },
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                account?.let {
                    ListItem(
                        modifier = Modifier.clickable {
                            viewModel.setIsNotificationEnabled(!enabled)
                        },
                        text = {
                            UserName(user = it)
                        },
                        secondaryText = {
                            UserScreenName(user = it)
                        },
                        trailing = {
                            ColoredSwitch(
                                checked = enabled,
                                onCheckedChange = {
                                    viewModel.setIsNotificationEnabled(it)
                                },
                            )
                        }
                    )
                }
                val context = LocalContext.current
                NotificationChannelSpec.values().filter { it.grouped }
                    .sortedBy { stringResource(id = it.nameRes) }
                    .forEach {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val intent =
                                            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                                                .putExtra(
                                                    Settings.EXTRA_APP_PACKAGE,
                                                    BuildConfig.APPLICATION_ID
                                                )
                                                .putExtra(
                                                    Settings.EXTRA_CHANNEL_ID,
                                                    accountKey.notificationChannelId(it.id)
                                                )
                                        context.startActivity(intent)
                                    }
                                },
                                enabled = enabled
                            ),
                            text = {
                                CompositionLocalProvider(
                                    *if (!enabled) {
                                        arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                                    } else {
                                        emptyArray()
                                    }
                                ) {
                                    Text(text = stringResource(id = it.nameRes))
                                }
                            },
                            secondaryText = {
                                CompositionLocalProvider(
                                    *if (!enabled) {
                                        arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                                    } else {
                                        emptyArray()
                                    }
                                ) {
                                    Text(text = stringResource(id = it.descriptionRes))
                                }
                            }
                        )
                    }
            }
        }
    }
}
