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
package com.twidere.twiderex.db

import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.model.paging.NotificationCursor
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class NotificationCursorDaoImplTest : CacheDatabaseDaoTest() {
    private fun generateCursor(
        type: NotificationCursorType,
        accountKey: MicroBlogKey
    ) = NotificationCursor(
        _id = UUID.randomUUID().toString(),
        accountKey = accountKey,
        type = type,
        value = type.name,
        timestamp = System.currentTimeMillis()
    )

    @Test
    fun addAndFindCursorWithCorrectType(): Unit = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val accountKey = MicroBlogKey.twitter("test")
        val generalCursor = generateCursor(NotificationCursorType.General, accountKey)
        val mentionsCursor = generateCursor(NotificationCursorType.Mentions, accountKey)
        val followerCursor = generateCursor(NotificationCursorType.Follower, accountKey)
        cacheDatabase.withTransaction {
            cacheDatabase.notificationCursorDao().apply {
                add(generalCursor)
                add(mentionsCursor)
                add(followerCursor)
            }
        }
        cacheDatabase.notificationCursorDao().apply {
            assertEquals(generalCursor._id, find(accountKey = accountKey, type = NotificationCursorType.General)?._id)
            assertEquals(mentionsCursor._id, find(accountKey = accountKey, type = NotificationCursorType.Mentions)?._id)
            assertEquals(followerCursor._id, find(accountKey = accountKey, type = NotificationCursorType.Follower)?._id)
        }
    }
}
