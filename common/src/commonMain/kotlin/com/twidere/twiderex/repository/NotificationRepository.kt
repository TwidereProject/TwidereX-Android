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
package com.twidere.twiderex.repository

import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.NotificationService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.model.paging.NotificationCursor
import com.twidere.twiderex.model.ui.UiStatus
import java.util.UUID

class NotificationRepository(
    private val database: CacheDatabase,
) {
    suspend fun activities(
        accountKey: MicroBlogKey,
        service: MicroBlogService
    ): List<UiStatus> {
        return when (service) {
            is NotificationService -> {
                val notifications = service.notificationTimeline()
                    .map {
                        it.toUi(accountKey)
                    }
                takeActivities(accountKey, NotificationCursorType.General, notifications)
            }
            else -> {
                if (service is TimelineService) {
                    val mentions = service.mentionsTimeline()
                        .map { it.toUi(accountKey) }
                    takeActivities(accountKey, NotificationCursorType.Mentions, mentions)
                } else {
                    emptyList()
                }
            }
        }
    }

    private suspend fun takeActivities(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
        notifications: List<UiStatus>
    ): List<UiStatus> {
        val currentCursor = findCursor(accountKey, type)
        if (notifications.any() && (currentCursor == null || currentCursor.timestamp < notifications.first().timestamp)) {
            addCursor(
                accountKey = accountKey,
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
                NotificationCursor(
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
