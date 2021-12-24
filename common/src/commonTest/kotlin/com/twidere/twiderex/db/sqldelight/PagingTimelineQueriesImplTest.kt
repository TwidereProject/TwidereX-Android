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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.sqldelight.transform.toDbPagingTimelineWithStatus
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.sqldelight.table.DbPagingTimeline
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PagingTimelineQueriesImplTest : BaseCacheDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    @Test
    fun insert_ReplaceWhenUniqueKeyEquals() = runBlocking {
        val insert = mockIStatus(text = "insert").toPagingTimeline(accountKey = accountKey, pagingKey = "pagingKey").toDbPagingTimelineWithStatus()
        database.pagingTimelineQueries.insert(insert.timeline.copy(isGap = false))
        assertEquals(false, database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = insert.timeline.statusKey).executeAsOne().isGap)
        database.pagingTimelineQueries.insert(insert.timeline.copy(isGap = true))
        assertEquals(true, database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = insert.timeline.statusKey).executeAsOne().isGap)
    }

    @Test
    fun getPagingList_ReturnResultsWithGiveOffsetAndLimitAndOrderBySortIdDesc() = runBlocking {
        val list = mutableListOf<DbPagingTimeline>()
        for (i in 0 until 10) {
            list.add(
                mockIStatus().toPagingTimeline(accountKey, "pagingKey").toDbPagingTimelineWithStatus().timeline.copy(
                    statusKey = MicroBlogKey.valueOf(i.toString()), sortId = i.toLong()
                ),
            )
        }
        database.pagingTimelineQueries.transaction {
            list.forEach { database.pagingTimelineQueries.insert(it) }
        }
        assertEquals(10, database.pagingTimelineQueries.getPagingCount(accountKey = accountKey, pagingKey = "pagingKey").executeAsOne())
        val result = database.pagingTimelineQueries.getPagingList(accountKey = accountKey, pagingKey = "pagingKey", limit = 4, offset = 3).executeAsList()
        assertEquals(4, result.size)
        // Desc
        assertEquals(6, result.first().sortId)
    }

    @Test
    fun getLatest_ReturnsResultsWithGivenPagingKeyAndMaxedBySortId() = runBlocking {
        val list = mutableListOf<DbPagingTimeline>()
        for (i in 0 until 10) {
            list.add(
                mockIStatus().toPagingTimeline(accountKey, "pagingKey").toDbPagingTimelineWithStatus().timeline.copy(
                    statusKey = MicroBlogKey.valueOf(i.toString()), sortId = i.toLong()
                ),
            )
        }
        database.pagingTimelineQueries.transaction {
            list.forEach { database.pagingTimelineQueries.insert(it) }
        }
        assertEquals(
            9,
            database.pagingTimelineQueries.getLatest(
                accountKey = accountKey,
                pagingKey = "pagingKey"
            ).executeAsOneOrNull()?.sortId
        )
    }

    @Test
    fun clearAll_DeleteAllPagingTimelineWithGivenAccountKeyAndPagingKey() = runBlocking {
        val list = mutableListOf<DbPagingTimeline>()
        for (i in 0 until 10) {
            list.add(
                mockIStatus().toPagingTimeline(accountKey, "pagingKey").toDbPagingTimelineWithStatus().timeline.copy(
                    statusKey = MicroBlogKey.valueOf(i.toString()), sortId = i.toLong()
                ),
            )
        }
        list.add(mockIStatus().toPagingTimeline(accountKey, "otherKey").toDbPagingTimelineWithStatus().timeline)
        database.pagingTimelineQueries.transaction {
            list.forEach { database.pagingTimelineQueries.insert(it) }
        }
        database.pagingTimelineQueries.clearAll(
            accountKey = accountKey,
            pagingKey = "pagingKey"
        )

        assertEquals(0, database.pagingTimelineQueries.getPagingCount(accountKey = accountKey, pagingKey = "pagingKey").executeAsOneOrNull())
        assertEquals(1, database.pagingTimelineQueries.getPagingCount(accountKey = accountKey, pagingKey = "otherKey").executeAsOneOrNull())
    }

    @Test
    fun delete_DeletePagingTimelineWithGivenStatusKey(): Unit = runBlocking {
        val delete = mockIStatus().toPagingTimeline(accountKey = accountKey, pagingKey = "pagingKey").toDbPagingTimelineWithStatus()
        val other = mockIStatus().toPagingTimeline(accountKey = accountKey, pagingKey = "pagingKey").toDbPagingTimelineWithStatus()
        database.pagingTimelineQueries.insert(delete.timeline)
        database.pagingTimelineQueries.insert(other.timeline)
        assertNotNull(database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = delete.timeline.statusKey).executeAsOneOrNull())
        assertNotNull(database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = other.timeline.statusKey).executeAsOneOrNull())
        database.pagingTimelineQueries.delete(statusKey = delete.timeline.statusKey)
        assertNull(database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = delete.timeline.statusKey).executeAsOneOrNull())
        assertNotNull(database.pagingTimelineQueries.findWithStatusKey(accountKey = accountKey, statusKey = other.timeline.statusKey).executeAsOneOrNull())
    }
}
