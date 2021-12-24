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

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun AccountNotificationChannelDetail(
    enabled: Boolean,
    accountKey: MicroBlogKey,
) {
    val context = LocalContext.current
    NotificationChannelSpec.values().filter { it.grouped }
        .sortedBy { stringResource(res = it.nameRes) }
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
                        Text(text = stringResource(res = it.nameRes))
                    }
                },
                secondaryText = it.descriptionRes?.let {
                    {
                        CompositionLocalProvider(
                            *if (!enabled) {
                                arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                            } else {
                                emptyArray()
                            }
                        ) {
                            Text(text = stringResource(res = it))
                        }
                    }
                }
            )
        }
}
