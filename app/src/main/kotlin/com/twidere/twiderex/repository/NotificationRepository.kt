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
                val currentCursor = findCursor(account.accountKey, NotificationCursorType.General)
                if (notifications.any()) {
                    addCursor(
                        accountKey = account.accountKey,
                        type = NotificationCursorType.General,
                        value = notifications.first().statusId
                    )
                }
                if (currentCursor != null) {
                    notifications.takeWhile { it.statusId != currentCursor.value }
                } else {
                    emptyList()
                }
            }
            else -> {
                if (service is TimelineService) {
                    val mentions = service.mentionsTimeline()
                        .map { it.toDbStatusWithReference(account.accountKey) }
                        .map { it.toUi(account.accountKey) }
                    val currentCursor = findCursor(account.accountKey, NotificationCursorType.Mentions)
                    if (mentions.any()) {
                        addCursor(
                            accountKey = account.accountKey,
                            type = NotificationCursorType.Mentions,
                            value = mentions.first().statusId,
                        )
                    }
                    if (currentCursor != null) {
                        mentions.takeWhile { it.statusId != currentCursor.value }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
        }
    }

    suspend fun findCursor(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
    ) = database.notificationCursorDao().find(
        accountKey = accountKey,
        type = type,
    )

    suspend fun addCursor(
        accountKey: MicroBlogKey,
        type: NotificationCursorType,
        value: String,
    ) {
        database.notificationCursorDao()
            .add(
                DbNotificationCursor(
                    _id = UUID.randomUUID().toString(),
                    accountKey = accountKey,
                    type = type,
                    value = value,
                )
            )
    }
}
