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
package com.twidere.twiderex.jobs.common

import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.enums.MastodonStatusType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.NotificationRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class NotificationJob(
    private val repository: NotificationRepository,
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
    private val resLoader: ResLoader,
) {
    suspend fun execute() = coroutineScope {
        accountRepository.getAccounts()
            .filter {
                it.preferences.isNotificationEnabled.first()
            }
            .map { account ->
                launch {
                    val activities = try {
                        repository.activities(
                            accountKey = account.accountKey,
                            service = account.service
                        )
                    } catch (e: Throwable) {
                        // Ignore any exception cause there's no needs ot handle it
                        emptyList()
                    }
                    activities.forEach { status ->
                        notify(account, status)
                    }
                }
            }.joinAll()
    }

    private suspend fun notify(account: AccountDetails, status: UiStatus) {
        val notificationId = "${account.accountKey}_${status.statusKey}"
        val builder = AppNotification.Builder(
            account.accountKey.notificationChannelId(
                NotificationChannelSpec.ContentInteractions.id
            )
        )

        val notificationData = when (status.platformType) {
            PlatformType.Twitter -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_mentions,
                        status.user.displayName
                    ),
                    htmlContent = status.htmlText,
                    deepLink = RootDeepLinks.Twitter.Status(status.statusId),
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
                builder.setContentText(notificationData.htmlContent)
            }
            if (notificationData.deepLink != null) {
                builder.setDeepLink(notificationData.deepLink)
            }
            if (notificationData.profileImage != null) {
                builder.setLargeIcon(notificationData.profileImage)
            }
            notificationManager.notify(notificationId.hashCode(), builder.build())
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
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_follow,
                        actualStatus.user.displayName
                    ),
                    deepLink = RootDeepLinks.User(actualStatus.user.userKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationFollowRequest -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_follow_request,
                        actualStatus.user.displayName
                    ),
                    deepLink = RootDeepLinks.User(actualStatus.user.userKey)
                )
            }
            MastodonStatusType.NotificationMention -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_mentions,
                        actualStatus.user.displayName
                    ),
                    htmlContent = actualStatus.htmlText,
                    deepLink = RootDeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationReblog -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_reblog,
                        actualStatus.user.displayName
                    ),
                    deepLink = RootDeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationFavourite -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_favourite,
                        actualStatus.user.displayName
                    ),
                    deepLink = RootDeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationPoll -> {
                NotificationData(
                    title = resLoader.getString(
                        com.twidere.twiderex.MR.strings.common_notification_poll,
                    ),
                    deepLink = RootDeepLinks.Status(actualStatus.statusKey),
                    profileImage = actualStatus.user.profileImage,
                )
            }
            MastodonStatusType.NotificationStatus -> null
        }
    }
}

data class NotificationData(
    val title: String,
    val profileImage: String? = null,
    val htmlContent: String? = null,
    val deepLink: String? = null,
)
