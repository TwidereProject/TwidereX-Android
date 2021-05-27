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
package com.twidere.twiderex.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.twiderex.R
import com.twidere.twiderex.component.status.normalizeHtmlText
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.DeepLinks
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: NotificationRepository,
    private val accountRepository: AccountRepository,
    private val notificationManagerCompat: NotificationManagerCompat,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = coroutineScope {
        accountRepository.getAccounts().map { accountRepository.getAccountDetails(it) }
            .map { account ->
                launch {
                    runCatching {
                        val activities = repository.activities(account)
                        activities.forEach { status ->
                            notify(account, status)
                        }
                    }.onFailure {
                    }
                }
            }.joinAll()
        Result.success()
    }

    private fun notify(account: AccountDetails, status: UiStatus) {
        val notificationId = "${account.accountKey}_${status.statusKey}"
        val builder = NotificationCompat
            .Builder(
                applicationContext,
                account.accountKey.notificationChannelId(
                    NotificationChannelSpec.ContentInteractions.id
                )
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        when (status.platformType) {
            PlatformType.Twitter -> {
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(DeepLinks.Twitter.Status(status.statusId))
                    )
                val pendingIntent =
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                builder.setContentTitle("${status.user.screenName} just mentions you")
                    .setContentTitle(status.htmlText.normalizeHtmlText())
                    .setContentIntent(pendingIntent)
            }
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> {
                updateMastodonNotificationBuilder(builder, status)
            }
        }
        notificationManagerCompat.notify(notificationId.hashCode(), builder.build())
    }

    private fun updateMastodonNotificationBuilder(
        builder: NotificationCompat.Builder,
        status: UiStatus
    ) {
        if (status.mastodonExtra == null) {
            return
        }
        when (status.mastodonExtra.type) {
            MastodonStatusType.Status -> Unit
            MastodonStatusType.NotificationFollow -> {
                builder
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.common_notification_follow,
                            status.user.displayName
                        )
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.User(status.user.userKey)),
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationFollowRequest -> {
                builder
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.common_notification_follow_request,
                            status.user.displayName
                        )
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.User(status.user.userKey)),
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationMention -> {
                builder.setContentTitle("${status.user.screenName} just mentions you")
                    .setContentTitle(status.htmlText.normalizeHtmlText())
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.Status(status.statusKey))
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationReblog -> {
                builder
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.common_notification_reblog,
                            status.user.displayName
                        )
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.Status(status.statusKey))
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationFavourite -> {
                builder
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.common_notification_favourite,
                            status.user.displayName
                        )
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.Status(status.statusKey))
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationPoll -> {
                builder
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.common_notification_poll,
                        )
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DeepLinks.Status(status.statusKey))
                            ),
                            PendingIntent.FLAG_MUTABLE,
                        )
                    )
            }
            MastodonStatusType.NotificationStatus -> Unit
        }
    }
}
