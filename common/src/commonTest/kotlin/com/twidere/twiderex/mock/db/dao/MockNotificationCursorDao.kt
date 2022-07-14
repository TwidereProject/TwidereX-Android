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
package com.twidere.twiderex.mock.db.dao

import com.twidere.twiderex.db.dao.NotificationCursorDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.model.paging.NotificationCursor
import org.jetbrains.annotations.TestOnly

internal class MockNotificationCursorDao @TestOnly constructor() : NotificationCursorDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<NotificationCursor>>()
    override suspend fun find(
        accountKey: MicroBlogKey,
        type: NotificationCursorType
    ): NotificationCursor? {
        return fakeDb[accountKey]?.find { it.type == type }
    }

    override suspend fun add(notificationCursor: NotificationCursor) {
        fakeDb[notificationCursor.accountKey]?.let {
            it.removeAll { cursor -> cursor.type == cursor.type }
            it.add(notificationCursor)
        } ?: let { fakeDb[notificationCursor.accountKey] = mutableListOf(notificationCursor) }
    }
}
