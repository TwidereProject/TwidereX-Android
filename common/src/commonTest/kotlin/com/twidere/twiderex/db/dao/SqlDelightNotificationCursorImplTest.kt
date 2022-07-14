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
package com.twidere.twiderex.db.dao

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightNotificationCursorDaoImpl
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.model.paging.NotificationCursor
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SqlDelightNotificationCursorImplTest : BaseCacheDatabaseTest() {

    @Test
    fun find_transformToUiData() = runBlocking {
        val accountKey = MicroBlogKey.twitter("account")
        val cursor = NotificationCursor(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            type = NotificationCursorType.Mentions,
            value = "value",
            timestamp = System.currentTimeMillis()
        )
        val dao = SqlDelightNotificationCursorDaoImpl(database.notificationCursorQueries)
        dao.add(cursor)
        assertEquals(cursor, dao.find(accountKey = accountKey, type = cursor.type))
    }
}
