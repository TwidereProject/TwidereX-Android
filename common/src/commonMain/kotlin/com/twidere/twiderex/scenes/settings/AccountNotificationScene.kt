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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.ColoredSwitch
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.AccountNotificationViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountNotificationScene(
    accountKey: MicroBlogKey,
) {
    val viewModel: AccountNotificationViewModel = getViewModel {
        parametersOf(accountKey)
    }
    val account by viewModel.account.observeAsState(initial = null)
    val enabled by viewModel.isNotificationEnabled.observeAsState(initial = true)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_title))
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
                            UserName(user = it.toUi())
                        },
                        secondaryText = {
                            UserScreenName(user = it.toUi())
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
                AccountNotificationChannelDetail(
                    enabled = enabled,
                    accountKey = accountKey,
                )
            }
        }
    }
}

@Composable
expect fun AccountNotificationChannelDetail(
    enabled: Boolean,
    accountKey: MicroBlogKey,
)
