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
package com.twidere.twiderex.repository

import com.twidere.services.microblog.NotificationService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbStatusWithReference
import com.twidere.twiderex.db.model.DbNotificationCursor
import com.twidere.twiderex.db.model.NotificationCursorType
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import java.util.UUID

class NotificationRepository(
    private val database: CacheDatabase,
) {
    suspend fun activities(
        account: AccountDetails,
    ): List<UiStatus> {
        return when (val service = account.service) {
            is NotificationService -> {
                val notifications = service.notificationTimeline()
                    .map { it.toDbStatusWithReference(account.accountKey) }
                    .map { it.toUi(account.accountKey) }
                takeActivities(account, NotificationCursorType.General, notifications)
            }
            else -> {
                if (service is TimelineService) {
                    val mentions = service.mentionsTimeline()
                        .map { it.toDbStatusWithReference(account.accountKey) }
                        .map { it.toUi(account.accountKey) }
                    takeActivities(account, NotificationCursorType.Mentions, mentions)
                } else {
                    emptyList()
                }
            }
        }
    }

    private suspend fun takeActivities(
        account: AccountDetails,
        type: NotificationCursorType,
        notifications: List<UiStatus>
    ): List<UiStatus> {
        val currentCursor = findCursor(account.accountKey, type)
        if (notifications.any() && (currentCursor == null || currentCursor.timestamp < notifications.first().timestamp)) {
            addCursor(
                accountKey = account.accountKey,
                type = type,
                value = notifications.first().statusId,
                timestamp = notifications.first().timestamp,
            )
        }
        return if (currentCursor != null) {
            notifications.takeWhile { it.statusId != currentCursor.value && it.timestamp > currentCursor.timestamp }
        } else {
            emptyList()
        }
    }

    suspend fun findCursor(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
    ) = database.notificationCursorDao().find(
        accountKey = accountKey,
        type = type,
    )

    private suspend fun addCursor(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
        value: String,
        timestamp: Long,
    ) {
        database.notificationCursorDao()
            .add(
                DbNotificationCursor(
                    _id = UUID.randomUUID().toString(),
                    accountKey = accountKey,
                    type = type,
                    value = value,
                    timestamp = timestamp,
                )
            )
    }

    suspend fun addCursorIfNeeded(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
        statusId: String,
        timestamp: Long
    ) {
        val current = findCursor(accountKey = accountKey, type = type)
        if (current == null || current.timestamp < timestamp) {
            addCursor(
                accountKey,
                type,
                statusId,
                timestamp
            )
        }
    }
}
