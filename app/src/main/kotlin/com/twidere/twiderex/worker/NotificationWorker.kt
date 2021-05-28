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
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.HtmlCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.ReferenceType
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
                    val activities = try {
                        repository.activities(account)
                    } catch (e: Throwable) {
                        // Ignore any exception cause there's no needs ot handle it
                        emptyList()
                    }
                    activities.forEach { status ->
                        notify(account, status)
                    }
                }
            }.joinAll()
        Result.success()
    }

    private suspend fun notify(account: AccountDetails, status: UiStatus) {
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
            .setAutoCancel(true)

        val notificationData = when (status.platformType) {
            PlatformType.Twitter -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_mentions,
                        status.user.displayName
                    ),
                    htmlContent = status.htmlText,
                    deepLink = DeepLinks.Twitter.Status(status.statusId),
                    profileImage = status.user.profileImage,
                )
            }
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> {
                generateMastodonNotificationData(
                    status,
                )
            }
        }
        if (notificationData != null) {
            builder.setContentTitle(notificationData.title)
            if (notificationData.htmlContent != null) {
                val html = HtmlCompat.fromHtml(
                    notificationData.htmlContent,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                builder
                    .setContentText(html)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(html)
                    )
            }
            if (notificationData.deepLink != null) {
                builder.setContentIntent(
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        Intent(Intent.ACTION_VIEW, Uri.parse(notificationData.deepLink)),
                        PendingIntent.FLAG_MUTABLE
                    )
                )
            }
            if (notificationData.profileImage != null) {
                val result = Coil.execute(
                    ImageRequest.Builder(applicationContext)
                        .data(notificationData.profileImage)
                        .build()
                )
                if (result is SuccessResult) {
                    builder.setLargeIcon(result.drawable.toBitmap())
                }
            }
            notificationManagerCompat.notify(notificationId.hashCode(), builder.build())
        }
    }

    private fun generateMastodonNotificationData(
        status: UiStatus
    ): NotificationData? {
        val actualStatus = status.referenceStatus[ReferenceType.MastodonNotification]
        if (status.mastodonExtra == null || actualStatus == null) {
            return null
        }
        return when (status.mastodonExtra.type) {
            MastodonStatusType.Status -> null
            MastodonStatusType.NotificationFollow -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_follow,
                        actualStatus.user.displayName
                    ),
                    deepLink = DeepLinks.User(actualStatus.user.userKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationFollowRequest -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_follow_request,
                        actualStatus.user.displayName
                    ),
                    deepLink = DeepLinks.User(actualStatus.user.userKey)
                )
            }
            MastodonStatusType.NotificationMention -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_mentions,
                        actualStatus.user.displayName
                    ),
                    htmlContent = actualStatus.htmlText,
                    deepLink = DeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationReblog -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_reblog,
                        actualStatus.user.displayName
                    ),
                    deepLink = DeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationFavourite -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_favourite,
                        actualStatus.user.displayName
                    ),
                    deepLink = DeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationPoll -> {
                NotificationData(
                    title = applicationContext.getString(
                        R.string.common_notification_poll,
                    ),
                    deepLink = DeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationStatus -> null
        }
    }
}

private data class NotificationData(
    val title: String,
    val profileImage: Any? = null,
    val htmlContent: String? = null,
    val deepLink: String? = null,
)
