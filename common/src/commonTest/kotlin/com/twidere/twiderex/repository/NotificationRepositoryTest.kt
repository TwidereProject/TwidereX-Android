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

import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.service.MockNotificationService
import com.twidere.twiderex.mock.service.MockTimelineService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.NotificationCursorType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class NotificationRepositoryTest {

    @Test
    fun notification_AddLatestNotificationCursorAfterSuccess() = runBlocking {
        val repo = NotificationRepository(MockCacheDatabase())
        val service = MockNotificationService()
        val accountKey = MicroBlogKey.twitter("test")
        var list = repo.activities(
            accountKey = accountKey,
            service = service
        )
        assertNotNull(repo.findCursor(accountKey, NotificationCursorType.General))
        assert(list.isEmpty())
        list = repo.activities(
            accountKey = accountKey,
            service = service
        )
        assert(list.isNotEmpty())
        assertEquals(list.first().statusId, repo.findCursor(accountKey, NotificationCursorType.General)?.value)
    }

    @Test
    fun mention_AddLatestNotificationCursorAfterSuccess() = runBlocking {
        val repo = NotificationRepository(MockCacheDatabase())
        val service = MockTimelineService()
        val accountKey = MicroBlogKey.twitter("test")
        var list = repo.activities(
            accountKey = accountKey,
            service = service
        )
        assertNotNull(repo.findCursor(accountKey, NotificationCursorType.Mentions))
        assert(list.isEmpty())
        list = repo.activities(
            accountKey = accountKey,
            service = service
        )
        assert(list.isNotEmpty())
        assertEquals(list.first().statusId, repo.findCursor(accountKey, NotificationCursorType.Mentions)?.value)
    }
}
